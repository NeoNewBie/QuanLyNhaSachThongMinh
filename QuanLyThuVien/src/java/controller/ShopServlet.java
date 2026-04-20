package controller;

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
        
        request.setCharacterEncoding("UTF-8"); // 🛑 Rất quan trọng để tìm tiếng Việt
        
        BookDAO dao = new BookDAO();
        CategoryDAO cdao = new CategoryDAO();
        
        // 1. Lấy TẤT CẢ tham số từ giao diện gửi lên
        String cate = request.getParameter("category"); 
        String txt = request.getParameter("txt");       // Tên sách (từ thanh tìm kiếm)
        String minPrice = request.getParameter("min");  // Giá từ
        String maxPrice = request.getParameter("max");  // Giá đến
        String[] cateIds = request.getParameterValues("cateId"); // Checkbox thể loại
        String sort = request.getParameter("sort");     // 🛑 Tham số Sắp xếp (MỚI)
        String pStr = request.getParameter("page");     
        
        int pageSize = 9; 
        int pageIndex = 1;
        if (pStr != null) pageIndex = Integer.parseInt(pStr);
        
        List<Book> list = null;
        String title = "Tất Cả Sách";
        int totalBooks = 0;
        boolean isDefault = false;

        // 2. LOGIC LỌC NÂNG CAO & TÌM KIẾM
        // Nếu có nhập tên, hoặc nhập giá, hoặc có yêu cầu sắp xếp -> Gọi hàm searchAdvanced
        if ((txt != null && !txt.isEmpty()) || (minPrice != null && !minPrice.isEmpty()) || (maxPrice != null && !maxPrice.isEmpty()) || cateIds != null || sort != null) {
            
            // 🛑 Đã cập nhật hàm searchAdvanced thêm tham số 'sort'
            list = dao.searchAdvanced(txt, cateIds, minPrice, maxPrice, sort);
            title = "Kết quả tìm kiếm & lọc";
            
        } 
        // 3. LOGIC THEO MENU (Các mục định sẵn)
        else if ("new".equals(cate)) {
            list = dao.getNewBooks();
            title = "Sách Mới Cập Nhật";
        } else if ("featured".equals(cate)) {
            list = dao.getFeaturedBooks();
            title = "Sách Nổi Bật";
        } else if ("hot".equals(cate)) {
            list = dao.getBestSellerBooks();
            title = "Sách Bán Chạy Nhất";
        } else if ("most-viewed".equals(cate)) {
            list = dao.getMostViewedBooks();
            title = "Sách Được Xem Nhiều";
        } else if ("ebook".equals(cate)) {
            list = dao.getEbooks();
            title = "Truyện Online Đặc Sắc";
        } else {
            // Mặc định: Hiện tất cả và phân trang từ SQL
            list = dao.getBooksByPage(pageIndex, pageSize);
            totalBooks = dao.countTotalBooks();
            title = "Tất Cả Sách";
            isDefault = true;
        }

        // 4. XỬ LÝ PHÂN TRANG (Cho kết quả lọc)
        if (!isDefault && list != null) {
            totalBooks = list.size();
            int start = (pageIndex - 1) * pageSize;
            int end = Math.min(start + pageSize, totalBooks);
            
            if (start < totalBooks) {
                list = list.subList(start, end);
            } else {
                list.clear();
            }
        }

        // 5. Gửi dữ liệu ra giao diện
        List<Category> listCC = cdao.getAll();
        int totalPages = (int) Math.ceil((double)totalBooks / pageSize);
        
        request.setAttribute("listB", list);      
        request.setAttribute("listCC", listCC);   
        request.setAttribute("pageTitle", title);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("activeCate", cate);
        
        // 🛑 Lưu lại các giá trị khách vừa nhập để không bị mất khi load lại trang
        request.setAttribute("txtS", txt);
        request.setAttribute("minS", minPrice);
        request.setAttribute("maxS", maxPrice);
        request.setAttribute("sortS", sort);

        request.getRequestDispatcher("shop.jsp").forward(request, response);
    }
}