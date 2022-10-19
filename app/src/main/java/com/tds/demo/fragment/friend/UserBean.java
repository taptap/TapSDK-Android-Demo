package com.tds.demo.fragment.friend;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * 2022/10/19
 * Describe：
 */

public class UserBean implements Parcelable {

    private String shortId;
    private String createdAt;
    private Boolean emailVerified;
    private String nickname;
    private String avatar;
    private Boolean mobilePhoneVerified;
    private String objectId;
    private String updatedAt;
    private String username;


    public UserBean() {

    }

    protected UserBean(Parcel in) {
        shortId = in.readString();
        createdAt = in.readString();
        byte tmpEmailVerified = in.readByte();
        emailVerified = tmpEmailVerified == 0 ? null : tmpEmailVerified == 1;
        nickname = in.readString();
        avatar = in.readString();
        byte tmpMobilePhoneVerified = in.readByte();
        mobilePhoneVerified = tmpMobilePhoneVerified == 0 ? null : tmpMobilePhoneVerified == 1;
        objectId = in.readString();
        updatedAt = in.readString();
        username = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getMobilePhoneVerified() {
        return mobilePhoneVerified;
    }

    public void setMobilePhoneVerified(Boolean mobilePhoneVerified) {
        this.mobilePhoneVerified = mobilePhoneVerified;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shortId);
        dest.writeString(createdAt);
        dest.writeByte((byte) (emailVerified == null ? 0 : emailVerified ? 1 : 2));
        dest.writeString(nickname);
        dest.writeString(avatar);
        dest.writeByte((byte) (mobilePhoneVerified == null ? 0 : mobilePhoneVerified ? 1 : 2));
        dest.writeString(objectId);
        dest.writeString(updatedAt);
        dest.writeString(username);
    }
}
