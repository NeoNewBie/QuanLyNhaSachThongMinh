package controller;

import dao.NotificationDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ReadNotifServlet", urlPatterns = {"/read-notif"})
public class ReadNotifServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nidStr = request.getParameter("nid");
        String targetUrl = request.getParameter("url");

        if (nidStr != null && !nidStr.isEmpty()) {
            try {
                int notifId = Integer.parseInt(nidStr);
                // Cập nhật DB thành đã đọc
                new NotificationDAO().markAsRead(notifId);
            } catch (Exception e) { e.printStackTrace(); }
        }

        // Chuyển hướng sang trang chi tiết (order-detail hoặc borrow-detail)
        if (targetUrl != null && !targetUrl.isEmpty()) {
            response.sendRedirect(targetUrl);
        } else {
            response.sendRedirect("home");
        }
    }
}