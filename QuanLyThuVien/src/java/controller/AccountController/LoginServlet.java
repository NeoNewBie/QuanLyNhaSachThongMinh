package controller.AccountController;

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

        request.setCharacterEncoding("UTF-8");

        String user     = request.getParameter("user");
        String passRaw  = request.getParameter("pass");
        String remember = request.getParameter("remember");

        String hashedPass = utils.SecurityUtil.hashMD5(passRaw);
        UserDAO dao = new UserDAO();
        User account = dao.login(user, hashedPass);

        if (account == null) {
            request.setAttribute("mess", "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("acc", account);
            session.setAttribute("cart", new dao.CartDAO().getCartByUserId(account.getId()));

            // ✅ FIX: Chỉ lưu username vào cookie, KHÔNG lưu mật khẩu thô
            Cookie cUser = new Cookie("cUser", user);
            Cookie cRemember = new Cookie("cRemember", remember != null ? "true" : "false");

            if (remember != null) {
                int maxAge = 7 * 24 * 60 * 60; // 7 ngày
                cUser.setMaxAge(maxAge);
                cRemember.setMaxAge(maxAge);
            } else {
                cUser.setMaxAge(0);
                cRemember.setMaxAge(0);
            }

            response.addCookie(cUser);
            response.addCookie(cRemember);

            if (account.getRoleId() == 1) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect("home");
            }
        }
    }
}