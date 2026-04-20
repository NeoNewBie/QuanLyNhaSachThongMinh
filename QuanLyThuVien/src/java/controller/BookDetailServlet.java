package controller;

import dao.BookDAO;
import model.Book;
import model.Review; 
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BookDetailServlet", urlPatterns = {"/book-detail"})
public class BookDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String bookIdStr = request.getParameter("id");
            if (bookIdStr == null || bookIdStr.isEmpty()) {
                bookIdStr = request.getParameter("bid");
            }

            if (bookIdStr != null) {
                int id = Integer.parseInt(bookIdStr);
                BookDAO dao = new BookDAO();
                
                Book b = dao.getBookById(bookIdStr);

                List<Review> listR = dao.getReviewsByBookId(id);

                if (b != null) {
                    // 🛑 BỔ SUNG: Lấy thêm danh sách sách cùng thể loại
                    // Lưu ý: Đảm bảo trong model Book của sếp có trường categoryId
                    // Nếu DB của sếp chưa lấy CategoryID trong hàm getBookById, hãy kiểm tra lại nhé!
                    List<Book> listRelated = dao.getRelatedBooks(b.getCategoryId(), b.getId());
                    
                    request.setAttribute("detail", b);
                    request.setAttribute("listR", listR); 
                    request.setAttribute("listRelated", listRelated); // Gửi sang JSP
                    
                    request.getRequestDispatcher("book-detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect("home");
                }
            } else {
                response.sendRedirect("home");
            }
        } catch (Exception e) {
            System.out.println("❌ LỖI TRANG CHI TIẾT SÁCH: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}