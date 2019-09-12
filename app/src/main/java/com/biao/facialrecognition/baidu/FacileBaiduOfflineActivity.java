package com.biao.facialrecognition.baidu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.biao.facialrecognition.MainActivity;
import com.biao.facialrecognition.R;
import com.biao.facialrecognition.YuvUtil;
import com.biao.facialrecognition.model.FaceQueryBen;
import com.biao.facialrecognition.preference.PreferencesKey;
import com.biao.facialrecognition.preference.SharedPreferencesUtil;
import com.biao.facialrecognition.utils.FaceSDKManager;
import com.biao.facialrecognition.utils.LogUtil;
import com.biao.facialrecognition.utils.SingleBaseConfig;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacileBaiduOfflineActivity extends AppCompatActivity implements Camera.PreviewCallback {

    @BindView(R.id.tv_tip2)
    TextView tvTip2;
    @BindView(R.id.sv_tip2)
    ScrollView svTip2;
    @BindView(R.id.sv_facial2)
    SurfaceView svFacial2;
    @BindView(R.id.tv_succeed_tip2)
    TextView tvSucceedTip2;
    @BindView(R.id.iv_card_photo2)
    ImageView ivCardPhoto2;

    private MainActivity mainActivity;
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
            tvSucceedTip2.setVisibility(View.GONE);
            openCamera();
        }
    };
    private boolean firstFeatureFinished = false;
    private boolean secondFeatureFinished = false;
    private byte[] firstFeature = new byte[512];
    private byte[] secondFeature = new byte[512];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facile_baidu_offline);
        ButterKnife.bind(this);

        initData();
        mainActivity = new MainActivity();
    }

    private void initData() {
        preferencesUtil = SharedPreferencesUtil.getIntent(this);
        String path = Environment.getExternalStorageDirectory() + "/tmp2.png";
        bitmap = getBitmap(path);
        ivCardPhoto2.setImageBitmap(bitmap);
        surfaceholder = svFacial2.getHolder();
        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean result = syncFeature(bitmap, firstFeature, 1, true);
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
                } catch (Exception e) {
                    j++;
                    if (j < 5) {
                        cloesCamera();
                        openCamera();
                    } else {
                        LogUtil.e("打开相机失败catch");
                    }
                }
            } else {
                LogUtil.e("打开相机1失败");
                j++;
                if (j < 5) {
                    openCamera();
                } else {
                    LogUtil.e("打开相机失败else");
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
            try {
                isCompare = true;
                LogUtil.i("处理上传照片");
                byte[] bytes2 = YuvUtil.nv21ConvertBitmap(bytes);
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytes2, 0, bytes2.length);
                if (syncFeature(bitmap2, secondFeature, 2, true)) {
                    match();
                } else {
                    isCompare = false;
                }
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
                isCompare = false;
            }
        }
    }

    /**
     * bitmap -提取特征值
     *
     * @param bitmap
     * @param feature
     * @param index
     */

    private boolean syncFeature(final Bitmap bitmap, final byte[] feature, final int index, boolean isFromPhotoLibrary) {
        float ret = -1;
        boolean result = false;
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        // 检测结果判断
        if (faceInfos != null && faceInfos.length > 0) {

            // 判断质量检测，针对模糊度、遮挡、角度
            if (qualityCheck(faceInfos[0], isFromPhotoLibrary)) {

                ret = FaceSDKManager.getInstance().getFaceFeature().feature(BDFaceSDKCommon.FeatureType.
                        BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
                LogUtil.i("ret:" + ret);
                if (ret == 128 && index == 1) {
                    firstFeatureFinished = true;
                } else if (ret == 128 && index == 2) {
                    secondFeatureFinished = true;
                }
                if (ret == 128) {
                    LogUtil.i("图片" + index + "特征抽取成功");
                    result = true;
                } else if (ret == -100) {
                    LogUtil.e("未完成人脸比对，可能原因，图片1为空");
                } else if (ret == -101) {
                    LogUtil.e("未完成人脸比对，可能原因，图片2为空");
                } else if (ret == -102) {
                    LogUtil.e("未完成人脸比对，可能原因，图片1未检测到人脸");
                } else if (ret == -103) {
                    LogUtil.e("未完成人脸比对，可能原因，图片2未检测到人脸");
                } else {
                    LogUtil.e("未完成人脸比对，可能原因，"
                            + "人脸太小（小于sdk初始化设置的最小检测人脸）"
                            + "人脸不是朝上，sdk不能检测出人脸");
                }
            }
        } else {
            LogUtil.e("未检测到人脸,可能原因人脸太小");
        }
        return result;
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FacileBaiduOfflineActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 质量检测
     * FaceInfo faceInfo
     *
     * @return
     */
    public boolean qualityCheck(final FaceInfo faceInfo, boolean isFromPhotoLibrary) {

        // 不是相册选的图片，不必再次进行质量检测，因为采集图片的时候已经做过了
        if (!isFromPhotoLibrary) {
            return true;
        }

        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return true;
        }

        if (faceInfo != null) {
            // 模糊结果过滤
            float blur = faceInfo.bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                toast("图片模糊");
                return false;
            }

            // 光照结果过滤
            float illum = faceInfo.illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                toast("图片光照不通过");
                return false;
            }

            // 遮挡结果过滤
            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // 左眼遮挡置信度
                    toast("左眼遮挡");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // 右眼遮挡置信度
                    toast("右眼遮挡");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // 鼻子遮挡置信度
                    toast("鼻子遮挡");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // 嘴巴遮挡置信度
                    toast("嘴巴遮挡");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // 左脸遮挡置信度
                    toast("左脸遮挡");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // 右脸遮挡置信度
                    toast("右脸遮挡");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // 下巴遮挡置信度
                    toast("下巴遮挡");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * argb - 特征比对
     */

    private void match() {
        if (!firstFeatureFinished) {
            toast("图片一特征抽取失败");
            return;
        }
        if (!secondFeatureFinished) {
            toast("图片二特征抽取失败");
            return;
        }
        int idFeatureValue = SingleBaseConfig.getBaseConfig().getThreshold();
        float score = 0;
        //  比较两个人脸
        score = FaceSDKManager.getInstance().getFaceFeature().featureCompare(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,
                firstFeature, secondFeature, true);
        LogUtil.e("分数：" + String.valueOf(score));
        if (score > idFeatureValue) {
            LogUtil.i("核验通过");
            tvSucceedTip2.setVisibility(View.VISIBLE);
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
            tvTip2.append("\n" + format.format(System.currentTimeMillis()) + "：第" + ++num + "次成功识别！");
            svTip2.fullScroll(ScrollView.FOCUS_DOWN);
            handler.postDelayed(runnable1, 5000);
            cloesCamera();
        } else {
            LogUtil.e("核验不通过");
        }
        isCompare = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cloesCamera();
    }
}
