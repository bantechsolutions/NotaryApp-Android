package com.notaryapp.interfacelisterners;

public interface CaptureStampListerner {
    void captureStamp(String from,int position, String licenseNo, String state, boolean flag, String stampName);
}
