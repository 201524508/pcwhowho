package com.example.yuuuuu.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by s0woo on 2017-08-23.
 */

public class PlayerDetailActivity extends Activity {

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    String name, sport, nation, time;

    byte[] buffer;
    Bitmap bmp;

    public ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.player_detail_popup);

        final TextView txtName= (TextView)findViewById(R.id.playerName);
        final TextView txtNation = (TextView)findViewById(R.id.playerNation);
        final TextView txtSport = (TextView)findViewById(R.id.playerSport);
        final TextView txtTime = (TextView)findViewById(R.id.playerTime);


        //클릭된 선수 데이터 가져오기
        name= getIntent().getStringExtra("name");
        sport = getIntent().getStringExtra("sport");
        nation = getIntent().getStringExtra("nation");

        cubrid();
        progressBarDialog();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run() {
                //가져온 정보 위치에 맞춰 각각의 textView에 출력
                txtName.setText(name);
                txtSport.setText(sport);
                txtNation.setText(nation);
                txtTime.setText(time);

                if(buffer == null) {
                    System.out.println("************byte array : null");
                    //cubrid DB에 사진이 등록되지 않은 사람이라면 기본사진으로 설정
                    ImageView imgPic = (ImageView)findViewById(R.id.viewPicture);
                    imgPic.setImageResource(R.drawable.no_player);
                }
                else {
                    System.out.println("************byte array : " + buffer.length);
                    //cubrid DB에 사진이 등록된 사람이라면 그 사람의 사진을 보여줌
                    bmp = BitmapFactory.decodeByteArray(buffer,0, buffer.length);

                    ImageView imgPic = (ImageView)findViewById(R.id.viewPicture);
                    imgPic.setImageBitmap(bmp);
                }
            }
        }, 1500);

    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    void cubrid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
                    String jdbcUrl = "jdbc:cubrid:192.168.0.5:30000:sample:::?charset=UTF-8";

                    conn = DriverManager.getConnection(jdbcUrl, "dba", "1234");

                    if(name.equals("심영우") || name.equals("유아정") || name.equals("조유나")) {
                        String sql = "SELECT picture, playtime FROM info WHERE name = \'" + name + "\' AND nation = \'" + nation + "\' AND sport = \'" + sport + "\' LIMIT 1";

                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        System.out.println("sql=============== : " + sql);

                        if(rs.next()) {
                            time = rs.getString("playtime");
                            Blob blob = rs.getBlob("picture");
                            buffer = blob.getBytes(1, (int)blob.length());
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@2" + time + " " + buffer.length);
                        }

                    }
                    else {
                        String sql = "SELECT playtime FROM info WHERE name = \'" + name + "\' AND nation = \'" + nation + "\' AND sport = \'" + sport + "\' LIMIT 1";

                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                        //System.out.println("sql=============== : " + sql);

                        if(rs.next()) {
                            time = rs.getString("playtime");
                        }
                        //System.out.println("@@@@@@@@@@@@@@@@@@@@@2" + time);
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

    public Handler mhandler = new Handler();
    public void progressBarDialog() {
        progressDialog = new ProgressDialog(PlayerDetailActivity.this);

        progressDialog.setMessage("선수 정보를 출력중입니다.");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        //progressDialog.setProgress(0);
        progressDialog.show();

        mhandler.postDelayed( new Runnable() {
            @Override public void run() {
                try {
                    if (progressDialog!=null&&progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }

}
