package com.biao.facialrecognition.model;

/**
 * Created by benxiang on 2019/9/5.
 */

public class GetTonken {

    /**
     * refresh_token : 25.6196b7fbcbf7b51279f8bb6b9d1a8503.315360000.1883037509.282335-11196453
     * expires_in : 2592000
     * session_key : 9mzdCXSWLpUmO3AKxasnMOjLADUX2iz7H62YU+JhxQAogTGI4/Gi8XlhkbgDqDYq96d9tHu8iZSa/rgQ9eBgv9mcrYHLFw==
     * access_token : 24.2be7efb3e7e002c971083b85d61d839d.2592000.1570269509.282335-11196453
     * scope : brain_cvpaas-app-scope vis-faceverify_FACE_Police vis-faceverify_idl_face_merge vis-faceverify_FACE_V3 vis-faceverify_faceverify_h5-face-liveness public vis-faceverify_faceverify vis-ocr_ocr vis-faceattribute_faceattribute vis-faceverify_faceverify_v2 vis-faceverify_faceverify_match_v2 brain_ocr_general brain_ocr_general_basic brain_ocr_general_enhanced brain_all_scope brain_ocr_accurate_basic vis-faceverify_vis-faceverify-detect wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test权限 vis-classify_flower lpq_开放 cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi fake_face_detect_开放Scope
     * session_secret : e4f66067f1edc295aaa3cf3c41f9599a
     */

    private String refresh_token;
    private int expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSession_secret() {
        return session_secret;
    }

    public void setSession_secret(String session_secret) {
        this.session_secret = session_secret;
    }
}
