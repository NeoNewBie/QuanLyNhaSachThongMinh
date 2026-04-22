package controller;

import dao.ChapterDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UnlockChapterServlet", urlPatterns = {"/unlock-chapter"})
public class UnlockChapterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("======================================");
        System.out.println("👉 [DEBUG] ĐÃ VÀO UNLOCK SERVLET");

        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");
        
        if (acc == null) {
            System.out.println("❌ [DEBUG] Lỗi: Chưa đăng nhập!");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String chapterIdStr = request.getParameter("chapterId");
        String bookIdStr = request.getParameter("bookId"); 

        System.out.println("👉 [DEBUG] UserID: " + acc.getId());
        System.out.println("👉 [DEBUG] ChapterID nhận được: " + chapterIdStr);
        System.out.println("👉 [DEBUG] BookID nhận được: " + bookIdStr);

        if (chapterIdStr != null && !chapterIdStr.isEmpty()) {
            try {
                int chapterId = Integer.parseInt(chapterIdStr);
                ChapterDAO dao = new ChapterDAO();
                
                System.out.println("👉 [DEBUG] Bắt đầu gọi hàm Insert vào DB...");
                boolean success = dao.unlockChapter(acc.getId(), chapterId);

                if (success) {
                    System.out.println("✅ [THÀNH CÔNG] Đã lưu lịch sử mở khóa!");
                } else {
                    System.out.println("❌ [THẤT BẠI] Hàm unlockChapter trả về false. Kiểm tra lại SQL!");
                }
            } catch (Exception e) {
                System.out.println("❌ [EXCEPTION] Lỗi khi xử lý ID: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ [LỖI FORM] Không nhận được ChapterID từ giao diện gửi lên!");
        }

        System.out.println("======================================");
        response.sendRedirect(request.getContextPath() + "/read?id=" + bookIdStr + "&msg=success");
    }
}