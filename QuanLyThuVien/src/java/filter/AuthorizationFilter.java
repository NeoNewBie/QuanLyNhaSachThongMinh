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

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req   = (HttpServletRequest) request;
        HttpServletResponse res  = (HttpServletResponse) response;
        HttpSession session      = req.getSession();

        String uri = req.getRequestURI();
        User acc   = (User) session.getAttribute("acc");

        // ===== VÙNG CHỈ ADMIN =====
        // Nếu vào trang admin mà chưa đăng nhập hoặc không phải admin
        if (uri.contains("/admin/") || uri.contains("/manage-")) {
            if (acc == null || acc.getRoleId() != 1) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        // ===== VÙNG CẦN ĐĂNG NHẬP =====
        // [ĐÃ SỬA LỖI #14] Bổ sung đầy đủ các URL cần bảo vệ
        if (requiresLogin(uri)) {
            if (acc == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Kiểm tra URI có cần đăng nhập không
     */
    private boolean requiresLogin(String uri) {
        return uri.contains("/profile")
            || uri.contains("/borrow")
            || uri.contains("/cart")
            || uri.contains("/checkout")
            || uri.contains("/process-checkout")
            || uri.contains("/orders")
            || uri.contains("/order-detail")
            || uri.contains("/order-success")
            || uri.contains("/track-order")
            || uri.contains("/change-password")
            || uri.contains("/update-profile")
            || uri.contains("/notifications")
            || uri.contains("/wishlist")
            || uri.contains("/read")
            || uri.contains("/payment")
            || uri.contains("/unlock-chapter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}