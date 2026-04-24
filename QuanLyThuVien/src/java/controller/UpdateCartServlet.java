package controller;

import model.Cart;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UpdateCartServlet", urlPatterns = {"/update-cart"})
public class UpdateCartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            HttpSession session = request.getSession();
            
            model.User acc = (model.User) session.getAttribute("acc");
            
            if (acc != null) {
                // Đã đăng nhập: Update Database, nạp lại lên Session
                dao.CartDAO cDao = new dao.CartDAO();
                cDao.updateQuantity(acc.getId(), id, quantity);
                session.setAttribute("cart", cDao.getCartByUserId(acc.getId()));
            } else {
                // Khách vãng lai: Update trực tiếp Session
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    cart.updateQuantity(id, quantity);
                }
            }
            response.sendRedirect("cart");
        } catch (Exception e) {
            response.sendRedirect("cart");
        }
    }
}