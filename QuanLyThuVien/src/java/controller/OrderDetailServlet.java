package controller;

import dao.OrderDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Item;
import model.Order;
import model.User;

@WebServlet(name = "OrderDetailServlet", urlPatterns = {"/order-detail"})
public class OrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");
        
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            OrderDAO dao = new OrderDAO();
            
            Order order = dao.getOrderById(orderId);
            List<Item> details = dao.getOrderDetails(orderId);
            
            // Kiểm tra bảo mật
            if (order != null && (order.getUserID() == acc.getId() || acc.getRoleId() == 1)) {
                request.setAttribute("order", order);
                request.setAttribute("details", details); 
                request.getRequestDispatcher("order-detail.jsp").forward(request, response);
            } else {
                // 🛑 BẢO HIỂM: Chỉ chuyển hướng nếu web chưa vẽ gì
                if (!response.isCommitted()) response.sendRedirect("orders"); 
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi thật ra console để dễ bắt bệnh
            // 🛑 BẢO HIỂM: Cứu tinh của lỗi IllegalStateException
            if (!response.isCommitted()) {
                response.sendRedirect("orders");
            }
        }
    }
}