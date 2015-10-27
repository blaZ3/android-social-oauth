package com.talview.socialauthwrapper.helpers;

import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by root on 19/10/15.
 */
public class MiscHelpers {

    public static JsonObject parseAsJsonObject(String str){
        JsonObject jsonObject = null;
        JsonParser parser = new JsonParser();

        if(str != null){
            if(str.length()>0){
                try{
                    jsonObject = (JsonObject)parser.parse(str);
                }catch (Exception ex){
                    Log.e("EXP parseJson", ex.getMessage());
                }
            }else {
                return null;
            }
            return jsonObject;
        }else{
            return null;
        }

    }

    //Map<String, String> map = new TreeMap<String, String>();
    public static String getOAuthSignature(Map<String, String> map, String app_secret, String oauth_token_secret, String url)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        String parameter_string = "POST&"+getUrlEncoded(url)+"&";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("&");
        }

        String query_string = sb.toString();
        try{
            query_string = query_string.substring(0, query_string.length() - 1);
        }catch (Exception ex){}

        parameter_string = parameter_string + getUrlEncoded(query_string);

        //app_secret should be percent encoded consumer key with an &
        app_secret = getUrlEncoded(app_secret)+"&"+getUrlEncoded(oauth_token_secret);

        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(app_secret.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] bytes = mac.doFinal(parameter_string.getBytes());

        // encode data on your side using BASE64
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        base64 = base64.replace("\n","");
        return getUrlEncoded(base64);
    }

    public final static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * returns the url encoded version of the given string
     * @param str
     * @return
     */
    private static String getUrlEncoded(String str){
        try{
            String encoded = URLEncoder.encode(str, "UTF-8");
            encoded = encoded.replace("+","%20");
            encoded = encoded.replace("%7E", "~");
            return encoded;
        }catch (Exception ex){}
        return "";
    }


    public static String randomString(int length) {
        char[] characterSet = "ABCDEFGHIJK34654756LMNOPQRSTUVWXYZabcdefskdhfksjhdfnisuydf34534554430123456789".toCharArray();
        Random random = new Random();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

//    public static String randomString(int numChars) {
//        final char[] VALID_CHARACTERS =
//                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456879".toCharArray();
//        SecureRandom srand = new SecureRandom();
//        Random rand = new Random();
//        char[] buff = new char[numChars];
//
//        for (int i = 0; i < numChars; ++i) {
//            // reseed rand once you've used up all available entropy bits
//            if ((i % 10) == 0) {
//                rand.setSeed(srand.nextLong()); // 64 bits of random!
//            }
//            buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
//        }
//        return new String(buff);
//    }

    public static long getTimestamp(){
        long millis = System.currentTimeMillis() / 1000;
        return millis;
    }

}
