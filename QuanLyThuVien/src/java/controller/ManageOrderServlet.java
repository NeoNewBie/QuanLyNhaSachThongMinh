package controller;

import dao.OrderDAO;
import dao.NotificationDAO;
import model.Order;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// ✅ ĐÃ SỬA: Đưa URL về đúng chuẩn /admin/manage-orders
@WebServlet(name = "ManageOrderServlet", urlPatterns = {"/manage-orders"})
public class ManageOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO dao = new OrderDAO();
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        // 1. Xử lý Duyệt nhanh qua link GET (?action=approve&id=...)
        if ("approve".equals(action) && idStr != null) {
            handleApprove(request, response, idStr, "2"); // Mặc định duyệt thành trạng thái số 2
            return;
        }

        // 2. Hiển thị danh sách đơn hàng cho Admin
        List<Order> list = dao.getAllOrders();
        request.setAttribute("listOrders", list);
        // Trỏ đúng vào file jsp trong thư mục admin
        request.getRequestDispatcher("/manage-orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) idStr = request.getParameter("ticketId");
        String status = request.getParameter("status");

        if (idStr != null) {
            handleApprove(request, response, idStr, status != null ? status : "2");
        } else {
            // ✅ ĐÃ SỬA: Chuyển hướng về lại trang admin
            response.sendRedirect(request.getContextPath() + "/manage-orders");
        }
    }

    // --- HÀM HỖ TRỢ XỬ LÝ DUYỆT ---
    private void handleApprove(HttpServletRequest request, HttpServletResponse response, String idStr, String status) 
            throws IOException {
        
        OrderDAO dao = new OrderDAO();
        NotificationDAO notiDao = new NotificationDAO();
        
        try {
            // 🛑 Xử lý ID: Xóa sạch chữ #ORD-, #LIB- để lấy đúng con số
            int orderId = Integer.parseInt(idStr.replaceAll("[^0-9]", ""));
            
            // 1. Cập nhật trạng thái trong Database
            dao.updateOrderStatus(orderId, status);

            // 2. Lấy thông tin đơn để bắn Thông báo & Email
            Order currentOrder = dao.getOrderById(orderId);
            if (currentOrder != null) {
                
                // 🔔 Bắn thông báo lên hệ thống
                String msg = "Đơn hàng #ORD-" + orderId + " đã được duyệt. Sách đã sẵn sàng chờ sếp qua lấy!";
                notiDao.addNotification(currentOrder.getUserID(), msg);
                System.out.println("🔔 Bắn thông báo thành công cho User: " + currentOrder.getUserID());

                // 📧 Gửi Email (Chỉ gửi khi trạng thái là Sẵn sàng/Đã giao)
                if ("2".equals(status) || status.toLowerCase().contains("giao")) {
                    String userEmail = dao.getUserEmailByOrderId(orderId);
                    if (userEmail != null && !userEmail.isEmpty()) {
                        String subject = "Smart Lib - Đơn hàng #" + orderId + " đã sẵn sàng!";
                        String content = "Chào sếp, đơn hàng của sếp đã được thủ thư duyệt thành công. Mời sếp qua thư viện nhận sách nhé!";
                        utils.EmailUtil.sendEmail(userEmail, subject, content);
                        System.out.println("✅ Đã gửi mail tới: " + userEmail);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi duyệt đơn: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ✅ ĐÃ SỬA: Duyệt xong quay về đúng trang danh sách admin
        response.sendRedirect(request.getContextPath() + "/manage-orders");
    }
}