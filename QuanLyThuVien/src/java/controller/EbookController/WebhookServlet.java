package controller.EbookController;

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
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("✅ Webhook Smart Lib đang hoạt động bình thường!");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        String json = sb.toString();
        
        // 1. THANH TOÁN MUA SÁCH (BILL)
        Matcher billMatcher = Pattern.compile("BILL[\\s_]*(\\d+)").matcher(json.toUpperCase());
        if (billMatcher.find()) {
            String billCode = billMatcher.group(1); 
            controller.EbookController.CheckPaymentStatusServlet.paidTransactions.add(billCode);
            System.out.println("==> Đã nhận tiền Đơn Hàng: BILL_" + billCode);
        }

        // 2. THANH TOÁN MỞ KHÓA TRUYỆN (MUACHAP)
        Matcher muaMatcher = Pattern.compile("MUACHAP[\\s_]*U[\\s_]*(\\d+)[\\s_]*C[\\s_]*(\\d+)").matcher(json.toUpperCase());
        if (muaMatcher.find()) {
            int uId = Integer.parseInt(muaMatcher.group(1));
            int cId = Integer.parseInt(muaMatcher.group(2));
            controller.EbookController.CheckPaymentStatusServlet.paidTransactions.add("U" + uId + "_C" + cId);
            new ChapterDAO().unlockChapter(uId, cId);
        }
        
        response.setStatus(200);
        response.getWriter().write("{\"success\":true}");
    }
}