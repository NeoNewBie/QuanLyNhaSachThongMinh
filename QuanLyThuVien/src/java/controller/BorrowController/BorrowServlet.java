package controller.BorrowController;

import dao.BookDAO;
import model.Book;
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

        // [ĐÃ SỬA LỖI #9] Redirect đến Servlet /login thay vì login.jsp
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int bookID = Integer.parseInt(request.getParameter("bookID"));
            String returnDate = request.getParameter("returnDate");
            int userID = u.getId();

            BookDAO dao = new BookDAO();

            // [GỢI Ý #1] Kiểm tra stock trước khi tạo phiếu mượn
            Book book = dao.getBookById(String.valueOf(bookID));
            if (book == null) {
                session.setAttribute("borrowMsg", "Không tìm thấy sách!");
                response.sendRedirect("home");
                return;
            }
            if (book.getStock() <= 0) {
                session.setAttribute("borrowMsg", "Rất tiếc, sách này hiện đã hết tại quầy!");
                response.sendRedirect("book-detail?id=" + bookID);
                return;
            }

            // Kiểm tra ngày trả hợp lệ
            if (returnDate == null || returnDate.trim().isEmpty()) {
                session.setAttribute("borrowMsg", "Vui lòng chọn ngày trả sách!");
                response.sendRedirect("book-detail?id=" + bookID);
                return;
            }

            boolean success = dao.insertBorrow(userID, bookID, returnDate);

            if (success) {
                session.setAttribute("borrowMsg", "Đăng ký mượn thành công! Chờ thủ thư duyệt.");
                response.sendRedirect("home");
            } else {
                session.setAttribute("borrowMsg", "Đăng ký mượn thất bại, vui lòng thử lại!");
                response.sendRedirect("book-detail?id=" + bookID);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("borrowMsg", "Dữ liệu không hợp lệ!");
            response.sendRedirect("home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}