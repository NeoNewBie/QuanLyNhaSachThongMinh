package controller;

import dao.ChapterDAO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/check-payment-status")
public class CheckPaymentStatusServlet extends HttpServlet {
    
    // Kho lưu trữ các mã đã thanh toán
    public static Set<String> paidTransactions = new HashSet<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        String desc = request.getParameter("orderId"); 
        
        if (desc == null || desc.trim().isEmpty()) {
            response.getWriter().print("pending");
            return;
        }

        desc = desc.toUpperCase();

        try {
            // Trường hợp 1: Kiểm tra Mua Sách (BILL)
            if (desc.contains("BILL")) {
                // Lấy ra số ID của bill, ví dụ BILL_447764 -> lấy 447764
                Matcher m = Pattern.compile("BILL_(\\d+)").matcher(desc);
                if (m.find()) {
                    String id = m.group(1);
                    if (paidTransactions.contains(id)) {
                        paidTransactions.remove(id);
                        response.getWriter().print("success");
                        return;
                    }
                }
            }
            
            // Trường hợp 2: Kiểm tra Mua Truyện (MUACHAP)
            else if (desc.contains("MUACHAP")) {
                Matcher m = Pattern.compile("MUACHAP U(\\d+) C(\\d+)").matcher(desc);
                if (m.find()) {
                    int uId = Integer.parseInt(m.group(1));
                    int cId = Integer.parseInt(m.group(2));
                    String checkCode = "U" + uId + "_C" + cId;

                    // 1. Check Kho tạm trước
                    if (paidTransactions.contains(checkCode)) {
                        paidTransactions.remove(checkCode);
                        response.getWriter().print("success");
                        return;
                    }

                    // 2. Check Database dự phòng
                    ChapterDAO dao = new ChapterDAO();
                    if (dao.isChapterUnlocked(uId, cId)) {
                        response.getWriter().print("success");
                        return;
                    }
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        response.getWriter().print("pending");
    }
}