package controller.AccountController;

import dao.UserDAO;
import utils.SecurityUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {
    // 🛑 THÊM HÀM NÀY VÀO ĐỂ KHÁCH VÀO XEM ĐƯỢC GIAO DIỆN
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra xem có email trong session không (chống đi cửa sau)
        if (request.getSession().getAttribute("email") == null) {
            response.sendRedirect("login");
        } else {
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String newPass = request.getParameter("new_pass");
        String rePass = request.getParameter("re_pass");
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        // 1. Kiểm tra 2 mật khẩu có khớp nhau không
        if (newPass != null && newPass.equals(rePass)) {
            // 2. MÃ HÓA mật khẩu mới thành MD5
            String hashedPass = SecurityUtil.hashMD5(newPass);
            
            // 3. Cập nhật vào Database
            UserDAO dao = new UserDAO();
            boolean success = dao.updatePasswordByEmail(email, hashedPass);
            
            if (success) {
                // Thành công -> Xóa hết session cũ và về login
                session.invalidate(); 
                response.sendRedirect("login.jsp?msg=reset_success");
            } else {
                request.setAttribute("error", "Lỗi cập nhật Database, sếp thử lại nhé!");
                request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
        }
    }
}