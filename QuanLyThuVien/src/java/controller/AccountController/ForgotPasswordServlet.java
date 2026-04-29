package controller.AccountController;

import dao.UserDAO;
import java.io.IOException;
import java.util.Random; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

// 🛑 ĐÃ FIX: Cho Servlet này đón lõng cả 2 đường link
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {
    
    // 🛑 ĐÃ FIX: Thêm hàm doGet để mở cửa cho khách vào xem giao diện (Khi bấm link)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Dẫn khách vào trang forgot.jsp
        request.getRequestDispatcher("forgot.jsp").forward(request, response);
    }

    // Hàm doPost cũ của sếp giữ nguyên (Dùng khi khách bấm nút GỬI FORM)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        UserDAO dao = new UserDAO();
        
        if (dao.checkEmailExist(email)) {
            Random rd = new Random();
            int otpValue = rd.nextInt(900000) + 100000;
            
            HttpSession session = request.getSession();
            session.setAttribute("otp", otpValue);
            session.setAttribute("email", email);
            
            String subject = "MÃ XÁC NHẬN - SMART LIB";
            String content = "Chào sếp Hải! Mã OTP của sếp là: <b style='color:red; font-size:20px;'>" + otpValue + "</b>";
            utils.EmailUtil.sendEmail(email, subject, content);
            
            response.sendRedirect("verify-otp");
        } else {
            request.setAttribute("error", "Email này chưa đăng ký tài khoản sếp ơi!");
            request.getRequestDispatcher("forgot.jsp").forward(request, response);
        }
    }
}