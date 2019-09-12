package com.biao.facialrecognition.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

/**
 * Created by Tairong Chan on 2017/2/24.
 * Connect:
 */

public class FaceDetectUtils {

    private static FaceDetector.Face[] faces;
    private static final int MAX_FACE = 15;

    private FaceDetectUtils() {
    }

    public static void init() {
        faces = new FaceDetector.Face[MAX_FACE];
    }

    public static boolean isDetectFace(Bitmap bitmap) {
        return detectFaceCount(bitmap) > 0;
    }

    public static int detectFaceCount(Bitmap bitmap) {
        FaceDetector.Face[] faces = detectFace(bitmap);
        return faces.length;
    }

    public static FaceDetector.Face[] detectFace(Bitmap bitmap) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();
        FaceDetector faceDetector = new FaceDetector(imageWidth, imageHeight, MAX_FACE);
        int faceNumber = faceDetector.findFaces(bitmap, faces);
        FaceDetector.Face[] detectedFace = new FaceDetector.Face[faceNumber];
        System.arraycopy(faces, 0, detectedFace, 0, faceNumber);
        return detectedFace;
    }

    public static Bitmap drawFaceArea(Bitmap bitmap, Paint paint) {
        FaceDetector.Face[] detectedDaces = detectFace(bitmap);
        if (detectedDaces.length == 0) return bitmap;
        Bitmap newBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 0; i < detectedDaces.length; i++) {
            PointF eyeMidPoint = new PointF();
            detectedDaces[i].getMidPoint(eyeMidPoint);
            float eyesDistance = detectedDaces[i].eyesDistance();
            canvas.drawRect((eyeMidPoint.x - eyesDistance), (eyeMidPoint.y - eyesDistance),
                    (eyeMidPoint.x + eyesDistance), (eyeMidPoint.y + eyesDistance), paint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
//      canvas.save();
            canvas.restore();
        }
        return newBitmap;
    }

    public static Bitmap drawFaceArea(Bitmap bitmap) {
        return drawFaceArea(bitmap, defaultPaint());
    }

    private static Paint defaultPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10.0f);
        return paint;
    }

    public static Bitmap drawBigFaceArea(Bitmap bitmap) {
        FaceDetector.Face[] faces = detectFace(bitmap);
        if (faces.length == 0) return bitmap;
        Bitmap newBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 0; i < faces.length; i++) {
            PointF eyeMidPoint = new PointF();
            faces[i].getMidPoint(eyeMidPoint);
            float eyesDistance = faces[i].eyesDistance();
            float left = (eyeMidPoint.x - (1.5f * eyesDistance));
            float right = (eyeMidPoint.x + (1.5f * eyesDistance));
            float top = (eyeMidPoint.y - (2.0f * eyesDistance));
            float bottom = (eyeMidPoint.y + (2.0f * eyesDistance));
            Log.e("LcDebug", ">>>>>>>>" + left + " " + right + " " + top + " " + bottom);
            canvas.drawRect(left > 0 ? left : 0,
                    top > 0 ? top : 0,
                    right < bitmap.getWidth() ? right : bitmap.getWidth(),
                    bottom < bitmap.getHeight() ? bottom : bitmap.getHeight(),
                    defaultPaint());
            canvas.save();
//      canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return newBitmap;
    }

    public static FaceDetector.Face[] detectFace(byte[] image) {
//    BitmapFactory.Options options = new BitmapFactory.Options();
//    options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();
        FaceDetector faceDetector = new FaceDetector(imageWidth, imageHeight, MAX_FACE);
        int faceNumber = faceDetector.findFaces(bitmap, faces);
        FaceDetector.Face[] detectedFace = new FaceDetector.Face[faceNumber];
        System.arraycopy(faces, 0, detectedFace, 0, faceNumber);
        return detectedFace;
    }

    public static Bitmap[] clipFace(Bitmap bitmap) {
        FaceDetector.Face[] faces = detectFace(bitmap);
        if (faces.length == 0) return null;
        Bitmap[] faceBitmaps = new Bitmap[faces.length];
        for (int i = 0; i < faces.length; i++) {
            PointF eyeMidPoint = new PointF();
            faces[i].getMidPoint(eyeMidPoint);
            float eyesDistance = faces[i].eyesDistance();
            int left = (int) (eyeMidPoint.x - (1.5f * eyesDistance));
            left = left > 0 ? left : 0;
            int right = (int) (eyeMidPoint.x + (1.5f * eyesDistance));
            right = right < bitmap.getWidth() ? right : bitmap.getWidth();
            int top = (int) (eyeMidPoint.y - (2.0f * eyesDistance));
            top = top > 0 ? top : 0;
            int bottom = (int) (eyeMidPoint.y + (2.0f * eyesDistance));
            bottom = bottom < bitmap.getHeight() ? bottom : bitmap.getHeight();
            Log.e("LcDebug", ">>>>>>>>" + left + " " + right + " " + top + " " + bottom);
            Bitmap faceBitmap = bitmap.createBitmap(bitmap, left, top, right - left, bottom - top);
            faceBitmaps[i] = faceBitmap;
        }
        return faceBitmaps;
    }
}
