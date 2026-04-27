package controller.CartPaymentController;

import dao.BookDAO;
import dao.VoucherDAO; // 🛑 Đã import thêm VoucherDAO
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

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("acc") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        String qtyStr = request.getParameter("quantity");
        int quantity = 1;
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try { quantity = Integer.parseInt(qtyStr); } catch (Exception e) { quantity = 1; }
        }

        double totalAmount = 0;
        BookDAO dao = new BookDAO();

        // TRƯỜNG HỢP 1: MUA NGAY TỪ TRANG CHI TIẾT
        if (idStr != null && !idStr.isEmpty()) {
            Book b = dao.getBookById(idStr);
            if (b != null) {
                if (quantity > b.getStock()) quantity = b.getStock(); 
                request.setAttribute("checkoutType", "single");
                request.setAttribute("sach_mua", b);
                request.setAttribute("quantity_mua", quantity);
                totalAmount = b.getPrice() * quantity;
            } else {
                response.sendRedirect("home"); return;
            }
        } 
        // TRƯỜNG HỢP 2: THANH TOÁN TỪ GIỎ HÀNG
        else {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null && !cart.getItems().isEmpty()) {
                request.setAttribute("checkoutType", "cart");
                totalAmount = cart.getTotalMoney(); // Lấy tổng tiền chuẩn từ giỏ

                // FIX LỖI: Tính tổng số lượng thực tế của TẤT CẢ các cuốn sách
                int totalRealQuantity = 0;
                for (Item i : cart.getItems()) {
                    totalRealQuantity += i.getQuantity();
                }

                // Tạo sách giả lập để giao diện hiển thị
                Book placeholder = new Book();
                placeholder.setTitle("Toàn bộ sản phẩm trong giỏ hàng");
                placeholder.setAuthor("Nhiều tác giả");
                placeholder.setCoverImage("https://cdn-icons-png.flaticon.com/512/1170/1170678.png");
                placeholder.setIsEbook(0); 
                placeholder.setPrice(totalAmount); // FIX LỖI: Gán tổng tiền vào giá sách để thẻ UI hiện đúng

                request.setAttribute("sach_mua", placeholder);
                request.setAttribute("quantity_mua", totalRealQuantity); // Đưa số lượng thật ra UI
            } else {
                response.sendRedirect("shop"); return;
            }
        }

        // 🛑 ĐOẠN SẾP THÊM VÀO NẰM Ở ĐÂY: Cực chuẩn!
        VoucherDAO vDao = new VoucherDAO();
        request.setAttribute("voucherList", vDao.getValidVouchers());
        request.setAttribute("totalAmount", totalAmount);
        
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }
}