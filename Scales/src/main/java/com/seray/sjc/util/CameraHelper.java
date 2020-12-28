package com.seray.sjc.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Base64;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.seray.sjc.entity.message.ImageEncodeMessage;
import com.seray.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/18 12:57
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class CameraHelper implements Camera.PreviewCallback, SurfaceHolder.Callback {

    private static final int IMG_WIDTH = 640;
    private static final int IMG_HEIGHT = 480;

    private static int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;

    private Camera mCamera;
    private Activity mActivity;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private boolean isWork = false;
    private boolean isCameraOpened = false;

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType;
    private Allocation in, out;

    public CameraHelper(Activity activity, SurfaceView surfaceView) {
        this.mActivity = activity;
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolder = surfaceView.getHolder();
        this.mSurfaceHolder.addCallback(this);

        rs = RenderScript.create(activity);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    private boolean isStartPreviewImage = false;

    public void getPreviewImage() {
        isStartPreviewImage = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) {
            openCamera();
        }
        startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isStartPreviewImage) {
            if (isWork) {
                isStartPreviewImage = false;
                return;
            }
            isWork = true;
            isStartPreviewImage = false;
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            Bitmap bitmap = nv21ToBitmap(data, previewSize.width, previewSize.height);
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                byte[] bs = out.toByteArray();
                String encodeBs = Base64.encodeToString(bs, Base64.DEFAULT);
                EventBus.getDefault().post(new ImageEncodeMessage(encodeBs, bitmap));
            } catch (Exception e) {
                LogUtil.w("encodeImage error");
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        LogUtil.w("encodeImage error");
                    }
                }
                isWork = false;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        if (yuvType == null) {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
            Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            bitmap = rotate(bitmap, 270f);  //前置摄像头旋转270°
        } else {
            bitmap = rotate(bitmap, 90f);  //后置摄像头旋转90°
        }
        return bitmap;
    }

    private void openCamera() {
        try {
            mCamera = Camera.open(mCameraFacing);
            isCameraOpened = true;
            if (mCamera != null) {
                initParameters();
                mCamera.setPreviewCallback(this);
            }
        } catch (Exception e) {
            isCameraOpened = false;
            mCamera = null;
            LogUtil.e("相机打开失败！" + e.getMessage());
        }
    }

    public void releaseCamera() {
        if (mCamera != null && isCameraOpened) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initParameters() {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            Camera.Size bestPreviewSize = getBestSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), parameters.getSupportedPreviewSizes());
            if (bestPreviewSize != null) {
                parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            }
            Camera.Size bestSaveSize = getBestSize(IMG_WIDTH, IMG_HEIGHT, parameters.getSupportedPictureSizes());
            if (bestSaveSize != null) {
                parameters.setPictureSize(bestSaveSize.width, bestSaveSize.height);
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            LogUtil.e("相机参数设置失败！" + e.getMessage());
        }
    }

    private Camera.Size getBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizeList) {
        Camera.Size bestSize = null;
        double targetRatio = targetHeight * 1.d / targetWidth;
        double minDiff = targetRatio;
        for (Camera.Size size : sizeList) {
            if (size.width == targetWidth && size.height == targetHeight) {
                bestSize = size;
                break;
            }
            double supportedRatio = (size.width * 1.d / size.height);
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }
        return bestSize;
    }

    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraFacing, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int screenDegree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                screenDegree = 0;
                break;
            case Surface.ROTATION_90:
                screenDegree = 90;
                break;
            case Surface.ROTATION_180:
                screenDegree = 180;
                break;
            case Surface.ROTATION_270:
                screenDegree = 270;
                break;
        }
        int displayOrientation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + screenDegree) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (info.orientation - screenDegree + 360) % 360;
        }
        mCamera.setDisplayOrientation(displayOrientation);
    }

    private Bitmap rotate(Bitmap rawBitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);
    }

    private void startPreview() {
        if (!isCameraOpened || mCamera == null) {
            LogUtil.e("设置预览界面失败！相机未打开！");
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            setCameraDisplayOrientation();
            mCamera.startPreview();
        } catch (IOException e) {
            LogUtil.e("设置预览界面失败！" + e.getMessage());
        }
    }
}
