package filter;

import model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// 🛑 FILTER NÀY SẼ QUÉT QUA TOÀN BỘ CÁC ĐƯỜNG DẪN TRONG WEB
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        // 1. Lấy URL mà người dùng đang muốn truy cập
        String uri = req.getRequestURI();
        
        // 2. Lấy thông tin sếp Hải đã đăng nhập trong Session
        User acc = (User) session.getAttribute("acc");

        // 3. DANH SÁCH "VÙNG CẤM"
        
        // Nếu đòi vào trang Admin mà: Chưa đăng nhập HOẶC Quyền không phải Admin (Role != 1)
        if (uri.contains("/admin/") || uri.contains("/manage-")) {
            if (acc == null || acc.getRoleId() != 1) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }
        
        // Nếu đòi vào trang Cá nhân, Lịch sử mượn, Giỏ hàng mà chưa đăng nhập
        if (uri.contains("/profile") || uri.contains("/borrow-") || uri.contains("/cart")) {
            if (acc == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        // Nếu mọi thứ ok thì cho đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}