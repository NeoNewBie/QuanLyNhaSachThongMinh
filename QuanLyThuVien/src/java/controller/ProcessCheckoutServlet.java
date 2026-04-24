package controller;

import dao.BookDAO;
import dao.OrderDAO;
import model.Book;
import model.Cart;
import model.Item;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ProcessCheckoutServlet", urlPatterns = {"/process-checkout"})
public class ProcessCheckoutServlet extends HttpServlet {

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

        try {
            String checkoutType = request.getParameter("checkoutType");
            String phuong_thuc = request.getParameter("txt_phuong_thuc"); // COD hoặc TRANSFER
            OrderDAO oDao = new OrderDAO();
            boolean success = false;

            if ("single".equals(checkoutType)) {
                String idSach = request.getParameter("txt_id_sach");
                // 🛑 Lấy số lượng thực tế từ form gửi lên (trường hợp mua ngay)
                String qtyStr = request.getParameter("quantity_mua"); 
                int qty = (qtyStr != null) ? Integer.parseInt(qtyStr) : 1;

                Book sach = new BookDAO().getBookById(idSach);
                Cart fakeCart = new Cart();
                fakeCart.addItem(new Item(sach, qty, sach.getPrice()));
                
                success = oDao.addOrder(acc, fakeCart);
            } else {
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    success = oDao.addOrder(acc, cart);
                    if (success) {
                        // 🛑 Xóa sạch giỏ hàng dưới Database và Session
                        new dao.CartDAO().clearCart(acc.getId());
                        session.removeAttribute("cart"); 
                    }
                }
            }

            if (success) {
                // Vì quét mã QR PayOS đã được xử lý bằng Popup ở checkout.jsp rồi
                // Nên lưu DB xong là cho bay thẳng ra trang Báo Thành Công!
                response.sendRedirect("order-success.jsp");
            } else {
                session.setAttribute("error", "Lưu đơn hàng thất bại!");
                response.sendRedirect("home");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}