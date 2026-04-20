package controller;

import dao.BookDAO;
import model.Book;
import model.Cart;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("acc") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        
        // 🛑 LẤY SỐ LƯỢNG KHÁCH CHỌN (Mặc định là 1 nếu không truyền)
        String qtyStr = request.getParameter("quantity");
        int quantity = 1;
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (Exception e) {
                quantity = 1;
            }
        }

        double totalAmount = 0;

        // TRƯỜNG HỢP 1: Khách bấm "MUA NGAY" (Mua thẳng từ trang chi tiết)
        if (idStr != null && !idStr.isEmpty()) {
            BookDAO dao = new BookDAO();
            Book b = dao.getBookById(idStr);
            if (b != null) {
                request.setAttribute("checkoutType", "single");
                request.setAttribute("sach_mua", b);
                request.setAttribute("quantity_mua", quantity); // Gửi số lượng sang JSP
                
                // 🛑 QUAN TRỌNG: Tổng tiền = Giá x Số lượng
                totalAmount = b.getPrice() * quantity;
            }
        } 
        // TRƯỜNG HỢP 2: Khách bấm "THANH TOÁN" từ Giỏ Hàng
        else {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null && !cart.getItems().isEmpty()) {
                request.setAttribute("checkoutType", "cart");
                totalAmount = cart.getTotalMoney();
            } else {
                response.sendRedirect("shop");
                return;
            }
        }

        request.setAttribute("totalAmount", totalAmount);
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }
}