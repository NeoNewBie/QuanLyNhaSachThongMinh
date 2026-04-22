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
        
        request.setCharacterEncoding("UTF-8"); 
        
        BookDAO dao = new BookDAO();
        CategoryDAO cdao = new CategoryDAO();
        
        // 1. Lấy tất cả tham số từ request
        String cate = request.getParameter("category"); 
        String txt = request.getParameter("txt");       
        String minPrice = request.getParameter("min");  
        String maxPrice = request.getParameter("max");  
        String[] cateIds = request.getParameterValues("cateId"); 
        String sort = request.getParameter("sort");     
        String pStr = request.getParameter("page");     
        
        int pageSize = 9; 
        int pageIndex = 1;
        try {
            if (pStr != null) pageIndex = Integer.parseInt(pStr);
        } catch (Exception e) { pageIndex = 1; }
        
        List<Book> list = null;
        String title = "Tất Cả Sách";
        int totalBooks = 0;
        boolean isDefault = false;

        // 2. LOGIC LỌC NÂNG CAO & TÌM KIẾM
        // Kiểm tra xem khách có đang dùng thanh tìm kiếm, lọc giá, lọc thể loại hay sắp xếp không
        boolean isSearching = (txt != null && !txt.isEmpty()) 
                            || (minPrice != null && !minPrice.isEmpty()) 
                            || (maxPrice != null && !maxPrice.isEmpty()) 
                            || (cateIds != null && cateIds.length > 0) 
                            || sort != null;

        if (isSearching) {
            // 🛑 FIX LỖI: Thêm tham số 'null' vào cuối vì hàm searchAdvanced của anh em mình giờ cần 6 tham số
            // Tham số thứ 6 (isFeatured) để null nghĩa là tìm kiếm chung, không ép buộc chỉ lấy sách nổi bật
            list = dao.searchAdvanced(txt, cateIds, minPrice, maxPrice, sort, null);
            title = "Kết quả tìm kiếm & lọc";
        } 
        // 3. LOGIC THEO MENU
        else if ("new".equals(cate)) {
            list = dao.getNewBooks();
            title = "Sách Mới Cập Nhật";
        } else if ("featured".equals(cate)) {
            // Sếp cũng có thể dùng luôn hàm searchAdvanced ở đây cho đồng bộ:
            // list = dao.searchAdvanced(null, null, null, null, null, true);
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
            // Mặc định: Phân trang trực tiếp từ SQL (Rất nhanh)
            list = dao.getBooksByPage(pageIndex, pageSize);
            totalBooks = dao.countTotalBooks();
            title = "Tất Cả Sách";
            isDefault = true;
        }

        // 4. XỬ LÝ PHÂN TRANG CHO KẾT QUẢ TÌM KIẾM (SubList)
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

        // 5. Gửi dữ liệu ra JSP
        int totalPages = (int) Math.ceil((double)totalBooks / pageSize);
        
        request.setAttribute("listB", list);      
        request.setAttribute("listCC", cdao.getAll());   
        request.setAttribute("pageTitle", title);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("activeCate", cate);
        
        // Giữ lại các giá trị khách đã nhập để hiện lên thanh tìm kiếm/lọc
        request.setAttribute("txtS", txt);
        request.setAttribute("minS", minPrice);
        request.setAttribute("maxS", maxPrice);
        request.setAttribute("sortS", sort);

        request.getRequestDispatcher("shop.jsp").forward(request, response);
    }
}