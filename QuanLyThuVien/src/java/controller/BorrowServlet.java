package controller;

import dao.BookDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "BorrowServlet", urlPatterns = {"/borrow"})
public class BorrowServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("acc");

        if (u == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int bookID = Integer.parseInt(request.getParameter("bookID"));
            String returnDate = request.getParameter("returnDate");
            int userID = u.getId(); // Đảm bảo trong User.java có hàm getId()

            BookDAO dao = new BookDAO();
            boolean success = dao.insertBorrow(userID, bookID, returnDate);

            if (success) {
                session.setAttribute("borrowMsg", "Đăng ký mượn thành công! Chờ thủ thư duyệt.");
                response.sendRedirect("home");
            } else {
                response.sendRedirect("book-detail?id=" + bookID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}