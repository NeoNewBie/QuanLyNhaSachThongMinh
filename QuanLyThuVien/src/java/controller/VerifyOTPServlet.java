package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "VerifyOTPServlet", urlPatterns = {"/verify-otp"})
public class VerifyOTPServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 🛑 Gom 6 ô input o1 -> o6 lại thành 1 mã OTP hoàn chỉnh
        String o1 = request.getParameter("o1");
        String o2 = request.getParameter("o2");
        String o3 = request.getParameter("o3");
        String o4 = request.getParameter("o4");
        String o5 = request.getParameter("o5");
        String o6 = request.getParameter("o6");

        String otpInput = o1 + o2 + o3 + o4 + o5 + o6; // Gom lại thành 6 số
        
        HttpSession session = request.getSession();
        Integer otpSystem = (Integer) session.getAttribute("otp");
        
        // Kiểm tra xem mã nhập vào có khớp với mã hệ thống gửi mail không
        if (otpSystem != null && otpInput.equals(String.valueOf(otpSystem))) {
            // Đúng mã -> Cho sếp sang trang đặt mật khẩu mới (reset-password.jsp)
            response.sendRedirect("reset-password.jsp");
        } else {
            // Sai mã -> Báo lỗi và quay lại trang nhập
            request.setAttribute("error", "Mã xác thực không chính xác, sếp Hải nhập lại nhé!");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
        }
    }
}