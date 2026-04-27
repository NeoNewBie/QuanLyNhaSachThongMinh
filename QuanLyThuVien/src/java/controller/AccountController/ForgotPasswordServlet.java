package controller.AccountController;

import dao.UserDAO;
import java.io.IOException;
import java.util.Random; // 🛑 Cần cái này để tạo mã OTP
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // 🛑 Cần cái này để lưu OTP tạm thời

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        UserDAO dao = new UserDAO();
        
        // 1. Kiểm tra xem email có tồn tại không (Hết lỗi nhờ Bước 1)
        if (dao.checkEmailExist(email)) {
            // 2. Tạo mã OTP 6 số
            Random rd = new Random();
            int otpValue = rd.nextInt(900000) + 100000;
            
            // 3. Lưu OTP vào session
            HttpSession session = request.getSession();
            session.setAttribute("otp", otpValue);
            session.setAttribute("email", email);
            
            // 4. Gửi mail (Dùng EmailUtil sếp đã nạp library lúc nãy)
            String subject = "MÃ XÁC NHẬN - SMART LIB";
            String content = "Chào sếp Hải! Mã OTP của sếp là: <b style='color:red; font-size:20px;'>" + otpValue + "</b>";
            utils.EmailUtil.sendEmail(email, subject, content);
            
            // 5. Sang trang nhập OTP (sếp nhớ tạo file verify-otp.jsp nhé)
            response.sendRedirect("verify-otp.jsp");
        } else {
            request.setAttribute("error", "Email này chưa đăng ký tài khoản sếp ơi!");
            request.getRequestDispatcher("forgot.jsp").forward(request, response);
        }
    }
}