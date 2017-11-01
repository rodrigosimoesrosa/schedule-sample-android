package br.com.mirabilis.schedulesample.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ticketservices on 7/6/17.
 */

public class HashUtil {

    public static String doHash(String value) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            String a = value;
            byte[] b = a.getBytes("UTF-8");
            sha.update(b, 0, b.length);
            byte[] digest = sha.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            Log.e("ERROR_ENCODING", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
