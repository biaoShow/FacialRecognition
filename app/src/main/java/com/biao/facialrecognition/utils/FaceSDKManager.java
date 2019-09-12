package com.biao.facialrecognition.utils;

import android.content.Context;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.biao.facialrecognition.MainActivity;
import com.biao.facialrecognition.common.Constant;
import com.biao.facialrecognition.model.GlobalSet;
import com.biao.facialrecognition.preference.PreferencesKey;
import com.biao.facialrecognition.preference.SharedPreferencesUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceAnakinRunMode.BDFACE_ANAKIN_RUN_AT_SMALL_CORE;
import static com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_ALL_MESSAGE;
import static com.biao.facialrecognition.model.GlobalSet.ALIGN_MODEL;
import static com.biao.facialrecognition.model.GlobalSet.DETECT_NIR_MODE;
import static com.biao.facialrecognition.model.GlobalSet.DETECT_VIS_MODEL;

public class FaceSDKManager {


    private FaceAuth faceAuth;
    private FaceDetect faceDetect;
    private FaceFeature faceFeature;
    private FaceLive faceLiveness;
    private static FaceSDKManager faceSDKManager;

    private FaceSDKManager() {
        faceAuth = new FaceAuth();
        faceAuth.setActiveLog(BDFACE_LOG_ALL_MESSAGE);
        faceAuth.setAnakinConfigure(BDFACE_ANAKIN_RUN_AT_SMALL_CORE, 2);

        faceDetect = new FaceDetect();
        faceFeature = new FaceFeature();
        faceLiveness = new FaceLive();
    }


    public static FaceSDKManager getInstance() {
        if (null == faceSDKManager) {
            faceSDKManager = new FaceSDKManager();
        }
        return faceSDKManager;
    }

    public FaceDetect getFaceDetect() {
        return faceDetect;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public FaceLive getFaceLiveness() {
        return faceLiveness;
    }

    /**
     * 激活百度人脸识别离线SDK
     */
    public void activateSDK(final Context context, final SharedPreferencesUtil sharedPreferencesUtil) {
        faceAuth.initLicenseOnLine(context, Constant.BAIDU_KEY, new Callback() {
            @Override
            public void onResponse(final int code, final String response) {
                if (code == 0) {
                    sharedPreferencesUtil.putString(PreferencesKey.BAIDU_SDK, "true");
                    LogUtil.i("激活成功");
                    ToastUtils.toast(context, "激活成功");
                    authentication(context);
                } else {
                    LogUtil.i("激活失败:" + response);
                    ToastUtils.toast(context, "激活失败:" + response);
                }
            }
        });
    }

    /**
     * 百度SDK鉴权
     */
    public void authentication(final Context context) {
        faceAuth.initLicenseBatchLine(context, Constant.BAIDU_KEY, new Callback() {
            @Override
            public void onResponse(int i, String s) {
                if (i == 0) {
                    LogUtil.i("鉴权成功");
                    initModel(context);
                } else {
                    LogUtil.i("鉴权失败：" + s);
                    ToastUtils.toast(context, "鉴权失败:" + s);
                }
            }
        });
    }

    /**
     * 模型初始化
     */
    private void initModel(final Context context) {
        initConfig();
        faceDetect.initModel(context, DETECT_VIS_MODEL, DETECT_NIR_MODE, ALIGN_MODEL, new Callback() {
            @Override
            public void onResponse(int i, String s) {
                if (i == 0) {
                    LogUtil.i("faceDetect初始化模块成功");
                } else {
                    LogUtil.i("faceDetect初始化模块失败：" + s);
                    ToastUtils.toast(context, "faceDetect初始化模块失败:" + s);
                }
            }
        });
        faceDetect.initQuality(context, GlobalSet.BLUR_MODEL,
                GlobalSet.OCCLUSION_MODEL, new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code == 0) {
                            LogUtil.i("faceDetect质量模型加载成功");
                        } else {
                            LogUtil.i("faceDetect质量模型加载失败");
                            ToastUtils.toast(context, "faceDetect质量模型加载失败:" + response);
                        }
                    }
                });
        faceFeature.initModel(context, GlobalSet.RECOGNIZE_IDPHOTO_MODEL, GlobalSet.RECOGNIZE_VIS_MODEL, "",
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code == 0) {
                            LogUtil.i("faceFeature模型加载成功");
                            ToastUtils.toast(context, "模型加载成功");
                        } else {
                            LogUtil.i("faceFeature模型加载失败");
                            ToastUtils.toast(context, "faceFeature模型加载失败:" + response);
                        }
                    }
                });
    }

    /**
     * 初始化配置
     *
     * @return
     */
    public boolean initConfig() {
        if (faceDetect != null) {
            BDFaceSDKConfig config = new BDFaceSDKConfig();
            // TODO: 最小人脸个数检查，默认设置为1,用户根据自己需求调整
            config.maxDetectNum = 1;

            // TODO: 默认为80px。可传入大于30px的数值，小于此大小的人脸不予检测，生效时间第一次加载模型
            config.minFaceSize = SingleBaseConfig.getBaseConfig().getMinimumFace();
            // 是否进行属性检测，默认关闭
            config.isAttribute = SingleBaseConfig.getBaseConfig().isAttribute();

            // TODO: 模糊，遮挡，光照三个质量检测和姿态角查默认关闭，如果要开启，设置页启动
            config.isCheckBlur = config.isOcclusion
                    = config.isIllumination = config.isHeadPose
                    = SingleBaseConfig.getBaseConfig().isQualityControl();

            faceDetect.loadConfig(config);
            return true;
        }
        return false;
    }
}