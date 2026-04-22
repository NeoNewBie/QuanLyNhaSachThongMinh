package controller;

import dao.BorrowDAO;
import model.Borrow;
import model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "BorrowHistoryServlet", urlPatterns = {"/borrow-history"})
public class BorrowHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");

        // 1. Kiểm tra đăng nhập
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Lấy danh sách từ DAO
        BorrowDAO dao = new BorrowDAO();
        // Lấy lịch sử mượn của đúng ông Hải đang đăng nhập
        List<Borrow> list = dao.getBorrowHistory(acc.getId()); 
        
        // 3. Đẩy list sang JSP
        request.setAttribute("listB", list);

        // 4. Mở trang giao diện
        request.getRequestDispatcher("borrow-history.jsp").forward(request, response);
    }
}