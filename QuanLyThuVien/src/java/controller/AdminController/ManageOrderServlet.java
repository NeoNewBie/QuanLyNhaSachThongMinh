package controller.AdminController;

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

@WebServlet(name = "ManageOrderServlet", urlPatterns = {"/admin/manage-orders"})
public class ManageOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO dao = new OrderDAO();
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if ("approve".equals(action) && idStr != null) {
            handleApprove(request, response, idStr, "2"); 
            return;
        }

        List<Order> list = dao.getAllOrders();
        request.setAttribute("listOrders", list);
        request.getRequestDispatcher("/admin/manage-orders.jsp").forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
        }
    }

    private void handleApprove(HttpServletRequest request, HttpServletResponse response, String idStr, String status) 
            throws IOException {
        OrderDAO dao = new OrderDAO();
        NotificationDAO notiDao = new NotificationDAO();
        int orderId = 0;
        
        try {
            orderId = Integer.parseInt(idStr.replaceAll("[^0-9]", ""));
            
            // 1. Lấy đơn hàng ra check xem có phải QR không
            Order currentOrder = dao.getOrderById(orderId);
            boolean isQR = (currentOrder != null && (currentOrder.getStatus().equals("1") || currentOrder.getStatus().contains("QR")));
            
            // 2. Nếu là QR thì phải ép thêm đuôi "_QR" để web không bị mất trí nhớ
            String finalStatus = status;
            if (isQR && !finalStatus.contains("QR")) {
                finalStatus = finalStatus + "_QR";
            }
            
            // 3. Update vào DB (Lúc này đơn QR sẽ lưu là 2_QR)
            dao.updateOrderStatus(orderId, finalStatus);
            
            if (currentOrder != null) {
                String msg = "Đơn hàng #ORD-" + orderId + " đã được duyệt. Đơn hàng đang vận chuyển đến địa chỉ của bạn!";
                notiDao.addNotification(currentOrder.getUserID(), msg);

                // 4. Gửi Mail cho luồng QR
                if (isQR && (finalStatus.startsWith("2") || finalStatus.toLowerCase().contains("giao"))) {
                    String userEmail = dao.getUserEmailByOrderId(orderId);
                    if (userEmail != null && !userEmail.isEmpty()) {
                        String subject = "Smart Lib - Đơn hàng #" + orderId + " đã sẵn sàng!";
                        String content = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                                "<h2 style='color: #173F5F;'>SÁCH ĐÃ SẴN SÀNG!</h2>" +
                                "<p>Chào sếp, đơn hàng <b>#ORD-" + orderId + "</b> (đã thanh toán qua mã QR) đã được thủ thư duyệt và đóng gói cẩn thận.</p>" +
                                "<p>Đơn hàng đang vận chuyển đến địa chỉ của bạn, bạn đợi shipper giao hàng tới nhé!</p>" +
                                "<hr><small>Đây là thư tự động từ hệ thống Smart Library.</small></div>";
                        
                        utils.EmailUtil.sendEmail(userEmail, subject, content);
                    }
                }
            }
            
            request.getSession().setAttribute("sysMsg", "Duyệt đơn #" + orderId + " thành công!");
            request.getSession().setAttribute("sysMsgType", "success");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("sysMsg", "LỖI HỆ THỐNG: " + e.toString());
            request.getSession().setAttribute("sysMsgType", "error");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
    }
}