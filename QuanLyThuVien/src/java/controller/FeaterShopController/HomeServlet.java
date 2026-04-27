package controller.FeaterShopController;

import dao.BookDAO;
import model.Book;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        BookDAO bDao = new BookDAO();
        
        // 1. Lấy sách mới nhất (Sách giấy)
        List<Book> list = bDao.getTop4Newest(); 
        System.out.println("DEBUG: So luong sach lay duoc la: " + (list != null ? list.size() : "NULL"));
        request.setAttribute("listNew", list); 

        // 2. Lấy truyện mới nhất (Ebook) - Fix lỗi Truyện toàn hiện sách
        List<Book> listTruyen = bDao.getNewEbooks(); 
        request.setAttribute("listTruyen", listTruyen); 

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}