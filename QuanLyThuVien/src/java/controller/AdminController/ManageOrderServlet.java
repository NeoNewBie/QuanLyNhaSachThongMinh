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
            dao.updateOrderStatus(orderId, status);
            Order currentOrder = dao.getOrderById(orderId);
            
            if (currentOrder != null) {
                String msg = "Đơn hàng #ORD-" + orderId + " đã được duyệt. Sách đã sẵn sàng chờ sếp qua lấy!";
                notiDao.addNotification(currentOrder.getUserID(), msg);

                if ("2".equals(status) || status.toLowerCase().contains("giao")) {
                    String userEmail = dao.getUserEmailByOrderId(orderId);
                    if (userEmail != null && !userEmail.isEmpty()) {
                        String subject = "Smart Lib - Đơn hàng #" + orderId + " đã sẵn sàng!";
                        String content = "Chào sếp, đơn hàng của sếp đã được thủ thư duyệt thành công. Mời sếp qua thư viện nhận sách nhé!";
                        // Gọi hàm gửi mail
                        utils.EmailUtil.sendEmail(userEmail, subject, content);
                    }
                }
            }
            
            // 🛑 NẾU THÀNH CÔNG: Gửi thông báo xanh
            request.getSession().setAttribute("sysMsg", "Duyệt đơn #" + orderId + " thành công!");
            request.getSession().setAttribute("sysMsgType", "success");
            
        } catch (Exception e) {
            e.printStackTrace();
            // 🛑 NẾU LỖI: Bắt sống lỗi và ném lên màn hình!
            request.getSession().setAttribute("sysMsg", "LỖI HỆ THỐNG: " + e.toString());
            request.getSession().setAttribute("sysMsgType", "error");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
    }
}