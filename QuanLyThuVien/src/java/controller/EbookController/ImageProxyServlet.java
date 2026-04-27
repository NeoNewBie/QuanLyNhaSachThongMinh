package controller.EbookController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/image-proxy")
public class ImageProxyServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String imageUrl = request.getParameter("url");
        if (imageUrl == null || imageUrl.isEmpty()) return;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // 🛑 ĐÃ FIX NÂNG CẤP: Giả danh trình duyệt Chrome xịn nhất hiện tại
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
            
            // 🛑 ĐÃ FIX: Lấy tự động tên miền gốc của tấm ảnh làm giấy thông hành (Referer)
            // Thay vì fix cứng "nettruyennew.com" (vì web nó đổi tên miền liên tục)
            String host = url.getHost();
            conn.setRequestProperty("Referer", "https://" + host + "/");
            
            // Timeout chống treo web nếu ảnh bị lỗi
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            response.setContentType(conn.getContentType());
            try (InputStream in = conn.getInputStream(); OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            }
        } catch (Exception e) {
            if (!response.isCommitted()) {
                response.sendError(404);
            } else {
                System.out.println("[ImageProxy] Lỗi tải mảnh ảnh, bỏ qua: " + imageUrl);
            }
        }
    }
}