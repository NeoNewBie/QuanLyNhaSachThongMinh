package controller.AccountController;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu chưa có email trong session thì không cho vào trang này
        HttpSession session = request.getSession();
        if (session.getAttribute("email") == null) {
            response.sendRedirect("forgot");
            return;
        }
        request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String o1 = request.getParameter("o1");
        String o2 = request.getParameter("o2");
        String o3 = request.getParameter("o3");
        String o4 = request.getParameter("o4");
        String o5 = request.getParameter("o5");
        String o6 = request.getParameter("o6");

        if (o1==null||o2==null||o3==null||o4==null||o5==null||o6==null) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mã OTP!");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
            return;
        }

        String otpInput = o1 + o2 + o3 + o4 + o5 + o6;
        HttpSession session = request.getSession();
        Integer otpSystem = (Integer) session.getAttribute("otp");

        if (otpSystem != null && otpInput.equals(String.valueOf(otpSystem))) {
            // ✅ FIX: Xóa OTP khỏi session ngay sau khi verify thành công
            session.removeAttribute("otp");
            // Đánh dấu đã verify để ResetPasswordServlet biết
            session.setAttribute("otpVerified", true);
            response.sendRedirect("reset-password");
        } else {
            request.setAttribute("error", "Mã xác thực không chính xác, vui lòng thử lại!");
            request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
        }
    }
}