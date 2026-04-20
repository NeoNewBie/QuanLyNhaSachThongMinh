package controller;

import dao.BookDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddReviewServlet", urlPatterns = {"/add-review"})
public class AddReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession phien_lam_viec = request.getSession();
        User nguoi_dung = (User) phien_lam_viec.getAttribute("acc");

        if (nguoi_dung == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String txt_id_sach = request.getParameter("txt_id_sach");
            String txt_so_sao = request.getParameter("txt_so_sao");
            String txt_binh_luan = request.getParameter("txt_binh_luan");

            if (txt_id_sach == null || txt_id_sach.trim().isEmpty()) {
                response.sendRedirect("shop");
                return;
            }

            int id_sach = Integer.parseInt(txt_id_sach);
            int so_sao = (txt_so_sao != null && !txt_so_sao.isEmpty()) ? Integer.parseInt(txt_so_sao) : 5;

            BookDAO dao_sach = new BookDAO();
            dao_sach.insertReview(nguoi_dung.getId(), id_sach, so_sao, txt_binh_luan);

            phien_lam_viec.setAttribute("msg", "Đã gửi bình luận thành công!");

            response.sendRedirect("book-detail?bid=" + id_sach);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}