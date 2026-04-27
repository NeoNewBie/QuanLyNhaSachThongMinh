package controller.AccountController;

import dao.UserDAO;
import model.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
// 🛑 Cấu hình để Servlet có thể nhận file (Ảnh đại diện)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        if (request.getSession().getAttribute("acc") == null) {
            response.sendRedirect("login.jsp");
        } else {
            request.getRequestDispatcher("profile.jsp").forward(request, response);
        }
    }

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

    String fullName = request.getParameter("fullname");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    
    // 🛑 QUAN TRỌNG: Mặc định giữ lại đường dẫn ảnh cũ nếu không có ảnh mới
    String avatarPath = acc.getAvatar(); 

    try {
        Part part = request.getPart("avatarFile");
        if (part != null && part.getSize() > 0) {
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String newFileName = "user" + acc.getId() + "_at_" + System.currentTimeMillis() + "_" + fileName;
            
            // Đường dẫn lưu file
            String uploadPath = getServletContext().getRealPath("/") + "assets" + File.separator + "img" + File.separator + "avatars";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            part.write(uploadPath + File.separator + newFileName);
            
            // Cập nhật đường dẫn mới
            avatarPath = "/assets/img/avatars/" + newFileName;
        }
    } catch (Exception e) { e.printStackTrace(); }

    // 2. Gọi DAO cập nhật DB
    UserDAO dao = new UserDAO();
    dao.updateProfile(acc.getId(), fullName, email, phone, address, avatarPath);

    // 3. Cập nhật lại đối tượng acc trong Session để trang JSP hiển thị ngay
    acc.setFullName(fullName);
    acc.setEmail(email);
    acc.setPhone(phone);
    acc.setAddress(address);
    acc.setAvatar(avatarPath); // Gán avatar (mới hoặc cũ) vào lại acc
    
    session.setAttribute("acc", acc);
    
    request.setAttribute("msg", "Cập nhật hồ sơ thành công rồi sếp Hải ơi! 🎉");
    request.getRequestDispatcher("profile.jsp").forward(request, response);
}
}