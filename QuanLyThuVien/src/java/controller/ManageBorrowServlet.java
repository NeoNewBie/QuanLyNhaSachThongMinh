package controller;

import dao.BorrowDAO;
import dao.NotificationDAO;
import model.Borrow;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageBorrowServlet", urlPatterns = {"/admin/manage-borrows"})
public class ManageBorrowServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        BorrowDAO dao = new BorrowDAO();
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        try {
            if (idStr != null) {
                int borrowId = Integer.parseInt(idStr);
                
                // 1. Thủ thư bấm DUYỆT (Chuyển thành 2: Sẵn sàng tại quầy)
                if ("approve".equals(action)) {
                    dao.updateBorrowStatus(borrowId, "2");
                    
                    // 🛑 ĐÃ FIX: Tự động lấy đúng ID của khách hàng dựa vào mã Phiếu mượn
                    int customerId = dao.getUserIdByBorrowId(borrowId);
                    if (customerId > 0) {
                        String msg = "Sách mượn #LIB-" + borrowId + " đã sẵn sàng. Mời sếp qua quầy lấy!";
                        new NotificationDAO().addNotification(customerId, msg);
                    }
                    
                    request.getSession().setAttribute("sysMsg", "Đã duyệt phiếu mượn #" + borrowId);
                    request.getSession().setAttribute("sysMsgType", "success");
                }
                
                // 2. Khách ra lấy sách (Chuyển thành 3: Bắt đầu tính ngày)
                else if ("give".equals(action)) {
                    dao.updateBorrowStatus(borrowId, "3");
                    request.getSession().setAttribute("sysMsg", "Đã giao sách cho khách. Bắt đầu đếm ngày!");
                    request.getSession().setAttribute("sysMsgType", "success");
                }
                
                // 3. Khách trả sách (Chuyển thành 4: Đã trả)
                else if ("return".equals(action)) {
                    dao.updateBorrowStatus(borrowId, "4");
                    request.getSession().setAttribute("sysMsg", "Đã thu hồi sách về kho an toàn.");
                    request.getSession().setAttribute("sysMsgType", "success");
                }
                
                response.sendRedirect(request.getContextPath() + "/admin/manage-borrows");
                return;
            }
        } catch (Exception e) {
            request.getSession().setAttribute("sysMsg", "LỖI HỆ THỐNG: " + e.toString());
            request.getSession().setAttribute("sysMsgType", "error");
            response.sendRedirect(request.getContextPath() + "/admin/manage-borrows");
            return;
        }

        // Lấy danh sách hiển thị
        List<Borrow> list = dao.getAllBorrows();
        request.setAttribute("listBorrows", list);
        request.getRequestDispatcher("/admin/manage-borrows.jsp").forward(request, response);
    }
}