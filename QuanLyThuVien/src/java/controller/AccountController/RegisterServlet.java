package controller.AccountController;

import dao.UserDAO;
import utils.SecurityUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String user   = request.getParameter("user");
        String email  = request.getParameter("email");
        String pass   = request.getParameter("pass");
        String repass = request.getParameter("repass");

        if (user == null || user.trim().isEmpty()) {
            request.setAttribute("error", "Tên đăng nhập không được để trống!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (pass == null || !pass.equals(repass)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (pass.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        if (!dao.checkUserExist(user)) {
            String hashedPass = SecurityUtil.hashMD5(pass);
            boolean isSuccess = dao.register(user, hashedPass, email);

            if (isSuccess) {
                // ✅ FIX: Redirect đúng sang servlet /login thay vì file login.jsp
                response.sendRedirect("login?msg=register_success");
            } else {
                request.setAttribute("error", "Lỗi hệ thống, vui lòng thử lại!");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Tên đăng nhập này đã tồn tại!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}