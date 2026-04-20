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

@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

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

        // Lấy dữ liệu từ form gửi lên
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        int id = acc.getId(); // Hàm lấy ID theo đúng model của ông

        // Gọi DAO lưu xuống Database
        UserDAO dao = new UserDAO();
        boolean success = dao.updateProfile(id, fullname, email, phone, address);

        if (success) {
            // Cập nhật lại Session để màn hình bên trái load dữ liệu mới ngay lập tức
            acc.setFullName(fullname); // Dùng đúng chữ N hoa theo model của ông
            acc.setEmail(email);
            acc.setPhone(phone);
            acc.setAddress(address);
            
            session.setAttribute("acc", acc);
            
            request.setAttribute("msg", "Tuyệt vời! Cập nhật thông tin thành công.");
        } else {
            request.setAttribute("msgError", "Cập nhật thất bại! Vui lòng kiểm tra lại kết nối Database.");
        }
        
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}