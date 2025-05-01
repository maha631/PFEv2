package com.cni.plateformetesttechnique.model;

import java.util.Base64;




public class ImageUtil {
    public static String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
