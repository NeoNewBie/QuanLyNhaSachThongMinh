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

// Chỉ áp dụng filter này cho các đường dẫn bắt đầu bằng /admin/
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        User a = (User) session.getAttribute("acc");
        
        // Kiểm tra xem đã đăng nhập chưa và có phải Admin (roleId = 1) không
        if (a != null && a.getRoleId() == 1) {
            // Đủ điều kiện -> Cho qua
            chain.doFilter(request, response);
        } else {
            // Không đủ quyền -> Đá về trang báo lỗi hoặc trang chủ
            res.sendRedirect(req.getContextPath() + "/home");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}