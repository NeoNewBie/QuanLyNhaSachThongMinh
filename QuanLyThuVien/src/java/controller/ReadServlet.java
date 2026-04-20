package controller;

import dao.BookDAO;
import dao.ChapterDAO;
import dao.OrderDAO; 
import model.Book;   
import model.Chapter;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ReadServlet", urlPatterns = {"/read"})
public class ReadServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy ID sách từ URL
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("home");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        
        // 2. Lấy thông tin User đang đăng nhập
        HttpSession session = request.getSession();
        model.User u = (model.User) session.getAttribute("acc");
        
        BookDAO dao = new BookDAO();
        Book b = dao.getBookById(idStr);
        
        // Biến đánh dấu trạng thái khóa nội dung
        boolean isLocked = false;

        // 🛑 LOGIC KIỂM TRA BẢN QUYỀN TRUYỆN:
        // Nếu là Ebook và có giá tiền (không phải truyện miễn phí)
        if (b != null && b.getIsEbook() == 1 && b.getPrice() > 0) {
            if (u == null) {
                // Sếp chưa đăng nhập thì mặc định là khóa, đuổi ra bắt login/mua
                isLocked = true;
            } else {
                // Đã đăng nhập -> Check trong bảng Orders xem đã mua thành công chưa
                OrderDAO orderDao = new OrderDAO();
                // Hàm này sếp check trong DB: Trạng thái đơn hàng phải là 'Success' hoặc 'Approved'
                boolean hasBought = orderDao.checkUserOwnsBook(u.getId(), id);
                
                if (!hasBought) {
                    isLocked = true;
                }
            }
        }

        // 3. Lấy danh sách Chương để hiển thị mục lục
        ChapterDAO cDao = new ChapterDAO();
        List<Chapter> listC = cDao.getChaptersByBookId(id);

        // 4. Đẩy dữ liệu sang trang hiển thị
        request.setAttribute("detail", b);
        request.setAttribute("listC", listC); 
        request.setAttribute("locked", isLocked); // Gửi cờ "khóa" sang JSP
        
        request.getRequestDispatcher("read.jsp").forward(request, response);
    }
}