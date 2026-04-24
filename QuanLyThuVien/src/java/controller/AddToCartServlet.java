package controller;

import dao.BookDAO;
import model.Book;
import model.Cart;
import model.Item;
import model.User;
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
        
        HttpSession session = request.getSession();
        String txt_id_sach = request.getParameter("id");
        String txt_hanh_dong = request.getParameter("action"); 
        String format = request.getParameter("format"); 
        String isAjax = request.getParameter("ajax");
        
        // Lấy thêm số lượng (phục vụ cho nút [-][1][+] ở trang chi tiết)
        int quantity = 1;
        String qtyStr = request.getParameter("quantity");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (Exception e) { quantity = 1; }
        }
        
        try {
            BookDAO dao_sach = new BookDAO();
            Book sach = dao_sach.getBookById(txt_id_sach);
            
            if (sach != null) {
                // 🛑 TRƯỜNG HỢP 1: MUA NGAY TỐC HÀNH (Redirect thẳng sang thanh toán)
                if ("buy".equals(txt_hanh_dong)) {
                    if (sach.getIsEbook() == 1 && !"physical".equals(format)) {
                        response.sendRedirect("payment-ebook?id=" + txt_id_sach);
                    } else {
                        // Mua sách giấy ngay, truyền luôn số lượng
                        response.sendRedirect("checkout?id=" + txt_id_sach + "&quantity=" + quantity + "&checkoutType=single");
                    }
                    return; 
                }

                // 🛑 TRƯỜNG HỢP 2 & 3: THÊM VÀO GIỎ HÀNG (AJAX hoặc Redirect)
                User acc = (User) session.getAttribute("acc");
                Cart gio_hang;
                
                if (acc != null) {
                    // Đã đăng nhập: Lưu thẳng xuống Database và nạp lại lên Session
                    dao.CartDAO cDao = new dao.CartDAO();
                    cDao.addToCart(acc.getId(), sach.getId(), quantity);
                    gio_hang = cDao.getCartByUserId(acc.getId());
                    session.setAttribute("cart", gio_hang);
                } else {
                    // Khách vãng lai: Lưu tạm trên Session
                    gio_hang = (Cart) session.getAttribute("cart");
                    if (gio_hang == null) {
                        gio_hang = new Cart();
                    }
                    Item mat_hang = new Item(sach, quantity, sach.getPrice());
                    gio_hang.addItem(mat_hang);
                    session.setAttribute("cart", gio_hang);
                }

                // Nếu là AJAX gọi đến (Dùng cho Home và Detail không load lại trang)
                if ("true".equals(isAjax)) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    
                    int totalItems = 0;
                    for (Item item : gio_hang.getItems()) {
                        totalItems += item.getQuantity();
                    }
                    
                    // Trả về JSON: trạng thái, tổng số lượng (để nảy badge), tên sách (để hiện Toast)
                    String json = "{\"status\":\"success\", \"cartSize\": " + totalItems + ", \"bookName\": \"" + sach.getTitle().replace("\"", "\\\"") + "\"}";
                    response.getWriter().write(json);
                    return; 
                }

                // Nếu là trình duyệt cũ (không dùng AJAX), thông báo và quay về trang chi tiết
                session.setAttribute("cartMsg", "Đã thêm vào giỏ hàng thành công!");
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