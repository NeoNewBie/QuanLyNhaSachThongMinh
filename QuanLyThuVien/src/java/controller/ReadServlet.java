package controller;

import dao.BookDAO;
import dao.ChapterDAO;
import dao.OrderDAO; 
import model.Book;   
import model.Chapter;
import model.User;
import utils.ComicScraper;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ReadServlet", urlPatterns = {"/read"})
public class ReadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id"); // ID Sách
        String numStr = request.getParameter("num"); // Số chương (ví dụ: 1, 2, 3...)
        
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("home");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        // Mặc định nếu không truyền num thì đọc chương 1
        int currentNum = (numStr == null || numStr.isEmpty()) ? 1 : Integer.parseInt(numStr);

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("acc");
        Book b = new BookDAO().getBookById(idStr);
        ChapterDAO cDao = new ChapterDAO();
        
        // 1. Lấy TOÀN BỘ danh sách chương để làm cái Dropdown chọn nhanh
        List<Chapter> allChapters = cDao.getChaptersByBookId(id);
        
        // 2. Tìm đúng cái chương mà sếp đang muốn đọc
        Chapter currentChapter = null;
        int nextNum = -1;
        int prevNum = -1;

        for (int i = 0; i < allChapters.size(); i++) {
            if (allChapters.get(i).getChapterNumber() == currentNum) {
                currentChapter = allChapters.get(i);
                // Xác định số chương trước và sau
                if (i > 0) prevNum = allChapters.get(i-1).getChapterNumber();
                if (i < allChapters.size() - 1) nextNum = allChapters.get(i+1).getChapterNumber();
                break;
            }
        }

        // 3. Kiểm tra khóa và cào ảnh cho DUY NHẤT chương này
        if (currentChapter != null) {
            boolean canRead = false;
            if (currentChapter.isIsFree()) {
                canRead = true;
            } else if (u != null && cDao.isChapterUnlocked(u.getId(), currentChapter.getChapterID())) {
                canRead = true;
                currentChapter.setUnlocked(true);
            }

            if (canRead && currentChapter.getSourceURL() != null) {
                List<String> images = utils.ComicScraper.getImages(currentChapter.getSourceURL());
                currentChapter.setImageList(images);
            }
        }

        // 4. Đẩy dữ liệu sang JSP
        request.setAttribute("detail", b);
        request.setAttribute("chapter", currentChapter); // Chỉ gửi 1 chương hiện tại
        request.setAttribute("allChapters", allChapters); // Gửi cả list để làm dropdown
        request.setAttribute("nextNum", nextNum);
        request.setAttribute("prevNum", prevNum);
        request.setAttribute("currentNum", currentNum);

        request.getRequestDispatcher("read.jsp").forward(request, response);
    }
}