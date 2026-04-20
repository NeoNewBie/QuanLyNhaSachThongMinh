/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.WishlistDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author HOANG HAI
 */
@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist"})
public class WishlistServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    model.User a = (model.User) session.getAttribute("acc");
    
    if (a == null) {
        response.sendRedirect("login");
        return;
    }

    // DÒNG NÀY ĐỂ KIỂM TRA:
    System.out.println("DEBUG: Hải đang xem Wishlist của UserID = " + a.getId());

    WishlistDAO dao = new WishlistDAO();
    request.setAttribute("listW", dao.getWishlistByUser(a.getId()));
    request.getRequestDispatcher("wishlist.jsp").forward(request, response);
}
}
