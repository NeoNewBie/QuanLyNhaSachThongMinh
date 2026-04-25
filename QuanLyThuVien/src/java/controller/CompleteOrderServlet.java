package controller;

import dao.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CompleteOrderServlet", urlPatterns = {"/complete-order"})
public class CompleteOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Bắt lấy cái ID đơn hàng từ URL (ví dụ: id=19)
        String orderId = request.getParameter("id");
        
        if (orderId != null && !orderId.isEmpty()) {
            // 2. Gọi Ninja OrderDAO ra chốt đơn (Đổi Status = 4)
            OrderDAO dao = new OrderDAO();
            boolean isSuccess = dao.completeOrder(orderId);
            
            // 3. Bơm một câu thông báo vào Session để tí hiện Popup SweetAlert
            if(isSuccess) {
                request.getSession().setAttribute("cartMsg", "Chốt đơn thành công! Cảm ơn sếp đã ủng hộ!");
            }
        }
        
        // 4. "Đá" người dùng quay ngược lại đúng cái trang chi tiết đơn hàng đó
        response.sendRedirect("order-detail?id=" + orderId);
    }
}