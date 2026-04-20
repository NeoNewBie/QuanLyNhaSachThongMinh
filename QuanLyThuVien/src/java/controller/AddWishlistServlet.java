package controller;

import dao.WishlistDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddWishlistServlet", urlPatterns = {"/add-wishlist"})
public class AddWishlistServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User a = (User) session.getAttribute("acc");
        
        if (a == null) {
            response.sendRedirect("login");
            return;
        }

        // 2. Lấy mã sách từ URL (?bid=...)
        String bookIdStr = request.getParameter("bid");
        
        if (bookIdStr != null) {
            try {
                int bId = Integer.parseInt(bookIdStr);
                WishlistDAO dao = new WishlistDAO();
                
                // Gọi hàm addToWishlist (Hàm này trả về 1 nếu thêm mới, 0 nếu đã có)
                int result = dao.addToWishlist(a.getId(), bId);

                // 3. Thiết lập thông báo dựa trên kết quả
                if (result == 1) {
                    session.setAttribute("wishMsg", "Đã thêm vào danh sách yêu thích!");
                    session.setAttribute("wishType", "success");
                } else if (result == 0) {
                    session.setAttribute("wishMsg", "Sách này đã có trong yêu thích của Hải rồi nhé!");
                    session.setAttribute("wishType", "info");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // 4. Quay lại trang trước đó (Detail hoặc Home) để hiện thông báo SweetAlert
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "home");
    }
}