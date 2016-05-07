package com.yotouch.base.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;


@Service
public class BarcodeServiceImpl implements BarcodeService {

    @Override
    public byte[] genQrCode(String text, int height) throws WriterException, IOException {

        Writer writer = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

        BitMatrix matrix = writer.encode(
                text,
                BarcodeFormat.QR_CODE,
                height,
                height,
                hints);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "png", bos);
        return bos.toByteArray();
    }

}
