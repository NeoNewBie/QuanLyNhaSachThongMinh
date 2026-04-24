package controller;

import dao.BorrowDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ExtendBorrowServlet", urlPatterns = {"/extend-borrow"})
public class ExtendBorrowServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr != null) {
            int borrowId = Integer.parseInt(idStr);
            BorrowDAO dao = new BorrowDAO();
            
            // Thực hiện cộng thêm 7 ngày vào Hạn trả
            boolean isSuccess = dao.extendBorrow(borrowId, 7);
            
            if (isSuccess) {
                request.getSession().setAttribute("borrowMsg", "Gia hạn thành công! Sách đã được cộng thêm 7 ngày. (Phí gia hạn sẽ được tính lúc sếp trả sách nhé).");
            } else {
                request.getSession().setAttribute("borrowMsg", "LỖI: Không thể gia hạn lúc này!");
            }
        }
        
        // Làm xong thì quay về trang cũ
        response.sendRedirect("borrow-history");
    }
}