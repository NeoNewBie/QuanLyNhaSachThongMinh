package controller;

import dao.BookDAO;
import dao.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Khởi tạo các DAO (Đã sửa lại cách gọi cho chuẩn)
        OrderDAO oDao = new OrderDAO();
        BookDAO bDao = new BookDAO();
        
        // Lấy dữ liệu thật từ DB
        double revenue = oDao.getTotalRevenue();
        int orders = oDao.countTotalOrders();
        int books = bDao.countTotalBooks();
        
        // Đẩy dữ liệu sang JSP
        request.setAttribute("totalRevenue", revenue);
        request.setAttribute("totalOrders", orders);
        request.setAttribute("totalBooks", books);
        
        // Chuyển hướng đến trang dashboard
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}