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

// ⚠️ DÒNG NÀY CỰC QUAN TRỌNG: Phải khớp với cái link ở JSP
@WebServlet(name = "RemoveWishlistServlet", urlPatterns = {"/remove-wishlist"})
public class RemoveWishlistServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User a = (User) session.getAttribute("acc");
        
        // 1. Kiểm tra đăng nhập
        if (a == null) {
            response.sendRedirect("login");
            return;
        }

        // 2. Lấy mã sách cần xóa (bid)
        String bookIdStr = request.getParameter("bid");
        
        if (bookIdStr != null) {
            try {
                int bId = Integer.parseInt(bookIdStr);
                WishlistDAO dao = new WishlistDAO();
                
                // 3. Thực hiện xóa trong Database
                dao.removeFromWishlist(a.getId(), bId);
                
                // 4. Bắn thông báo về trang wishlist
                session.setAttribute("wishMsg", "Đã gỡ sách khỏi danh sách yêu thích!");
                session.setAttribute("wishType", "success");
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        // 5. Quay lại trang danh sách yêu thích để thấy kết quả
        response.sendRedirect("wishlist");
    }
}