package controller.CartPaymentController;

import dao.OrderDAO;
import model.Order;
import model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/orders"})
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User a = (User) session.getAttribute("acc");
        
        if (a == null) {
            response.sendRedirect("login"); // Nếu chưa login thì đá về trang đăng nhập
            return;
        }
        
        OrderDAO dao = new OrderDAO();
        List<Order> list = dao.getOrdersByUser(a.getId());
        
        // Gắn dữ liệu và đẩy sang JSP
        request.setAttribute("listO", list);
        request.getRequestDispatcher("orders.jsp").forward(request, response);
    }
}