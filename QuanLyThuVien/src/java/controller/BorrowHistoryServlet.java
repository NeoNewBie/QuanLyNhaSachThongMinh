package controller;

import dao.BookDAO; // Hoặc UserDAO tùy ông để hàm lấy lịch sử ở đâu
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

        // 1. Nếu chưa đăng nhập thì đá về trang Login
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Lấy danh sách sách đang mượn từ Database (Hải kiểm tra lại hàm này trong DAO nhé)
        // BookDAO dao = new BookDAO();
        // List<Borrow> list = dao.getBorrowHistory(acc.getId());
        // request.setAttribute("listB", list);

        // 3. Đẩy giao diện ra (Lệnh này cực quan trọng để hết trắng trang)
        request.getRequestDispatcher("borrow-history.jsp").forward(request, response);
    }
}