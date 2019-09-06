package com.biao.facialrecognition.baidu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.biao.facialrecognition.R;
import com.biao.facialrecognition.YuvUtil;
import com.biao.facialrecognition.model.FaceQueryBen;
import com.biao.facialrecognition.model.FacetoFaceBean;
import com.biao.facialrecognition.model.GetTonken;
import com.biao.facialrecognition.preference.PreferencesKey;
import com.biao.facialrecognition.preference.SharedPreferencesUtil;
import com.biao.facialrecognition.retrofit.BaseObserver;
import com.biao.facialrecognition.retrofit.RetrofitHelper;
import com.biao.facialrecognition.retrofit.RetrofitMap;
import com.biao.facialrecognition.retrofit.RxUtil;
import com.biao.facialrecognition.utils.LogUtil;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class FacielBaiduActivity extends AppCompatActivity implements Camera.PreviewCallback {

    private static final String TAG = "FacielBaiduActivity";
    private static final CompositeDisposable composite = new CompositeDisposable();//网络请求管理
    @BindView(R.id.tv_tip1)
    TextView tvTip;
    @BindView(R.id.sv_tip1)
    ScrollView svTip;
    @BindView(R.id.sv_facial1)
    SurfaceView svFacial;
    @BindView(R.id.tv_succeed_tip1)
    TextView tvSucceedTip;
    @BindView(R.id.iv_card_photo1)
    ImageView ivCardPhoto;

    private Camera camera;
    private Bitmap bitmap;
    private int num = 0;//成功次数
    private SurfaceHolder surfaceholder;
    private int j = 0;
    private boolean isClose = false;//标识摄像头是否关闭
    private int width = 640, height = 480;//相机的宽高
    private boolean isCompare = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            openCamera();
        }
    };

    private SharedPreferencesUtil preferencesUtil;

    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            tvSucceedTip.setVisibility(View.GONE);
            openCamera();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faciel_baidu);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        preferencesUtil = SharedPreferencesUtil.getIntent(this);
        String path = Environment.getExternalStorageDirectory() + "/tmp.bmp";
        bitmap = getBitmap(path);
        ivCardPhoto.setImageBitmap(bitmap);
        surfaceholder = svFacial.getHolder();
        handler.postDelayed(runnable, 100);

        getTonken();
    }

    /**
     * 获取指定路径图片
     *
     * @param path
     * @return
     */
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
//                    parameters.setJpegQuality(50);  //设置图片质量
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

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (!isClose && bitmap != null && !isCompare && preferencesUtil.getString(PreferencesKey.BAIDU_TONKEN) != null) {
            Log.i(TAG, "处理上传照片");
            byte[] bytes1 = YuvUtil.nv21ConvertBitmap(bytes);
//            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length);
//            ivCardPhoto.setImageBitmap(bitmap1);
            isCompare = true;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String imageIDCard = Base64Util.encode(data);
            String imageFace = Base64Util.encode(bytes1);
            List<FaceQueryBen> faceQueryBens = new ArrayList<>();
            FaceQueryBen faceQueryBen = new FaceQueryBen(imageIDCard, "BASE64", "LIVE",
                    "LOW", "NORMAL");
            FaceQueryBen faceQueryBen1 = new FaceQueryBen(imageFace, "BASE64", "LIVE",
                    "LOW", "NORMAL");

            faceQueryBens.add(faceQueryBen);
            faceQueryBens.add(faceQueryBen1);

            faceTOFace(faceQueryBens);
        }
    }

    /**
     * 获取Tonken
     */
    private void getTonken() {
        RetrofitHelper.getInstance().getRetrofitInterface()
                .getTonken(RetrofitMap.getTanken())
                .compose(RxUtil.<GetTonken>rxIoToMain())
                .subscribe(new BaseObserver<GetTonken>(composite) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(GetTonken getTonken) {
                        preferencesUtil.putString(PreferencesKey.BAIDU_TONKEN, getTonken.getAccess_token());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 人证比对
     *
     * @param faceQueryBens
     */
    private void faceTOFace(final List<FaceQueryBen> faceQueryBens) {
        RetrofitHelper.getInstance().getRetrofitInterface()
                .faceToFace(preferencesUtil.getString(PreferencesKey.BAIDU_TONKEN), faceQueryBens)
                .compose(RxUtil.<FacetoFaceBean>rxIoToMain())
                .subscribe(new BaseObserver<FacetoFaceBean>(composite) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(FacetoFaceBean facetoFaceBean) {
                        LogUtil.d(facetoFaceBean.toString());
                        if (facetoFaceBean.getError_code() == 0) {
                            LogUtil.i("对比得分：" + facetoFaceBean.getResult().getScore());
                            if (facetoFaceBean.getResult().getScore() > 60) {
                                LogUtil.i("识别成功");
                                tvSucceedTip.setVisibility(View.VISIBLE);
                                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
                                tvTip.append("\n" + format.format(System.currentTimeMillis()) + "：第" + ++num + "次成功识别！");
                                svTip.fullScroll(ScrollView.FOCUS_DOWN);
                                handler.postDelayed(runnable1, 5000);
                                cloesCamera();
                                return;
                            }
                        }
                        LogUtil.e("识别失败");
                    }

                    @Override
                    public void onError(Throwable e) {
                        isCompare = false;
                        LogUtil.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        isCompare = false;
                    }
                });
    }

    private void faceToFace(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
                    String result = HttpUtil.post(url, preferencesUtil.getString(PreferencesKey.BAIDU_TONKEN),
                            "application/json", json);
                    LogUtil.e(result);
                    isCompare = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        composite.clear();
        cloesCamera();
    }
}
