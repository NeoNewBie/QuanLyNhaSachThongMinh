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

        // 🛑 ĐÃ FIX: Hút mã BILL bất chấp ngân hàng xóa dấu _ hay chèn rác
        // Nhận diện "BILL447764", "BILL 447764", "BILL_447764"
        Matcher billMatcher = Pattern.compile("BILL[\\s_]*(\\d+)").matcher(json.toUpperCase());
        if (billMatcher.find()) {
            // Ném cái số (ví dụ: 447764) vào kho tạm
            String billCode = billMatcher.group(1); 
            controller.CheckPaymentStatusServlet.paidTransactions.add(billCode);
            System.out.println("==> Đã đẩy mã Đơn Hàng vào kho tạm: BILL_" + billCode);
        }

        // 🛑 ĐÃ FIX: Hút mã MUACHAP bất chấp rác
        // Nhận diện "MUACHAP U1 C2", "MUACHAPU1C2", v.v.
        Matcher muaMatcher = Pattern.compile("MUACHAP[\\s_]*U[\\s_]*(\\d+)[\\s_]*C[\\s_]*(\\d+)").matcher(json.toUpperCase());
        if (muaMatcher.find()) {
            int uId = Integer.parseInt(muaMatcher.group(1));
            int cId = Integer.parseInt(muaMatcher.group(2));
            
            // Ném định dạng "U1_C2" vào kho tạm
            controller.CheckPaymentStatusServlet.paidTransactions.add("U" + uId + "_C" + cId);
            
            // Gọi DAO mở khóa trong Database
            new ChapterDAO().unlockChapter(uId, cId);
            System.out.println("==> ĐÃ MỞ KHÓA CHƯƠNG " + cId + " CHO SẾP HẢI!");
        }
        
        // 4. Báo cho PayOS là đã nhận tin thành công
        response.setStatus(200);
        response.getWriter().write("{\"success\":true}");
    }
}