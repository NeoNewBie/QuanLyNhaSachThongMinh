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
        HttpSession phien_lam_viec = request.getSession();
        User nguoi_dung = (User) phien_lam_viec.getAttribute("acc");

        if (nguoi_dung == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String txt_id_sach = request.getParameter("id");
        if (txt_id_sach == null || txt_id_sach.isEmpty()) {
            response.sendRedirect("shop");
            return;
        }

        BookDAO dao_sach = new BookDAO();
        Book sach = dao_sach.getBookById(txt_id_sach);

        if (sach != null && sach.getIsEbook() == 1) {
            request.setAttribute("detail", sach);
            request.getRequestDispatcher("payment-ebook.jsp").forward(request, response);
        } else {
            response.sendRedirect("shop");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession phien_lam_viec = request.getSession();
        User nguoi_dung = (User) phien_lam_viec.getAttribute("acc");

        if (nguoi_dung == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String txt_id_sach = request.getParameter("txt_id_sach");
        String txt_gia_tien = request.getParameter("txt_gia_tien");

        int id_sach = Integer.parseInt(txt_id_sach);
        double gia_tien = Double.parseDouble(txt_gia_tien);

        OrderDAO dao_donhang = new OrderDAO();
        boolean trang_thai = dao_donhang.addEbookOrder(nguoi_dung.getId(), gia_tien, id_sach);

        if (trang_thai) {
            response.sendRedirect("read?id=" + id_sach);
        } else {
            response.sendRedirect("payment-ebook?id=" + id_sach);
        }
    }
}