package com.nagornyi.uc.common.liqpay;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LiqPayUtil {

    public static byte[] sha1(String param) {
        MessageDigest SHA;
        try {
            SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Could not create SHA1");
        }
        return SHA.digest();
    }


    public static String base64Encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }
}