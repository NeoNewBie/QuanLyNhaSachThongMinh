package controller;

import dao.BookDAO;
import dao.OrderDAO;
import model.Book;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PaymentEbookServlet", urlPatterns = {"/payment-ebook"})
public class PaymentEbookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Kiểm tra phiên làm việc (Session) của sếp Hải
        HttpSession phien_lam_viec = request.getSession();
        User nguoi_dung = (User) phien_lam_viec.getAttribute("acc");

        if (nguoi_dung == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Lấy ID sách/truyện cần mua từ tham số "id" trên URL
        String txt_id_sach = request.getParameter("id");
        if (txt_id_sach == null || txt_id_sach.isEmpty()) {
            response.sendRedirect("shop");
            return;
        }

        // 3. Tìm thông tin sách trong Database
        BookDAO dao_sach = new BookDAO();
        Book sach = dao_sach.getBookById(txt_id_sach);

        if (sach != null && sach.getIsEbook() == 1) {
            // --- PHẦN NỐI DÂY ĐIỆN CHO THANH TOÁN TỰ ĐỘNG ---
            
            // Đẩy thông tin sách để hiển thị ảnh bìa, tên sách
            request.setAttribute("detail", sach);
            
            // Đẩy giá tiền để VietQR vẽ vào mã (Khách quét là hiện số tiền luôn)
            request.setAttribute("totalAmount", sach.getPrice());
            
            // Tạo mã nội dung chuyển khoản "huyền thoại" MUACHAP U{id} C{id}
            // Cái này cực quan trọng để Webhook nó biết ai trả tiền cho cuốn nào
            String ma_don_hang = "MUACHAP U" + nguoi_dung.getId() + " C" + txt_id_sach;
            request.setAttribute("orderId", ma_don_hang);
            
            // 4. Dẫn sếp sang trang payment-qr.jsp (Trang có cái Script tự động check tiền)
            request.getRequestDispatcher("payment-qr.jsp").forward(request, response);
            // ----------------------------------------------
        } else {
            response.sendRedirect("shop");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Giữ nguyên logic xử lý nút "Xác nhận thủ công" của sếp
        HttpSession phien_lam_viec = request.getSession();
        User nguoi_dung = (User) phien_lam_viec.getAttribute("acc");

        if (nguoi_dung == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String txt_id_sach = request.getParameter("txt_id_sach");
        String txt_gia_tien = request.getParameter("txt_gia_tien");

        try {
            int id_sach = Integer.parseInt(txt_id_sach);
            double gia_tien = Double.parseDouble(txt_gia_tien);

            OrderDAO dao_donhang = new OrderDAO();
            
            // Bước này sếp lưu đơn hàng vào DB để chờ Webhook duyệt hoặc sếp duyệt tay
            boolean trang_thai = dao_donhang.addEbookOrder(nguoi_dung.getId(), gia_tien, id_sach);

            if (trang_thai) {
                // Nếu thành công thì đẩy về trang đọc (Hoặc trang thành công)
                response.sendRedirect("read?id=" + id_sach);
            } else {
                // Thất bại thì quay lại trang thanh toán
                response.sendRedirect("payment-ebook?id=" + id_sach);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("shop");
        }
    }
}