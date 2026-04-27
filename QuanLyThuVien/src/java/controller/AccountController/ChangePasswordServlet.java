package controller.AccountController;

import dao.UserDAO;
import model.User;
import utils.SecurityUtil;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");

        if (acc == null) {
            response.sendRedirect("login");
            return;
        }

        String oldPass   = request.getParameter("oldPass");
        String newPass   = request.getParameter("newPass");
        String reNewPass = request.getParameter("reNewPass");

        if (oldPass == null || newPass == null || reNewPass == null) {
            request.setAttribute("msgError", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // ✅ FIX: Hash oldPass trước khi so sánh với mật khẩu đã hash trong session
        String hashedOld = SecurityUtil.hashMD5(oldPass);
        if (!hashedOld.equals(acc.getPassword())) {
            request.setAttribute("msgError", "Mật khẩu hiện tại không đúng!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        if (!newPass.equals(reNewPass)) {
            request.setAttribute("msgError", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        if (newPass.length() < 6) {
            request.setAttribute("msgError", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // ✅ FIX: Hash newPass trước khi lưu vào DB
        String hashedNew = SecurityUtil.hashMD5(newPass);
        UserDAO dao = new UserDAO();
        boolean success = dao.changePassword(acc.getId(), hashedNew);

        if (success) {
            // ✅ FIX: Cập nhật mật khẩu đã hash vào session
            acc.setPassword(hashedNew);
            session.setAttribute("acc", acc);
            request.setAttribute("msg", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("msgError", "Có lỗi xảy ra, vui lòng thử lại sau.");
        }

        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }
}