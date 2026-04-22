package controller;

import dao.ChapterDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/webhook/payos")
public class WebhookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Đọc dữ liệu PayOS bắn về
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        String json = sb.toString();
        
        System.out.println("PayOS gửi tin: " + json);

        // 2. Kiểm tra nếu có mã MUACHAP trong phần mô tả (description)
        if (json.contains("MUACHAP")) {
            // Regex lấy ID User (U) và ID Chapter (C)
            Pattern p = Pattern.compile("MUACHAP U(\\d+) C(\\d+)");
            Matcher m = p.matcher(json.toUpperCase());

            if (m.find()) {
                int uId = Integer.parseInt(m.group(1));
                int cId = Integer.parseInt(m.group(2));
                
                // 3. Gọi DAO mở khóa trong Database
                new ChapterDAO().unlockChapter(uId, cId);
                System.out.println("==> ĐÃ MỞ KHÓA CHƯƠNG " + cId + " CHO SẾP HẢI!");
            }
        }
        
        // 4. Báo cho PayOS là đã nhận tin thành công (Bắt buộc phải có)
        response.setStatus(200);
        response.getWriter().write("{\"success\":true}");
    }
}