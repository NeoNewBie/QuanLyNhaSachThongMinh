package controller;

import dao.OrderDAO;
import model.Order;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 🛑 ĐÃ SỬA: URL này sẽ khớp hoàn toàn với trang test của sếp
@WebServlet(name = "ManageOrderServlet", urlPatterns = {"/manage-order", "/admin/manage-orders"})
public class ManageOrderServlet extends HttpServlet {

    // Xử lý khi Admin bấm nút DUYỆT (từ trang test hoặc danh sách)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO dao = new OrderDAO();
        try {
            String idStr = request.getParameter("ticketId"); // hoặc "id" tùy sếp đặt ở form
            if (idStr == null) idStr = request.getParameter("id");
            
            // Xử lý ID: Xóa sạch chữ #ORD- nếu có
            int orderId = Integer.parseInt(idStr.replaceAll("[^0-9]", ""));
            String status = request.getParameter("status");
            
            // 🛑 FIX STATUS: Đã thống nhất với UI là dùng số "2" (Sẵn sàng nhận) 
            if (status == null) status = "2"; 

            // 1. Cập nhật DB
            dao.updateOrderStatus(orderId, status);

            // ==========================================
            // 🔔 GẮN NGÒI NỔ CHUÔNG THÔNG BÁO TẠI ĐÂY 🔔
            // ==========================================
            Order currentOrder = dao.getOrderById(orderId);
            if (currentOrder != null) {
                String notifMsg = "Tin chuẩn chưa chỉnh: Phiếu mượn #LIB-" + orderId + " đã được duyệt! Chuẩn bị qua thư viện nhận sách nhé sếp.";
                new dao.NotificationDAO().addNotification(currentOrder.getUserID(), notifMsg);
                System.out.println("🔔 ĐÃ BẮN THÔNG BÁO LÊN WEB CHO USER ID: " + currentOrder.getUserID());
            }
            // ==========================================

            // 2. Gửi mail nếu trạng thái là "Đã giao" hoặc Sẵn sàng (số 2)
            if (status.contains("giao") || status.equals("2")) {
                String userEmail = dao.getUserEmailByOrderId(orderId);
                
                // 🛑 Kiểm tra Email có tồn tại không để tránh lỗi NullPointer
                if (userEmail != null && !userEmail.isEmpty()) {
                    String subject = "Smart Lib - Sach cua sep Hai da san sang!";
                    String content = "Đơn hàng #LIB-" + orderId + " đã sẵn sàng. Qua lấy nhé sếp!";
                    utils.EmailUtil.sendEmail(userEmail, subject, content);
                    System.out.println("✅ ĐÃ GỬI MAIL CHO: " + userEmail);
                } else {
                    System.out.println("⚠️ KHÔNG GỬI ĐƯỢC: Đơn hàng này không có Email người nhận.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Duyệt xong quay về trang danh sách
        response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
    }

    // Xử lý khi Admin vào xem danh sách đơn hàng
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO dao = new OrderDAO();
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        // Nếu là bấm link Duyệt nhanh (?action=approve)
        if ("approve".equals(action) && idStr != null) {
            doPost(request, response); // Gọi sang hàm doPost để xử lý cho gọn
            return;
        }

        // Hiển thị danh sách như cũ của sếp
        List<Order> list = dao.getAllOrders();
        request.setAttribute("listOrders", list);
        request.getRequestDispatcher("/admin/manage-orders.jsp").forward(request, response);
    }
}