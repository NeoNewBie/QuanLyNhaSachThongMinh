package controller.AccountController;

import dao.UserDAO;
import java.io.File;
import model.User;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession();
    User acc = (User) session.getAttribute("acc");

    if (acc == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // 1. Lấy dữ liệu chữ từ form
    String fullname = request.getParameter("fullname");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    int id = acc.getId();

    // 🛑 2. ĐÂY LÀ CHỖ THIẾU CỦA SẾP: Xử lý để lấy biến avatarPath
    String avatarPath = acc.getAvatar(); // Mặc định lấy ảnh cũ trong session

    try {
        Part part = request.getPart("avatarFile"); // Tên phải khớp với name="avatarFile" bên JSP
        if (part != null && part.getSize() > 0) {
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String newFileName = "user" + id + "_img_" + System.currentTimeMillis() + "_" + fileName;
            
            // Đường dẫn lưu file vật lý
            String uploadPath = getServletContext().getRealPath("/") + "assets/img/avatars";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            part.write(uploadPath + File.separator + newFileName);
            
            // Đường dẫn lưu vào Database
            avatarPath = "/assets/img/avatars/" + newFileName;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    // 3. Gọi DAO lưu xuống Database (Nhớ truyền avatarPath vào cuối)
    UserDAO dao = new UserDAO();
    boolean success = dao.updateProfile(id, fullname, email, phone, address, avatarPath);

    if (success) {
        // Cập nhật lại "Chứng minh thư" (Session) để bên trái màn hình đổi luôn dữ liệu
        acc.setFullName(fullname);
        acc.setEmail(email);
        acc.setPhone(phone);
        acc.setAddress(address);
        acc.setAvatar(avatarPath);
        
        session.setAttribute("acc", acc);
        request.setAttribute("msg", "Tuyệt vời! Cập nhật hồ sơ thành công sếp Hải nhé.");
    } else {
        request.setAttribute("msgError", "Lỗi rồi sếp ơi, kiểm tra lại SQL giúp tôi!");
    }

    request.getRequestDispatcher("profile.jsp").forward(request, response);
}
}