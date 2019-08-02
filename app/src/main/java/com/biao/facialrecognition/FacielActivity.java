package com.biao.facialrecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.idcardveri.CompareResult;
import com.arcsoft.idcardveri.DetectFaceResult;
import com.arcsoft.idcardveri.IdCardVerifyError;
import com.arcsoft.idcardveri.IdCardVerifyListener;
import com.arcsoft.idcardveri.IdCardVerifyManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

public class FacielActivity extends AppCompatActivity implements Camera.PreviewCallback {

    private static final String TAG = "FacielActivity";
    public static final String APP_ID = "FwBJ6hHCzpdKN4cZkp3ig9Rp2HXG5448WdsQ1tMcvqW8";
    public static final String SDK_KEY = "Fj2ijgPyMWo8VibazsaLHcfjhQe54jZ6B3Bwqu8g6NXF";
    private boolean isIdCardReady = false;
    private boolean isCurrentReady = false;
    private int width = 1600, height = 1200;//相机的宽高
    //比对阈值，建议为0.82
    private static final double THRESHOLD = 0.82d;
    private Bitmap bitmap;
    private Camera camera;
    private int j = 0;
    private SurfaceHolder surfaceholder;
    private TextView succeedTip, tvTip;
    private SurfaceView sv_preview;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            openCamera();
        }
    };
    private ImageView imageView;

    private IdCardVerifyListener idCardVerifyListener = new IdCardVerifyListener() {
        @Override
        public void onPreviewResult(DetectFaceResult detectFaceResult, byte[] bytes, int i, int i1) {
            if (detectFaceResult.getErrCode() == IdCardVerifyError.OK) {
                isCurrentReady = true;
                compare();
            }
        }

        @Override
        public void onIdCardResult(DetectFaceResult detectFaceResult, byte[] bytes, int i, int i1) {
            if (detectFaceResult.getErrCode() == IdCardVerifyError.OK) {
                isIdCardReady = true;
                compare();
            }
        }
    };
    private boolean isClose = false;//标识摄像头是否关闭
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_recognition);

        sv_preview = findViewById(R.id.sv_facial);
        imageView = findViewById(R.id.iv_card_photo);
        succeedTip = findViewById(R.id.tv_succeed_tip);
        tvTip = findViewById(R.id.tv_tip);

        initData();
    }

    private void initData() {
        String path = Environment.getExternalStorageDirectory() + "/tmp.bmp";
        bitmap = getBitmap(path);
        imageView.setImageBitmap(bitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initHonRuan();
            }
        }).start();
        surfaceholder = sv_preview.getHolder();
        handler.postDelayed(runnable, 100);
    }


    private void openCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        Log.i("摄像头个数", cameraCount + "");
        if (cameraCount >= 1 && camera == null) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            if (camera != null) {
                try {
                    camera.setErrorCallback(callback);
                    Camera.Parameters parameters = camera.getParameters();
//                    List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//                    List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
//                    for (Camera.Size s : previewSizes) {
//                        Log.i(TAG, "preview:" + s.width + "-----" + s.height);
//                    }
//                    for (Camera.Size s : pictureSizes) {
//                        Log.i(TAG, "picture:" + s.width + "-----" + s.height);
//                    }
//                    parameters.setJpegQuality(100);  //设置图片质量
                    parameters.setPictureSize(width, height);
                    parameters.setPreviewSize(width, height);
                    parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(surfaceholder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                    camera.setPreviewCallback(this);
                    isClose = false;
//                    camera.setFaceDetectionListener(this);
                } catch (Exception e) {
                    j++;
                    if (j < 5) {
                        cloesCamera();
                        openCamera();
                    } else {
                        Log.i(TAG, "打开相机失败catch");
                    }
                }
            } else {
                Log.i(TAG, "打开相机1失败");
                j++;
                if (j < 5) {
                    openCamera();
                } else {
                    Log.i(TAG, "打开相机失败else");
                }
            }
        }
    }

    //监听相机服务是否终止或者出现其他异常
    Camera.ErrorCallback callback = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            Log.d("onError", "to do something");
            cloesCamera();
            openCamera();
        }
    };

    private void cloesCamera() {
        isClose = true;
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private Bitmap getBitmap(String path) {
        FileInputStream fs = null;
        Bitmap bitmap = null;
        try {
            fs = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 虹软人证对比注册
     */
    private void initHonRuan() {
        int initResult = IdCardVerifyManager.getInstance().init(this, idCardVerifyListener);
        Log.e(TAG, "init result: " + initResult);
        if (initResult == IdCardVerifyError.OK) {
            inputIcCard();
            return;
        }
        int activeResult = IdCardVerifyManager.getInstance().active(this, APP_ID, SDK_KEY);
        Log.i(TAG, "active result: " + activeResult);
        if (activeResult == IdCardVerifyError.OK) {
            int initResult1 = IdCardVerifyManager.getInstance().init(this, idCardVerifyListener);
            Log.e(TAG, "init result1: " + initResult1);
            if (initResult1 == IdCardVerifyError.OK) {
                inputIcCard();
            }
        }
    }

    private void inputIcCard() {
        //宽度4的倍数
        int width = (bitmap.getWidth() / 4) * 4;
        //图片数据高（2的倍数）
        int height = (bitmap.getHeight() / 2) * 2;
        //身份证数据 根据实际数据输入
        Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, width, height, false);
        byte[] bytes = YuvUtil.getNV21(newBmp);
        DetectFaceResult result = IdCardVerifyManager.getInstance().inputIdCardData(bytes, width, height);//传入身份证照片
        Log.e(TAG, "inputIdCardData result: " + result.getErrCode());
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!isClose) {
            DetectFaceResult result = IdCardVerifyManager.getInstance().onPreviewData(data, width, height, true);
            if (result.getErrCode() != IdCardVerifyError.OK) {
                Log.i(TAG, "onPreviewData video result: " + result.getErrCode());
            }
        }
    }

    private void compare() {
        if (!isCurrentReady || !isIdCardReady) {
            return;
        }
        //需要在主线程进行比对
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CompareResult compareResult = IdCardVerifyManager.getInstance().compareFeature(THRESHOLD);
                isCurrentReady = false;
                isIdCardReady = false;
                if (!compareResult.isSuccess()) {
                    Log.i(TAG, "人脸识别失败");
                    inputIcCard();
                } else {
                    Log.i(TAG, "人脸识别成功");
                    succeedTip.setVisibility(View.VISIBLE);
                    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
                    tvTip.append("\n" + format.format(System.currentTimeMillis()) + "：第" + ++num + "次成功识别！");
                    handler.postDelayed(runnable1, 5000);
                    cloesCamera();
                }
            }
        });
    }

    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            succeedTip.setVisibility(View.GONE);
            inputIcCard();
            openCamera();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cloesCamera();
        handler.removeCallbacks(runnable1);
        IdCardVerifyManager.getInstance().unInit();
    }
}
