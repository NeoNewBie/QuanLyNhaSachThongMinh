package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie; // 🛑 ĐÃ THÊM IMPORT NÀY ĐỂ DÙNG COOKIE
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    // doGet để hiển thị trang giao diện login.jsp
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    // doPost để nhận dữ liệu khi người dùng bấm nút Đăng nhập
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String user = request.getParameter("user");
        String passRaw = request.getParameter("pass"); // Pass khách gõ
        String remember = request.getParameter("remember"); // Lấy trạng thái ô tick Ghi nhớ
        
        // 🛑 Băm mật khẩu khách gõ ra, rồi mới đem đi check
        String hashedPass = utils.SecurityUtil.hashMD5(passRaw);
        
        UserDAO dao = new UserDAO();
        User account = dao.checkLogin(user, hashedPass); // Truyền pass đã băm
        
        // 3. Xử lý kết quả
        if (account == null) {
            // Sai mật khẩu -> Gửi thông báo lỗi về lại trang login
            request.setAttribute("mess", "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Đúng mật khẩu -> Lưu thông tin vào Session
            HttpSession session = request.getSession();
            session.setAttribute("acc", account);
            
            // ==========================================
            // 🚀 BẮT ĐẦU LOGIC COOKIE (REMEMBER ME)
            // ==========================================
            
            // Tạo 3 thẻ Cookie (Lưu passRaw chưa băm để form HTML in ra đúng ký tự gốc)
            Cookie cUser = new Cookie("cUser", user);
            Cookie cPass = new Cookie("cPass", passRaw);
            Cookie cRemember = new Cookie("cRemember", remember);
            
            if (remember != null) {
                // Nếu khách có tick -> Cho Cookie sống 7 ngày (7 ngày * 24h * 60p * 60s)
                int maxAge = 7 * 24 * 60 * 60;
                cUser.setMaxAge(maxAge);
                cPass.setMaxAge(maxAge);
                cRemember.setMaxAge(maxAge);
            } else {
                // Nếu khách KHÔNG tick -> Xóa Cookie luôn (Set tuổi thọ = 0)
                cUser.setMaxAge(0);
                cPass.setMaxAge(0);
                cRemember.setMaxAge(0);
            }
            
            // Trả Cookie về để trình duyệt của khách hàng lưu lại
            response.addCookie(cUser);
            response.addCookie(cPass);
            response.addCookie(cRemember);
            
            // ==========================================
            
            // TÍNH NĂNG NÂNG CẤP: ĐIỀU HƯỚNG TỰ ĐỘNG THEO QUYỀN (ROLE)
            if (account.getRoleId() == 1) {
                // Nếu là Admin -> Bay thẳng vào trang Dashboard (Dùng getContextPath để tránh lỗi đường dẫn)
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // Nếu là Khách hàng (Role = 2) -> Quay về trang chủ
                response.sendRedirect("home"); 
            }
        }
    }
}