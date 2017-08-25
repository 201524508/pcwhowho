#include <jni.h>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <string>
#include <sstream>


using namespace cv;

using namespace std;

extern "C" {

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}

int tmppo[2];   //사각형 좌표
int fc; //인식된 얼굴 수
double tmpR[2]; //사각형 가로, 세로

/*
 * 인식된 사각형의 좌표
 * point[0] : x, point[1] : y
 */
void backPoint(int point[2]){
    tmppo[0] = point[0];
    tmppo[1] = point[1];
}

/*
 * 인식된 사각형의 가로, 세로
 * tmpR[0] : 가로, tmpR[1] : 세로
 */
void backRect(double tmp[2]){
    tmpR[0] = tmp[0];
    tmpR[1] = tmp[1];
}

//인식된 얼굴의 수
void faceCnt(int cnt){
    fc = cnt;
}

//CameraActivity로 fc를 넘겨주는 함수
JNIEXPORT jint JNICALL
Java_com_example_yuuuuu_sample_CameraActivity_facecnt(JNIEnv *env, jobject){
    jint ji;
    ji = fc;

    return ji;
}

//CameraActivity로 tmpR를 넘겨주는 함수
JNIEXPORT jdoubleArray JNICALL
Java_com_example_yuuuuu_sample_CameraActivity_backrct(JNIEnv *env, jobject){
    jdoubleArray ji;
    double point[2];
    point[0] = tmpR[0];
    point[1] = tmpR[1];

    ji = env->NewDoubleArray(2);

    env->SetDoubleArrayRegion(ji, 0, 2, point);

    return ji;
}

//CameraActivity로 point를 넘겨주는 함수
JNIEXPORT jintArray JNICALL
Java_com_example_yuuuuu_sample_CameraActivity_backpnt(JNIEnv *env, jobject){
    jintArray ji;
    int point[2];
    point[0] = tmppo[0];
    point[1] = tmppo[1];

    ji = env->NewIntArray(2);

    env->SetIntArrayRegion(ji, 0, 2, point);

    return ji;
}

//CameraActivity에서 카메라를 통해 얼굴을 인식하는 함수
JNIEXPORT void JNICALL
Java_com_example_yuuuuu_sample_CameraActivity_detect(JNIEnv *env, jobject,
                                                   jlong cascadeClassifier_face,
                                                   jlong cascadeClassifier_eye,
                                                   jlong addrInput,
                                                   jlong addrResult) {

    int point[2];
    double rect[2];

    Mat &img_input = *(Mat *) addrInput;
    Mat &img_result = *(Mat *) addrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 320);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );


    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());


    faceCnt(faces.size());

    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        //얼굴 그리는 부분
        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);

        rect[0] = real_facesize_width;
        rect[1] = real_facesize_height;
        backRect(rect);

        rectangle(img_result, face_area, Scalar(0, 255, 0), 2, 8, 0);

        point[0] = real_facesize_x;
        point[1] = real_facesize_y;

        backPoint(point);
        backRect(rect);
    }
}


JNIEXPORT jlong JNICALL
Java_com_example_yuuuuu_sample_CameraActivity_loadCascade(JNIEnv *env, jobject,
                                                                               jstring cascadeFileName) {

    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName, JNI_FALSE);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);

    return ret; }
}