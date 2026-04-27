package controller.FeaterShopController;

import dao.BookDAO;
import dao.CategoryDAO; 
import model.Book;
import model.Category;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopServlet", urlPatterns = {"/shop"})
public class ShopServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); 
        
        BookDAO dao = new BookDAO();
        CategoryDAO cdao = new CategoryDAO();
        
        // 1. Lấy tất cả tham số
        String cate = request.getParameter("category"); 
        String txt = request.getParameter("txt");       
        String minPrice = request.getParameter("min");  
        String maxPrice = request.getParameter("max");  
        String cateId = request.getParameter("cateId"); 
        String sort = request.getParameter("sort");     
        String pStr = request.getParameter("page");     
        
        int pageSize = 9; 
        int pageIndex = 1;
        try { if (pStr != null) pageIndex = Integer.parseInt(pStr); } catch (Exception e) {}
        
        List<Book> list = null;
        String title = "Tất Cả Sách";

        // 2. ĐỊNH TUYẾN DỮ LIỆU
        if ("ebook".equals(cate)) {
            list = dao.getEbooks(); // Kênh riêng cho truyện
            title = "Truyện Online Đặc Sắc";
        } else {
            // 🛑 GOM HẾT VÀO HÀM MASTER: Đảm bảo lọc siêu mượt và không dính Ebook
            list = dao.getFilteredBooks(cate, cateId, txt, minPrice, maxPrice, sort);
            
            // Xử lý tiêu đề đẹp
            if (txt != null && !txt.isEmpty()) title = "Kết quả tìm kiếm";
            else if (cateId != null && !cateId.isEmpty()) {
                // Tùy chọn: Tìm tên Category để in ra làm Title
                Category currentC = cdao.getCategoryById(Integer.parseInt(cateId));
                title = currentC != null ? "Sách " + currentC.getName() : "Danh Mục Sách";
            }
            else if ("new".equals(cate)) title = "Sách Mới Cập Nhật";
            else title = "Tất Cả Sách";
        }

        // 3. XỬ LÝ PHÂN TRANG (SubList)
        int totalBooks = (list != null) ? list.size() : 0;
        if (list != null && !list.isEmpty()) {
            int start = (pageIndex - 1) * pageSize;
            int end = Math.min(start + pageSize, totalBooks);
            if (start < totalBooks) list = list.subList(start, end);
            else list.clear();
        }

        // 4. Gửi dữ liệu ra JSP
        request.setAttribute("listB", list);      
        request.setAttribute("listCC", cdao.getAll());   
        request.setAttribute("pageTitle", title);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("totalPages", (int) Math.ceil((double)totalBooks / pageSize));
        request.setAttribute("activeCate", cate);
        
        // 🛑 GIỮ TRẠNG THÁI ĐỂ NHÉT VÀO FORM ẨN
        request.setAttribute("txtS", txt);
        request.setAttribute("minS", minPrice);
        request.setAttribute("maxS", maxPrice);
        request.setAttribute("cateIdS", cateId);
        request.setAttribute("sortS", sort != null ? sort : "popular");

        request.getRequestDispatcher("shop.jsp").forward(request, response);
    }
}