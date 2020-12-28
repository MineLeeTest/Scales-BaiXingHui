package com.camera.simplewebcam;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraPreview {

    private static final int IMG_WIDTH = 640;
    private static final int IMG_HEIGHT = 480;
    private static CameraPreview mCameraPreview = null;

    static {
        System.loadLibrary("ImageProc");
    }

    private int cameraId = -1;
    private int cameraBase = 0;

    private CameraPreview() throws Exception {

        int ret;

        do {
            int cameraBase = 0;
            ret = prepareCameraWithBase(++cameraId, cameraBase);
        } while (ret == -1 && cameraId <= 6);

        if (ret == -1)
            throw new Exception("no camera id exists:" + cameraId);
        else
            stopCamera();
    }

    public native int prepareCameraWithBase(int videoID, int cameraBase);

    public native void stopCamera();

    public static CameraPreview getInstance() {
        if (mCameraPreview == null) {
            synchronized (CameraPreview.class) {
                try {
                    mCameraPreview = new CameraPreview();
                } catch (Exception e) {
                    Log.e("CameraPreview", "拍照初始化异常！");
                }
            }
        }
        return mCameraPreview;
    }

    public String taskPic() {

        int ret = prepareCameraWithBase(cameraId, cameraBase);

        if (ret == -1) {
           return null;
        }

        processCamera();

        Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);

        pixeltobmp(bmp);

        ByteArrayOutputStream out = null;

        String result = null;

        try {
            out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] bs = out.toByteArray();
            result = Base64.encodeToString(bs, Base64.DEFAULT);
        } catch (Exception e) {

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
            if (bmp != null) {
                bmp.recycle();
            }
        }

//        stopCamera();

        return result;
    }

    public native void processCamera();

    public native void pixeltobmp(Bitmap bitmap);

}
