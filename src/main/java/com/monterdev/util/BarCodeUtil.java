package com.monterdev.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.IOException;

import static com.monterdev.configuration.GlobalConfiguration.generatedQrCodeDirectory;
import static com.monterdev.configuration.GlobalConfiguration.getConfigValue;
import static com.monterdev.constants.FileTypeConstants.*;

public class BarCodeUtil {

    public static String filePath = getConfigValue(generatedQrCodeDirectory);

    public static void saveBarCode(String sku, String fileName) throws WriterException, IOException {
        generateBarcode(sku, filePath + "\\" + fileName + JPEG_FORMAT, APP_CHARSET, IMAGE_DIMENSION_HEIGHT, IMAGE_DIMENSION_WIDTH);
    }

    public static void generateBarcode(String data, String path, String charset, int h, int w) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.CODE_128, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

}
