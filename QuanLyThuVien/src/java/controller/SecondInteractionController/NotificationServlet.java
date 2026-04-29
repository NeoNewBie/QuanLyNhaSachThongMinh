package controller.SecondInteractionController;

import dao.NotificationDAO;
import model.Notification;
import model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/notifications"})
public class NotificationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User acc = (User) request.getSession().getAttribute("acc");
        if (acc == null) {
            response.sendRedirect("login");
            return;
        }
        
        List<Notification> list = new NotificationDAO().getAllByUser(acc.getId());
        request.setAttribute("allNotifs", list);
        request.getRequestDispatcher("notifications.jsp").forward(request, response);
    }
}