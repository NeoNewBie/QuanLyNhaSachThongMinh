package controller.FeaterShopController;

import dao.BookDAO;
import dao.CategoryDAO;
import model.Book;
import model.Category;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LiveSearchServlet", urlPatterns = {"/live-search"})
public class LiveSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String query = request.getParameter("q");
        if (query == null || query.trim().isEmpty()) {
            out.print("{\"categories\":[], \"books\":[]}");
            return;
        }

        // 1. TÌM KIẾM DANH MỤC (Logic Shopee)
        List<Category> allCates = new CategoryDAO().getAll();
        StringBuilder catesJson = new StringBuilder("[");
        boolean hasCate = false;
        for (Category c : allCates) {
            if (c.getName().toLowerCase().contains(query.toLowerCase())) {
                if (hasCate) catesJson.append(",");
                catesJson.append("{\"id\":").append(c.getId()).append(",\"name\":\"").append(c.getName().replace("\"", "\\\"")).append("\"}");
                hasCate = true;
            }
        }
        catesJson.append("]");

        // 2. TÌM KIẾM SÁCH
        BookDAO dao = new BookDAO();
        List<Book> list = dao.getLiveSearchResults(query);
        StringBuilder booksJson = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Book b = list.get(i);
            String imgUrl = b.getCoverImage().replace("\"", "\\\""); 
            String title = b.getTitle().replace("\"", "\\\"");
            
            booksJson.append("{")
                .append("\"id\": ").append(b.getId()).append(", ")
                .append("\"title\": \"").append(title).append("\", ")
                .append("\"price\": ").append(b.getPrice()).append(", ")
                .append("\"image\": \"").append(imgUrl).append("\"")
                .append("}");
                
            if (i < list.size() - 1) booksJson.append(", ");
        }
        booksJson.append("]");
        
        // 3. GỘP CẢ 2 BỘ JSON
        String finalJson = "{\"categories\":" + catesJson.toString() + ", \"books\":" + booksJson.toString() + "}";
        out.print(finalJson);
        out.flush();
    }
}