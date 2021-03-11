
package com.ruoyi.radius.toughradius.utils.wx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 二维码生成工具类
 *
 * @author panweilei
 * @date 2021-01-25
 */
public class QRCodeUtil {

    // 二维码尺寸
    public static final int QRCODE_SIZE = 300;

    // 存放二维码的路径
    public static final String PAY_PATH = "E://pay";

    /**
     * 生成二维码文件
     * @param content
     * @param pngName
     * @throws Exception
     */
    public static void createImage(String content,String pngName) throws Exception {
    	QRCodeWriter qrCodeWriter = new QRCodeWriter();
		
		BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 350, 350);
		
		Path path = FileSystems.getDefault().getPath(PAY_PATH + "/" + pngName);
		
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    }

    /**
     * 生成二维码字节流
     * @param content
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static byte[] getQRCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray(); 
        return pngData;
    }
}
