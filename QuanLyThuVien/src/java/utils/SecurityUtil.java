package utils;

import java.security.MessageDigest;

public class SecurityUtil {
    
    // Thuật toán băm mật khẩu MD5
    public static String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            // Trả về chuỗi đã bị mã hóa (Ví dụ: 202cb962ac59075b964b07152d234b70)
            return sb.toString(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}