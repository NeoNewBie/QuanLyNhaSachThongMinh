package controller;

import model.Cart;
import model.Item;
import model.Book;
import dao.BookDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddToCartAsyncServlet", urlPatterns = {"/add-to-cart-async"})
public class AddToCartAsyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // 1. Lấy ID sách (Dùng String để quăng vào DAO như file sếp gửi)
        String idStr = request.getParameter("id");
        
        // 2. Lấy số lượng từ bộ nút [-][1][+] (Mặc định là 1 nếu không có)
        int quantity = 1;
        String qtyStr = request.getParameter("quantity");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            quantity = Integer.parseInt(qtyStr);
        }
        
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        
        // 3. Gọi DAO lấy thông tin sách
        Book b = new dao.BookDAO().getBookById(idStr); 
        
        if (b != null) {
            // Tạo Item với số lượng sếp đã chọn
            Item t = new Item(b, quantity, b.getPrice());
            cart.addItem(t); 
        }
        
        session.setAttribute("cart", cart);
        
        // Trả về tổng số lượng Item để Header cập nhật số đỏ
        response.getWriter().print(cart.getItems().size());
    }
}