package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Hỗ trợ tiếng Việt
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");
        
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String oldPass = request.getParameter("oldPass");
        String newPass = request.getParameter("newPass");
        String reNewPass = request.getParameter("reNewPass");
        
        // 1. Kiểm tra mật khẩu cũ có đúng với Session không
        if (!oldPass.equals(acc.getPassword())) {
            request.setAttribute("msgError", "Mật khẩu hiện tại không đúng!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!newPass.equals(reNewPass)) {
            request.setAttribute("msgError", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // 3. Tiến hành đổi mật khẩu dưới Database
       UserDAO dao = new UserDAO();
        boolean success = dao.changePassword(acc.getId(), newPass);

        if (success) {
            // Cập nhật lại mật khẩu trong Session
            acc.setPassword(newPass);
            session.setAttribute("acc", acc);
            request.setAttribute("msg", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("msgError", "Có lỗi xảy ra, vui lòng thử lại sau.");
        }
        
        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }
}