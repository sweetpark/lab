package com.example.playground.kms;

import kms.wiezon.com.crypt.CryptUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.lang.reflect.Field;
import java.io.InputStream;

public class KmsApplication {

    public static void main(String[] args) throws Exception {
        CryptUtils cryptUtils = new CryptUtils();

        PrintStream originalOut = System.out;

        // system.out 제어
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        
        try (InputStream input = KmsApplication.class.getClassLoader().getResourceAsStream("kms.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);

                setField(cryptUtils, "sendIP", prop.getProperty("SEND_IP"));
                setField(cryptUtils, "remoteIP", prop.getProperty("REMOTE_IP"));
                setField(cryptUtils, "remotePORT", prop.getProperty("REMOTE_PORT"));
                setField(cryptUtils, "remoteTIMEOUT", prop.getProperty("REMOTE_TIMEOUT"));
            } else {
                throw new RuntimeException("kms.properties not found in classpath");
            }
        } catch (Exception e) {
            System.setOut(originalOut);
            System.out.println(Response.out(Response.fail(e.getMessage(), "", "")));
            return;
        }

        if(args.length < 2 || args.length > 3){
            System.out.println(" ============ [파라미터 오류] =========== ");
            System.out.println(" [Usage] java -jar kms.jar \"[COLUMN]\" \"[VALUE]\" \"[OPTION]\"" );
            System.out.print(" option[type] \n" +
                    " [default] : value + Hash + Encrypt \n" +
                    " [pw] : Password + OTP Password \n");
            System.out.println(" ======================================");
            return;
        }
        String column = args[0];
        String value = args[1];
        String option = args.length == 3 ? args[2] : "default";

        try{
            Response response = Response.fromOption(option, column, value);
            System.setOut(originalOut);
            System.out.println(Response.out(response));
        }catch(Exception e){
            System.setOut(originalOut);
            System.out.println(Response.out(Response.fail(e.getMessage(), column, value)));
        }

    }

    private static void setField(Object target, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}