package utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PayOSUtil {

    private static final Properties config = new Properties();

    static {
        try (InputStream input = PayOSUtil.class.getClassLoader().getResourceAsStream("payos.properties")) {
            if (input != null) {
                config.load(input);
                System.out.println("✅ [PayOS] Da nap file payos.properties thanh cong!");
            } else {
                System.err.println("❌ [PayOS] LOI: Khong tim thay file payos.properties!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getQRCode(int orderCode, int amount, String description) {
        try {
            String clientId = config.getProperty("payos.client_id");
            String apiKey = config.getProperty("payos.api_key");
            String checksumKey = config.getProperty("payos.checksum_key");

            if (clientId == null || apiKey == null || checksumKey == null) {
                System.err.println("❌ [PayOS] LOI: Cac thong so trong file properties dang bi trong!");
                return null;
            }

            String cancelUrl = "http://localhost:8080/QuanLyThuVien/home";
            String returnUrl = "http://localhost:8080/QuanLyThuVien/home";

            String dataForSignature = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(dataForSignature.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String signature = hexString.toString();

            URL url = new URL("https://api-merchant.payos.vn/v2/payment-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-client-id", clientId);
            conn.setRequestProperty("x-api-key", apiKey);
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            String jsonInput = String.format(
                "{\"orderCode\":%d,\"amount\":%d,\"description\":\"%s\",\"cancelUrl\":\"%s\",\"returnUrl\":\"%s\",\"signature\":\"%s\"}",
                orderCode, amount, description, cancelUrl, returnUrl, signature
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                String resStr = response.toString();
                if (resStr.contains("\"qrCode\":\"")) {
                    int start = resStr.indexOf("\"qrCode\":\"") + 10;
                    int end = resStr.indexOf("\"", start);
                    System.out.println("✅ [PayOS] LAY MA QR THANH CONG!");
                    return resStr.substring(start, end);
                }
            } else {
                System.err.println("❌ [PayOS] Server tu choi. Loi: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}