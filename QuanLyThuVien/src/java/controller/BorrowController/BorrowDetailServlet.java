package controller.BorrowController;

import dao.BorrowDAO;
import model.Borrow;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BorrowDetailServlet", urlPatterns = {"/borrow-detail"})
public class BorrowDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            Borrow b = new BorrowDAO().getBorrowById(Integer.parseInt(idStr));
            request.setAttribute("borrowDetail", b);
            request.getRequestDispatcher("borrow-detail.jsp").forward(request, response);
        } else {
            response.sendRedirect("borrow-history");
        }
    }
}