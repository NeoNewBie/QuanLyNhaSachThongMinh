package controller.CartPaymentController;

import dao.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

@WebServlet(name = "CancelOrderServlet", urlPatterns = {"/cancel-order"})
public class CancelOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra đăng nhập bảo mật
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");
        
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 2. Lấy ID của đơn hàng cần hủy từ URL
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                int orderId = Integer.parseInt(idParam);
                
                OrderDAO dao = new OrderDAO();
                
                // 🛑 FIX QUAN TRỌNG: Cập nhật trạng thái thành "5" (Code của Đã hủy)
                dao.updateOrderStatus(orderId, "5");
                
                // 4. Tạo thông báo thành công (Đổi #ORD thành #LIB cho khớp UI mới)
                session.setAttribute("msg", "Đã hủy phiếu mượn #LIB-" + orderId + " thành công!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi hủy đơn: " + e.getMessage());
            // Nếu có lỗi thì báo lỗi
            session.setAttribute("error", "Lỗi hủy đơn, sếp thử lại sau nhé!");
        }
        
        // Quay lại trang Lịch sử đơn hàng để xem sự thay đổi
        response.sendRedirect("orders");
    }
}