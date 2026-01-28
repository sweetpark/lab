package com.example.playground.wiezon.util;

import kms.wiezon.com.crypt.CryptUtils;

import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base32;

public class EncUtil {

    final static CryptUtils cryptUtils = new CryptUtils();


    // PW 생성
    public static String createEncPw(String password){
        return encodedSHA512(Base64EncodedMD5(password));
    }

    // OTP 생성
    public static String createEncOtp(){
        String otpKey = makeRandomPw() + makeRandomPw();
        Base32 base32 = new Base32();

        return base32.encodeAsString(otpKey.getBytes()).substring(0,32);
    }

    // Hash 생성
    public static String createHash(String str){
        return SHA256(str);
    }

    // Encrypt 적용
    public static String createEnc(String str){
        try {
            return cryptUtils.encrypt(str);
        } catch (Exception e) {
            throw new RuntimeException("Encrypt 싷패",e);
        }
    }


    private static String encodedSHA512(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.reset();
            md.update(password.getBytes());

            byte[] raw = md.digest();
            byte[] encodedBytes = Base64.encodeBase64(raw);

            return new String(encodedBytes);
        }catch(Exception e){
            throw new RuntimeException("sha-512 암호화 실패", e);
        }
    }

    private static String Base64EncodedMD5(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());
            byte[] raw = md.digest();
            byte[] encodedBytes = Base64.encodeBase64(raw);

            return new String(encodedBytes);

        }catch(Exception e){
            throw new RuntimeException("sha-256 암호화 실패", e);
        }
    }

    private static String SHA256(String str){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            byte[] byteData = md.digest();

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch(Exception e){
            throw new RuntimeException("Hash 실패 (sha-256)", e);
        }
    }

    private static String makeRandomPw(){
        // 패스워드에 사용될 문자 배열
        char pwCollection[] = new char[] {
                '1','2','3','4','5','6','7','8','9','0',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                '!','@','#','$','%','^','&','*'};

        String randomPw = "";
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int selectRandomPw = (int)(secureRandom.nextInt(pwCollection.length));
            randomPw += pwCollection[selectRandomPw];
        }

        return randomPw;
    }
}
