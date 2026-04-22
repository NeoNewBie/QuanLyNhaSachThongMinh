package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 🛑 BỔ SUNG: Xử lý Tiếng Việt để không bị lỗi font khi nhận dữ liệu
        request.setCharacterEncoding("UTF-8");
        
        String user = request.getParameter("user");
        String passRaw = request.getParameter("pass"); 
        String remember = request.getParameter("remember"); 
        
        // 🛑 Băm mật khẩu MD5
        String hashedPass = utils.SecurityUtil.hashMD5(passRaw);
        
        UserDAO dao = new UserDAO();
        
        // 🛑 FIX LỖI TẠI ĐÂY: Đổi checkLogin thành login cho khớp với UserDAO mới
        User account = dao.login(user, hashedPass); 
        
        if (account == null) {
            // Sai mật khẩu
            request.setAttribute("mess", "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Đúng mật khẩu -> Lưu Session
            HttpSession session = request.getSession();
            session.setAttribute("acc", account);
            
            // ==========================================
            // LOGIC COOKIE (REMEMBER ME)
            // ==========================================
            Cookie cUser = new Cookie("cUser", user);
            Cookie cPass = new Cookie("cPass", passRaw);
            Cookie cRemember = new Cookie("cRemember", remember);
            
            if (remember != null) {
                int maxAge = 7 * 24 * 60 * 60; // 7 ngày
                cUser.setMaxAge(maxAge);
                cPass.setMaxAge(maxAge);
                cRemember.setMaxAge(maxAge);
            } else {
                cUser.setMaxAge(0);
                cPass.setMaxAge(0);
                cRemember.setMaxAge(0);
            }
            
            response.addCookie(cUser);
            response.addCookie(cPass);
            response.addCookie(cRemember);
            
            // ==========================================
            // ĐIỀU HƯỚNG THEO QUYỀN (ROLE)
            // ==========================================
            if (account.getRoleId() == 1) {
                // Nếu là Admin -> Vào trang Dashboard quản trị
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // Nếu là Khách hàng -> Về trang chủ
                response.sendRedirect("home"); 
            }
        }
    }
}