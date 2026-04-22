package controller;

import dao.ChapterDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/check-payment-status")
public class CheckPaymentStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        
        // desc chính là chuỗi "MUACHAP U1 C2"
        String desc = request.getParameter("orderId"); 
        
        if (desc != null && desc.contains("MUACHAP")) {
            try {
                // Tách chữ U và C ra để lấy ID
                String[] parts = desc.split(" ");
                int uId = Integer.parseInt(parts[1].replace("U", ""));
                int cId = Integer.parseInt(parts[2].replace("C", ""));
                
                ChapterDAO dao = new ChapterDAO();
                // Check xem truyện đã mở khóa chưa
                if (dao.isChapterUnlocked(uId, cId)) {
                    response.getWriter().print("success");
                    return;
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        response.getWriter().print("pending");
    }
}