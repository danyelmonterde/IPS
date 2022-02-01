package com.monterdev.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.monterdev.constants.GlobalConfiguration.generatedQrCodeDirectory;
import static com.monterdev.constants.GlobalConfiguration.getConfigValue;

public class QrCodeUtil {


    public static String filePath = getConfigValue(generatedQrCodeDirectory);

    public static void saveQrCode(String sku,String fileName) throws Exception {
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        generateQRcode(sku, filePath+"\\"+fileName+".jpg", charset, hashMap, 200, 200);

    }

    public static void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.CODE_128, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }


    public static String readQrCodeImage() throws IOException, NotFoundException {
        String filePath = getConfigValue(generatedQrCodeDirectory)+"/release-item.png";

        // Encoding charset
        String charset = "UTF-8";

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap
                = new HashMap<EncodeHintType,
                ErrorCorrectionLevel>();

        hashMap.put(EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.L);


        return readQRCode(filePath, charset, hashMap);
    }

    public static String readQRCodeFromInputStream(InputStream inputStream)
            throws IOException,
            NotFoundException {
        BinaryBitmap binaryBitmap
                = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(inputStream))));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }

    // Function to read the QR file
    public static String readQRCode(String path, String charset,
                                    Map hashMap)
            throws FileNotFoundException, IOException,
            NotFoundException {
        BinaryBitmap binaryBitmap
                = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(
                                new FileInputStream(path)))));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }

}
