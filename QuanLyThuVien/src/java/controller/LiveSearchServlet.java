package controller;

import dao.BookDAO;
import model.Book;
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
        
        // Cấu hình trả về kiểu JSON có dấu tiếng Việt
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String query = request.getParameter("q");
        PrintWriter out = response.getWriter();
        
        if (query == null || query.trim().isEmpty()) {
            out.print("[]");
            return;
        }

        BookDAO dao = new BookDAO();
        List<Book> list = dao.getLiveSearchResults(query);
        
        // 🛑 Tạo thủ công chuỗi JSON (Không cần cài thêm thư viện Gson)
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Book b = list.get(i);
            
            // Ép ảnh CoverImage về chuẩn đường dẫn nếu cần
            String imgUrl = b.getCoverImage().replace("\"", "\\\""); 
            String title = b.getTitle().replace("\"", "\\\""); // Tránh lỗi ngoặc kép
            
            json.append("{")
                .append("\"id\": ").append(b.getId()).append(", ")
                .append("\"title\": \"").append(title).append("\", ")
                .append("\"price\": ").append(b.getPrice()).append(", ")
                .append("\"image\": \"").append(imgUrl).append("\"")
                .append("}");
                
            if (i < list.size() - 1) json.append(", ");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
    }
}