package com.imapcloud.sdk.pojo.callback;

public interface OnSocketTransPhotoListener {

        void onInit(String fileName, long fileSize);

        void onProcess(double i);

        void onSaveSuccess(boolean isSuccess, String errMsg);

        void onSocketClosed();

    }