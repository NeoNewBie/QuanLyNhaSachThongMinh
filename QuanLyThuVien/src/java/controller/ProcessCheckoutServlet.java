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
            String phuong_thuc = request.getParameter("txt_phuong_thuc");
            OrderDAO oDao = new OrderDAO();
            boolean success = false;
            double totalAmount = 0;

            if ("single".equals(checkoutType)) {
                String idSach = request.getParameter("txt_id_sach");
                Book sach = new BookDAO().getBookById(idSach);
                Cart fakeCart = new Cart();
                fakeCart.addItem(new Item(sach, 1, sach.getPrice()));
                success = oDao.addOrder(acc, fakeCart);
                totalAmount = sach.getPrice();
            } else {
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    success = oDao.addOrder(acc, cart);
                    totalAmount = cart.getTotalMoney();
                    if (success) {
                        session.removeAttribute("cart"); // 🛑 XÓA GIỎ SAU KHI MUA TỪ GIỎ
                    }
                }
            }

            if (success) {
                if ("TRANSFER".equals(phuong_thuc)) {
                    request.setAttribute("totalAmount", totalAmount);
                    request.getRequestDispatcher("payment-qr.jsp").forward(request, response);
                } else {
                    // 🛑 ĐÁ SANG TRANG THÀNH CÔNG (ORDER-SUCCESS)
                    response.sendRedirect("order-success.jsp");
                }
            } else {
                response.sendRedirect("home");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}