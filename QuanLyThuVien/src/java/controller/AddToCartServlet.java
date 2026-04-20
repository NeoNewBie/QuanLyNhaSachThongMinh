package controller;

import dao.BookDAO;
import model.Book;
import model.Cart;
import model.Item;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession phien_lam_viec = request.getSession();
        String txt_id_sach = request.getParameter("id");
        String txt_hanh_dong = request.getParameter("action"); 
        String format = request.getParameter("format"); // Lấy thêm tham số phân biệt bản in/web
        
        try {
            BookDAO dao_sach = new BookDAO();
            Book sach = dao_sach.getBookById(txt_id_sach);
            
            if (sach != null) {
                // 🛑 NẾU LÀ MUA NGAY TỐC HÀNH
                if ("buy".equals(txt_hanh_dong)) {
                    if (sach.getIsEbook() == 1 && !"physical".equals(format)) {
                        response.sendRedirect("payment-ebook?id=" + txt_id_sach);
                    } else {
                        response.sendRedirect("checkout?id=" + txt_id_sach);
                    }
                    return; 
                }

                // 🛑 NẾU CHỈ LÀ THÊM VÀO GIỎ HÀNG
                Cart gio_hang = (Cart) phien_lam_viec.getAttribute("cart");
                if (gio_hang == null) {
                    gio_hang = new Cart();
                }
                Item mat_hang = new Item(sach, 1, sach.getPrice());
                gio_hang.addItem(mat_hang);
                phien_lam_viec.setAttribute("cart", gio_hang);

                // ==========================================
                // 🛑 BỔ SUNG MỚI: XỬ LÝ NẾU LÀ AJAX GỌI ĐẾN
                // ==========================================
                String isAjax = request.getParameter("ajax");
                if ("true".equals(isAjax)) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    
                    int totalItems = 0;
                    for (Item item : gio_hang.getItems()) {
                        totalItems += item.getQuantity();
                    }
                    
                    // Trả về JSON cho Javascript
                    String json = "{\"status\":\"success\", \"cartSize\": " + totalItems + ", \"bookName\": \"" + sach.getTitle().replace("\"", "\\\"") + "\"}";
                    response.getWriter().write(json);
                    return; // Kết thúc sớm, không cho load lại trang
                }

                // Nếu trình duyệt cũ không dùng AJAX thì vẫn redirect bình thường
                phien_lam_viec.setAttribute("cartMsg", "Đã thêm vào giỏ hàng thành công!");
                response.sendRedirect("book-detail?id=" + txt_id_sach);
                
            } else {
                response.sendRedirect("shop");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}