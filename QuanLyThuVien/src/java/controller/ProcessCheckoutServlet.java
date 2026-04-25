package controller;

import dao.BookDAO;
import dao.OrderDAO;
import dao.UserDAO;       // 🛑 Import thêm UserDAO để xử lý địa chỉ
import dao.VoucherDAO;    // 🛑 Import thêm VoucherDAO để xử lý mã giảm giá
import model.Book;
import model.Cart;
import model.Item;
import model.User;
import model.Voucher;     // 🛑 Import thêm model Voucher
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ProcessCheckoutServlet", urlPatterns = {"/process-checkout"})
public class ProcessCheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Bắt buộc phải có để đọc được Tiếng Việt (Tên, Địa chỉ) không bị lỗi font
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String checkoutType = request.getParameter("checkoutType");
            String phuongThuc = request.getParameter("txt_phuong_thuc"); // Bắt lấy "COD" hoặc "TRANSFER"
            
            // 🛑 LẤY DỮ LIỆU TỪ GIAO DIỆN MỚI
            String voucherCode = request.getParameter("voucherCode"); // Mã giảm giá
            String shipName = request.getParameter("shipName");       // Tên người nhận
            String shipPhone = request.getParameter("shipPhone");     // SĐT người nhận
            String shipAddress = request.getParameter("shipAddress"); // Địa chỉ nhận

            // 🛑 1. CẬP NHẬT ĐỊA CHỈ KHÁCH HÀNG (Lưu vào DB & Cập nhật Session luôn cho nóng)
            if (shipName != null && shipPhone != null && shipAddress != null) {
                new UserDAO().updateShippingInfo(acc.getId(), shipName, shipPhone, shipAddress);
                acc.setFullName(shipName);
                acc.setPhone(shipPhone);
                acc.setAddress(shipAddress);
                session.setAttribute("acc", acc); 
            }

            OrderDAO oDao = new OrderDAO();
            boolean success = false;

            // 2. LƯU ĐƠN VÀO DATABASE (Mặc định Status = 0, Giá gốc)
            if ("single".equals(checkoutType)) {
                String idSach = request.getParameter("txt_id_sach");
                String qtyStr = request.getParameter("quantity_mua"); 
                int qty = (qtyStr != null) ? Integer.parseInt(qtyStr) : 1;

                Book sach = new BookDAO().getBookById(idSach);
                Cart fakeCart = new Cart();
                fakeCart.addItem(new Item(sach, qty, sach.getPrice()));
                
                success = oDao.addOrder(acc, fakeCart);
            } else {
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    success = oDao.addOrder(acc, cart);
                    if (success) {
                        new dao.CartDAO().clearCart(acc.getId());
                        session.removeAttribute("cart"); 
                    }
                }
            }

            // 3. XỬ LÝ HẬU KỲ (TRỪ TIỀN VOUCHER VÀ ĐỔI TRẠNG THÁI QR)
            if (success) {
                // 🛑 NẾU CÓ ÁP MÃ VOUCHER: GỌI NINJA TRỪ TIỀN & ĐỐT VOUCHER
                if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                    Voucher v = new VoucherDAO().getVoucherByCode(voucherCode);
                    if (v != null) {
                        oDao.applyDiscountToLatestOrder(acc.getId(), v.getDiscountAmount());
                        new VoucherDAO().markVoucherAsUsed(acc.getId(), voucherCode);
                    }
                }

                // 🛑 NẾU LÀ CHUYỂN KHOẢN (QR): ĐỔI THÀNH ĐÃ THANH TOÁN (1)
                if ("TRANSFER".equals(phuongThuc)) {
                    oDao.updateLatestOrderToPaid(acc.getId());
                }
                
                response.sendRedirect("order-success.jsp");
            } else {
                session.setAttribute("error", "Lưu đơn hàng thất bại!");
                response.sendRedirect("home");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}