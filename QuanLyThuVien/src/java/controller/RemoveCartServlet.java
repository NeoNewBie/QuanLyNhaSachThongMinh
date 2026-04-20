package controller;

import model.Cart;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "RemoveCartServlet", urlPatterns = {"/remove-cart"})
public class RemoveCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy ID của cuốn sách cần xóa
        String idRaw = request.getParameter("id");
        
        try {
            int id = Integer.parseInt(idRaw);
            
            // 2. Lấy Giỏ hàng hiện tại từ Session
            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            
            // 3. Nếu có giỏ hàng, tiến hành xóa sách
            if (cart != null) {
                cart.removeItem(id);
                // Cập nhật lại giỏ hàng vào Session
                session.setAttribute("cart", cart);
            }
        } catch (Exception e) {
            System.out.println("Lỗi xóa giỏ hàng: " + e.getMessage());
        }
        
        // 4. Quay lại trang giỏ hàng để xem kết quả mới
        response.sendRedirect("cart");
    }
}