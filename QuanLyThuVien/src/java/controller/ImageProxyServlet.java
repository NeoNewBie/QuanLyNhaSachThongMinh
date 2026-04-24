package controller;

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
            // Đóng vai trình duyệt từ trang gốc
            conn.setRequestProperty("Referer", "https://www.nettruyennew.com/");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            response.setContentType(conn.getContentType());
            try (InputStream in = conn.getInputStream(); OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            }
        } catch (Exception e) {
            // 🛑 ĐÃ FIX: Chỉ gửi lỗi 404 nếu Response CHƯA bị "chốt đơn"
            if (!response.isCommitted()) {
                response.sendError(404);
            } else {
                // Đã gửi dở dang rồi thì in nhẹ ra console cho biết thôi, không văng exception đỏ lòm nữa
                System.out.println("[ImageProxy] Lỗi tải ảnh giữa chừng, bỏ qua: " + imageUrl);
            }
        }
    }
}