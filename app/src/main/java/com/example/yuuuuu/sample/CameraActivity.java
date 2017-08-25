package com.example.yuuuuu.sample;

/**
 * Created by YUUUUU on 2017-07-10
 * Created by s0woo on 2017-08-24
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private Mat img_input;  //카메라 프리뷰
    private Mat img_result; //카메라 프리뷰 + 정사각형 + 아이콘
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;

    public ImageView pin;   //pin 아이콘
    public ImageView iv;    //팝업창 이미지뷰

    public static native long loadCascade(String cascadeFileName );
    public static native void detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);
    public static native int[] backpnt();   //정사각형 좌표
    public static native int facecnt(); //인식된 얼굴 수
    public static native double[] backrct();    //정사각형 가로, 세로 길이
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/optimized_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/output_labels.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private String result;
    private Bitmap bitmap;

    byte[] buffer;
    Bitmap bmp;

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    String c_name, c_sport, c_nation;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean camreaAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!camreaAccepted || !writeAccepted )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }else
                        {
                            read_cascade_file();
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(CameraActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pin = (ImageView)findViewById(R.id.pin);
        pin.setOnTouchListener(pin_popup);

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
            read_cascade_file();
        }

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        initTensorFlowAndLoadModel();   //텐서플로우를 이용하기 위해 초기화 및 모델 로드
    }

    private Dialog mdialog;

    //아이콘 터치했을 때 동작하는 부분
    View.OnTouchListener pin_popup = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event){
            Toast.makeText(CameraActivity.this, "잠시만 기다려 주세요.", Toast.LENGTH_LONG).show();

            //인식된 얼굴이 존재하면
            if(faceCnt() != 0){

                Mat m = Crop(); //카메라 프리뷰에서 얼굴을 크롭

                //Crop()에서 return된 이미지가 null이 아니면 팝업창을 띄워줌
                if(m!=null){
                    Bitmap b = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(m, b);

                    String result = tensor(b);  //인식한 인물 중 1순위의 이름을 리턴

                    mdialog = popup(result);
                    mdialog.show();
                }
                else{   //리턴된 이미지가 null인 경우
                    Toast.makeText(CameraActivity.this, "인식이 제대로 되지 않았습니다. 다시 시도해주십시오.", Toast.LENGTH_LONG).show();
                }
            }

            return false;
        }
    };

    /*
     * 텐서플로우 초기화 및 모델 로드
     * MODEL_FILE : 사용할 모델 그래프
     * LABEL_FILE : 사용할 라벨 목록 파일
     */
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    /*
     * 텐서플로우로 학습된 얼굴 사진들과 비교할 얼굴 이미지를 카메라 프리뷰에서 크롭
     * 얼굴 부분에 나타나는 사각형 만큼만 잘라낸 후 return
     * point[0] : x, point[1] : y
     * rect[0] : width, rect[1] : height
     * roi : 크롭할 사각형 위치 및 범위
     */
    public Mat Crop(){
        int[] point;
        double[] rect;
        Mat tmp = img_result;

        point = backPnt();
        rect = backRct();

        Rect roi = new Rect(point[0], point[1], Integer.parseInt(String.valueOf(Math.round(rect[0]))),Integer.parseInt(String.valueOf(Math.round(rect[1]))));

        //open cv의 Mat 형식이 인식할 수 있는 사각형 범위 내에 있어야 크롭을 실행 할 수 있음
        if(0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= tmp.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= tmp.rows()){
            Mat result = tmp.submat(roi);
            return result;
        }

        //조건에 맞지 않으면 null로 return
        return null;
    }

    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    /*
     * 텐서플로우와 모델을 이용해 인식된 인물의 결과 중 1순위의 이름을 RETURN
     * Bitmap b, bitmap : Crop() 함수를 이용해 화면에서 잘라낸 얼굴
     * tmp[0] : 1순위 인물의 이름
     * 학습 자료량의 부족으로 인식률이 50%를 넘었을 때 이름을 가져올 수 있도록 하였음
     */
    public String tensor(Bitmap b){
        bitmap = b;
        if(bitmap!=null){
            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
            result = results.get(0).toString();
            String[] tmp = result.split(" ");
            if(Float.parseFloat(tmp[1]) > 50.0){
                return tmp[0];
            }
        }
        else{
            System.out.println("없다");
        }
        return ".";
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {

    }

    //쓰레드 핸들러
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //투명도 조절을 통해 아이콘이 보이거나 안 보이게 함
            switch(msg.what){
                case 0x10:
                    //투명도 0
                    pin.setAlpha(0);
                    break;
                case 0x20:
                    //투명도 1000
                    pin.setAlpha(1000);
                    break;
            }
        }
    };

    /*
     * 오픈CV를 이용해 카메라 프리뷰에서 얼굴 인식을 진행
     * detect 함수 : native-lib.cpp 의 함수, 얼굴 인식 함수
     * img_input : 카메라를 통해 들어오는 프리뷰 화면
     * img_result : 얼굴이 인식되면 나타나는 사각형까지 표시되는 화면
     * UI를 수정하기 위해 Thread 내부에서 handler를 이용하여 pin 아이콘을 조절
     * setX, setY를 이용해 detect 함수로 받아오는 좌표로 pin 위치를 변경
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        img_input = inputFrame.rgba();

        if ( img_result != null ) img_result.release();
        img_result = new Mat();

        int[] point;

        detect(cascadeClassifier_face,cascadeClassifier_eye, img_input.getNativeObjAddr(), img_result.getNativeObjAddr());

        point = backPnt();

        pin.setX(point[0]);
        pin.setY(point[1]);

        new Thread(){
            int face;
            public void run(){
                face = faceCnt();
                if(face==0){
                    handler.sendMessage(Message.obtain(handler, 0x10));
                }
                else{
                    handler.sendMessage(Message.obtain(handler, 0x20));
                }
            }
        }.start();

        return img_result;
    }

    //아이콘 위치
    public int[] backPnt(){
        int[] tmp;
        tmp = backpnt();

        return tmp;
    }

    //얼굴 수
    public int faceCnt(){
        int tmp;
        tmp = facecnt();

        return tmp;
    }

    //얼굴 사각형 가로, 세로 길이
    public double[] backRct(){
        /*
         * tmp[0] : 가로, tmp[1] : 세로
         */
        double[] tmp;
        tmp = backrct();

        return tmp;
    }

    /*
     * pin 아이콘 터치했을 때 나타나는 팝업
     * bitmap은 Crop() 함수에서 넘어온 이미지, null이 아니다.
     * 팝업창을 띄우기 위해 AlertDialog 사용
     */
    private AlertDialog popup(String name) {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.custom_pin_popup, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(CameraActivity.this).setView(layout);

        if (name.length() != 0) {
            cubrid(name);

            Handler h = new Handler();
            h.postDelayed(new Runnable() {

                TextView txtName= (TextView)layout.findViewById(R.id.textView1);
                TextView txtNation = (TextView)layout.findViewById(R.id.textView2);
                TextView txtSport = (TextView)layout.findViewById(R.id.textView3);

                @Override
                public void run() {

                    if(buffer == null) {
                        System.out.println("************byte array : null");
                        //cubrid DB에 사진이 등록되지 않은 사람이라면 기본사진으로 설정
                        ImageView iv = (ImageView)layout.findViewById(R.id.nullx);
                        iv.setImageResource(R.drawable.no_player);

                        txtName.setText(c_name);
                        txtSport.setText(c_sport);
                        txtNation.setText(c_nation);
                    }
                    else {
                        System.out.println("************byte array : " + buffer.length);
                        //cubrid DB에 사진이 등록된 사람이라면 그 사람의 사진을 보여줌
                        //byte[]를 bitMap으로 변환하고 bitMap을 imageView에 출력

                        System.out.println("&&&&&&" +c_name + " " + c_sport + " "+c_nation + " ");

                        txtName.setText(c_name);
                        txtSport.setText(c_sport);
                        txtNation.setText(c_nation);

                        bmp = BitmapFactory.decodeByteArray(buffer,0, buffer.length);

                        ImageView iv = (ImageView)layout.findViewById(R.id.nullx);
                        iv.setImageBitmap(bmp);
                    }
                }
            }, 1500);
        }
        else {
            ImageView iv = (ImageView)layout.findViewById(R.id.nullx);
            iv.setImageResource(R.drawable.no_player);

            TextView txtSport = (TextView)layout.findViewById(R.id.textView3);
            txtSport.setText("선수 정보가 없습니다.");
        }

        dialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        dialog.setView(layout);

        return dialog.create();
    }


    void cubrid(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
                    String jdbcUrl = "jdbc:cubrid:192.168.0.5:30000:sample:::?charset=UTF-8";

                    conn = DriverManager.getConnection(jdbcUrl, "dba", "1234");

                    if(name.equals("youngwoo") || name.equals("ajeong") || name.equals("yuna")) {
                        String edit_name="";

                        if(name.equals("youngwoo")) {
                            edit_name = "심영우";
                        }
                        else if(name.equals("ajeong")) {
                            edit_name = "유아정";
                        }
                        else if(name.equals("yuna")) {
                            edit_name = "조유나";
                        }

                        String sql = "SELECT picture, name, sport, nation FROM info WHERE name = \'" + edit_name + "\' LIMIT 1";

                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);

                        if(rs.next()) {
                            //Blob 형식으로 DB에 저장되어 있는 정보를 받아와 byte[]에 대입
                            Blob blob = rs.getBlob("picture");
                            buffer = blob.getBytes(1, (int)blob.length());

                            c_name = rs.getString("name");
                            c_sport = rs.getString("sport");
                            c_nation = rs.getString("nation");
                        }
                    }
                    else {
                        c_name = "";
                        c_sport = "선수 정보가 없습니다.";
                        c_nation = "";
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch(NullPointerException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
