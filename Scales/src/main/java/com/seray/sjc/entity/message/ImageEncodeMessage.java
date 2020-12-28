package com.seray.sjc.entity.message;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author：李程
 * CreateTime：2019/5/18 16:29
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class ImageEncodeMessage implements Parcelable {

    public String data;
    public Bitmap bitmap;

    public ImageEncodeMessage() {
    }

    public ImageEncodeMessage(String data, Bitmap bitmap) {
        this.data = data;
        this.bitmap = bitmap;
    }

    protected ImageEncodeMessage(Parcel in) {
        data = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ImageEncodeMessage> CREATOR = new Creator<ImageEncodeMessage>() {
        @Override
        public ImageEncodeMessage createFromParcel(Parcel in) {
            return new ImageEncodeMessage(in);
        }

        @Override
        public ImageEncodeMessage[] newArray(int size) {
            return new ImageEncodeMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeParcelable(bitmap, flags);
    }
}
