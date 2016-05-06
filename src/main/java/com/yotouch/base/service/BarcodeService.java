package com.yotouch.base.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface BarcodeService {

    byte[] genQrCode(String text, int height) throws WriterException, IOException;

}
