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
            String phuongThuc = request.getParameter("txt_phuong_thuc"); // Bắt lấy "COD" hoặc "TRANSFER"
            OrderDAO oDao = new OrderDAO();
            boolean success = false;

            // 1. LƯU ĐƠN VÀO DATABASE (Mặc định Status = 0)
            if ("single".equals(checkoutType)) {
                String idSach = request.getParameter("txt_id_sach");
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
                        new dao.CartDAO().clearCart(acc.getId());
                        session.removeAttribute("cart"); 
                    }
                }
            }

            // 2. CHUYỂN HƯỚNG VÀ XỬ LÝ TRẠNG THÁI THANH TOÁN
            if (success) {
                // 🛑 NẾU LÀ CHUYỂN KHOẢN (QR): GỌI HÀM NINJA ĐỔI THÀNH ĐÃ THANH TOÁN
                if ("TRANSFER".equals(phuongThuc)) {
                    oDao.updateLatestOrderToPaid(acc.getId());
                }
                
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