package com.app.daol.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 설정파일(application.properties)의 값 복호화용 AES 유틸.
 * 키는 절대 코드에 하드코딩하지 않고 환경변수(AES_SECRET_KEY 등)로 주입한다.
 */
public class Aes {

    private final byte[] key;
    private Cipher cipher;

    private Aes() {
        String hexKey = System.getenv("AES_SECRET_KEY");
        if (hexKey == null || hexKey.isEmpty()) {
            throw new IllegalStateException("AES_SECRET_KEY 환경변수가 설정되어 있지 않습니다.");
        }
        this.key = hexStringToByteArray(hexKey);
        try {
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            throw new IllegalStateException("AES Cipher 초기화 실패", e);
        }
    }

    private static class AESSingleton {
        private static final Aes instance = new Aes();
    }

    public static Aes getInstance() {
        return AESSingleton.instance;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String encryptString(String plain) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes()));
    }

    public String decryptString(String base64Str) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        return new String(cipher.doFinal(Base64.getDecoder().decode(base64Str)));
    }
}
