package com.biao.facialrecognition.model;

/**
 * Created by benxiang on 2019/9/6.
 */

public class FaceQueryBen {

    /**
     * image : sfasq35sadvsvqwr5q...
     * image_type : BASE64
     * face_type : LIVE
     * quality_control : LOW
     * liveness_control : HIGH
     */

    private String image;
    private String image_type;
    private String face_type;
    private String quality_control;
    private String liveness_control;


    public FaceQueryBen(String image, String image_type, String face_type, String quality_control, String liveness_control) {
        this.image = image;
        this.image_type = image_type;
        this.face_type = face_type;
        this.quality_control = quality_control;
        this.liveness_control = liveness_control;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getFace_type() {
        return face_type;
    }

    public void setFace_type(String face_type) {
        this.face_type = face_type;
    }

    public String getQuality_control() {
        return quality_control;
    }

    public void setQuality_control(String quality_control) {
        this.quality_control = quality_control;
    }

    public String getLiveness_control() {
        return liveness_control;
    }

    public void setLiveness_control(String liveness_control) {
        this.liveness_control = liveness_control;
    }
}
