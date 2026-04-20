package controller;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // 1. Lấy dữ liệu từ form
        String user = request.getParameter("user");
        String email = request.getParameter("email");
        String pass = request.getParameter("pass");
        String repass = request.getParameter("repass");
        
        // 2. Kiểm tra xác nhận mật khẩu
        if (pass == null || !pass.equals(repass)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp, sếp nhập lại nhé!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        UserDAO dao = new UserDAO();
        
        // 3. 🛑 FIX LỖI TẠI ĐÂY: Nếu checkUserExist là FALSE (chưa tồn tại) thì mới cho đăng ký
        // 🛑 TÌM DÒNG 37 VÀ SỬA LẠI THẾ NÀY:
if (!dao.checkUserExist(user)) { // Dấu ! nghĩa là "Nếu chưa tồn tại"
    
    String hashedPass = utils.SecurityUtil.hashMD5(pass);
    
    // Gọi hàm register trong bản Final mình vừa thống nhất
    boolean isSuccess = dao.register(user, hashedPass, email); 
    
    if (isSuccess) {
        response.sendRedirect("login.jsp"); 
    } else {
        request.setAttribute("error", "Lỗi lưu dữ liệu rồi sếp Hải ơi!");
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
} else {
    request.setAttribute("error", "Tên đăng nhập này đã có người xài rồi!");
    request.getRequestDispatcher("register.jsp").forward(request, response);
}
    }
}