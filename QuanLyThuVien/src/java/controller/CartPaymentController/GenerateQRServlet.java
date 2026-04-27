package controller.CartPaymentController;

import utils.PayOSUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GenerateQRServlet", urlPatterns = {"/generate-qr"})
public class GenerateQRServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int amount = (int) Double.parseDouble(request.getParameter("amount"));
        String desc = request.getParameter("desc"); // Nhận chuỗi "MUACHAP U1 C2"

        // PayOS bắt buộc phải có 1 mã OrderCode ngẫu nhiên (chỉ là con số)
        int orderCode = (int) (System.currentTimeMillis() % 1000000000);

        // Lấy mã QR bảo mật từ PayOS
        String qrData = PayOSUtil.getQRCode(orderCode, amount, desc);

        response.setContentType("text/plain");
        response.getWriter().write(qrData != null ? qrData : "error");
    }
}