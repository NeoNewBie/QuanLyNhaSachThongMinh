<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="currentURI" value="${pageContext.request.requestURI}" />

<%-- ĐOẠN JAVA CHẠY NGẦM ĐỂ LẤY THÔNG BÁO TỪ DB --%>
<%@ page import="dao.NotificationDAO, model.Notification, java.util.List" %>
<%
    model.User currentUser = (model.User) session.getAttribute("acc");
    if(currentUser != null) {
        NotificationDAO notifDAO = new NotificationDAO();
        request.setAttribute("notifList", notifDAO.getTop5ByUser(currentUser.getId()));
        request.setAttribute("unreadCount", notifDAO.countUnread(currentUser.getId()));
    }
%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<style>
    :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
    body { font-family: 'Inter', sans-serif; }
    a { text-decoration: none !important; }

    /* TOP BAR */
    .top-bar { background: #fff; padding: 15px 0; border-bottom: 1px solid #eee; }
    .logo-text { color: var(--primary-color); font-weight: 900; font-size: 1.8rem; }
    .search-input-top { width: 100%; border-radius: 50px; border: 1px solid #ddd; padding: 10px 20px 10px 45px; background: var(--bg-light); font-weight: 500; outline: none; }
    .search-input-top:focus { border-color: var(--primary-color); background: #fff; }

    .action-link { color: #555; font-size: 0.85rem; font-weight: 700; display: flex; align-items: center; gap: 6px; cursor: pointer; transition: 0.3s; }
    .action-link:hover { color: var(--accent-color); }
    .action-link i { font-size: 1.3rem; color: var(--primary-color); transition: 0.3s; }
    .action-link:hover i { color: var(--accent-color); }

    /* DROPDOWN MENU */
    .dropdown-menu { border: none; border-radius: 12px; box-shadow: 0 8px 25px rgba(0,0,0,0.1); padding: 8px; margin-top: 15px !important; min-width: 190px; }
    .dropdown-header { font-size: 0.65rem !important; text-transform: uppercase; font-weight: 800; color: #b0b8c1; letter-spacing: 1.5px; padding: 5px 12px !important; margin-top: 5px; }
    .dropdown-item { border-radius: 8px; padding: 8px 12px; font-size: 0.85rem; transition: all 0.2s ease; display: flex; align-items: flex-start; }
    .dropdown-item i { font-size: 1.05rem; margin-right: 10px; transition: 0.2s; }
    .dropdown-item:hover, .dropdown-item:focus { background-color: #FFF5F3 !important; transform: translateX(4px); }
    .dropdown-divider { margin: 6px 0; border-color: #f0f0f0; }

    /* MENU CHÍNH XANH NAVY */
    .main-nav { background-color: var(--primary-color); height: 50px !important; display: flex; align-items: center; }
    .nav-list { display: flex; justify-content: center; align-items: center; margin: 0; padding: 0; list-style: none; width: 100%; height: 50px; }
    .nav-item { position: relative; }
    .nav-item:not(:last-child)::after { content: "|"; color: rgba(255,255,255,0.2); position: absolute; right: 0; top: 50%; transform: translateY(-50%); }
    .nav-link { color: rgba(255,255,255,0.85); font-size: 0.85rem; font-weight: 700; padding: 0 25px; line-height: 50px !important; display: block; transition: 0.3s; text-transform: uppercase; }
    .nav-link:hover, .nav-link.active { color: var(--accent-color) !important; background: rgba(255,255,255,0.1); }

    /* CHATBOT */
    #ai-chat-box { display: none; position: fixed; bottom: 100px; right: 30px; width: 350px; height: 500px; background: white; border-radius: 20px; box-shadow: 0 15px 50px rgba(0,0,0,0.2); z-index: 9999; flex-direction: column; overflow: hidden; border: 1px solid #eee; }
    .chat-header { background: var(--primary-color); color: white; padding: 15px; font-weight: 800; display: flex; justify-content: space-between; align-items: center; }
    .chat-input-group { padding: 15px; background: #fff; border-top: 1px solid #eee; display: flex; gap: 10px; }
    .chat-input-group input { flex: 1; border-radius: 10px; border: 1px solid #ddd; padding: 8px 15px; outline: none; font-size: 0.85rem; }
    .chat-input-group button { background: var(--primary-color); color: white; border: none; border-radius: 10px; padding: 0 15px; transition: 0.3s; }
    .chat-input-group button:hover { background: var(--accent-color); }
    .chatbot-btn { position: fixed; bottom: 30px; right: 30px; width: 65px; height: 65px; border-radius: 50%; background: var(--accent-color); color: white; display: flex; align-items: center; justify-content: center; font-size: 28px; cursor: pointer; box-shadow: 0 10px 25px rgba(237, 85, 59, 0.4); z-index: 9998; transition: 0.3s; }
    .chatbot-btn:hover { transform: scale(1.1); }
</style>

<div class="top-bar d-none d-lg-block">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-3">
                <a href="home" class="d-flex align-items-center"><i class="bi bi-book-half fs-2 me-2" style="color: var(--accent-color);"></i><span class="logo-text">SMART LIB</span></a>
            </div>
            <div class="col-4 position-relative">
                <i class="bi bi-search position-absolute" style="left: 18px; top: 50%; transform: translateY(-50%); color: #999;"></i>
                <form action="shop" method="GET"><input type="text" name="txt" class="search-input-top" placeholder="Tìm kiếm sách..."></form>
            </div>
            <div class="col-5 d-flex justify-content-end gap-4 align-items-center">
                <c:choose>
                    <c:when test="${sessionScope.acc == null}">
                        <div class="d-flex gap-3">
                            <a href="login.jsp" class="action-link"><i class="bi bi-person-lock"></i> ĐĂNG NHẬP</a>
                            <span class="text-muted">|</span>
                            <a href="register.jsp" class="action-link"><i class="bi bi-person-plus"></i> ĐĂNG KÝ</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        
                        <div class="dropdown me-1">
                            <a class="action-link position-relative" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-bell-fill fs-5 ${unreadCount > 0 ? 'text-warning' : 'text-secondary'}"></i> 
                                
                                <c:if test="${unreadCount > 0}">
                                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.55rem; padding: 0.3em 0.4em;">
                                        ${unreadCount}
                                    </span>
                                </c:if>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0" style="width: 340px; margin-top: 18px !important; max-height: 400px; overflow-y: auto;">
                                <li><h6 class="dropdown-header text-primary fw-bold">Thông báo của sếp</h6></li>
                                <li><hr class="dropdown-divider"></li>
                                
                                <c:choose>
                                    <c:when test="${empty notifList}">
                                        <li><span class="dropdown-item text-muted text-center py-3">Hiện chưa có thông báo nào.</span></li>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${notifList}" var="n">
                                            <li>
                                                <c:set var="targetLink" value="home" />
                                                
                                                <c:choose>
                                                    <c:when test="${fn:contains(n.message, '#ORD-')}">
                                                        <c:set var="idStr" value="${fn:substringBefore(fn:substringAfter(n.message, '#ORD-'), ' ')}" />
                                                        <c:set var="targetLink" value="order-detail?id=${idStr}" />
                                                    </c:when>
                                                    <c:when test="${fn:contains(n.message, '#LIB-')}">
                                                        <c:set var="idStr" value="${fn:substringBefore(fn:substringAfter(n.message, '#LIB-'), ' ')}" />
                                                        <c:set var="targetLink" value="borrow-detail?id=${idStr}" />
                                                    </c:when>
                                                </c:choose>

                                                <%-- 🛑 ĐÃ FIX: Chèn link read-notif để cập nhật DB trước khi bay sang trang chi tiết --%>
                                                <a class="dropdown-item py-2 ${!n.read ? 'bg-light border-start border-4 border-warning' : 'bg-white text-muted'}" style="white-space: normal;" href="read-notif?nid=${n.notifId}&url=${targetLink}">
                                                    <div class="d-flex align-items-start gap-2 w-100">
                                                        <i class="bi bi-info-circle-fill fs-5 mt-1 ${!n.read ? 'text-warning' : 'text-secondary opacity-50'}"></i>
                                                        <div class="flex-grow-1">
                                                            <p class="mb-1 text-dark ${!n.read ? 'fw-bold' : 'fw-normal'}" style="font-size: 0.85rem; line-height: 1.4;">${n.message}</p>
                                                            <small class="d-block ${!n.read ? 'text-primary fw-semibold' : 'text-muted opacity-75'}" style="font-size: 0.7rem;">
                                                                <fmt:formatDate value="${n.createdDate}" pattern="HH:mm - dd/MM/yyyy"/>
                                                            </small>
                                                        </div>
                                                        <c:if test="${!n.read}">
                                                            <span class="p-1 bg-primary border border-light rounded-circle mt-2"></span>
                                                        </c:if>
                                                    </div>
                                                </a>
                                            </li>
                                        </c:forEach>
                                        
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item text-center text-primary fw-bold py-2 d-block w-100" href="notifications">Xem tất cả thông báo</a></li>

                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>
                        <div class="dropdown">
                            <a class="action-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle"></i> HI, ${sessionScope.acc.username}
                            </a>
                            <ul class="dropdown-menu shadow-lg border-0">
                                <c:if test="${sessionScope.acc.roleId == 1}">
                                    <li><a class="dropdown-item fw-bold text-primary" href="admin/dashboard"><i class="bi bi-speedometer2"></i> Quản trị hệ thống</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                </c:if>
                                
                                <li><span class="dropdown-header">Cá nhân</span></li>
                                <li><a class="dropdown-item" href="profile"><i class="bi bi-person-vcard me-2"></i> Hồ sơ của tôi</a></li>
                                <li><a class="dropdown-item" href="change-password.jsp"><i class="bi bi-shield-lock me-2"></i> Đổi mật khẩu</a></li>
                                
                                <li><hr class="dropdown-divider"></li>
                                
                                <li><span class="dropdown-header">Giao dịch</span></li>
                                <li><a class="dropdown-item" href="borrow-history"><i class="bi bi-journal-bookmark me-2"></i> Sách đang mượn</a></li>
                                <li><a class="dropdown-item" href="orders"><i class="bi bi-box-seam me-2"></i> Lịch sử mua hàng</a></li>
                                
                                <li><hr class="dropdown-divider"></li>
                                
                                <li><a class="dropdown-item fw-bold" href="logout" style="color: #ED553B !important;"><i class="bi bi-box-arrow-right text-danger me-2"></i> Đăng xuất</a></li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
                <a href="cart" class="action-link position-relative">
                    <i class="bi bi-cart3"></i> GIỎ HÀNG
                    <span id="cart-badge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        ${sessionScope.cart != null ? sessionScope.cart.items.size() : 0}
                    </span>
                </a>
                <a href="wishlist" class="action-link"><i class="bi bi-heart"></i> YÊU THÍCH</a>
            </div>
        </div>
    </div>
</div>

<nav class="main-nav">
    <div class="container">
        <ul class="nav-list">
            <li class="nav-item"><a href="home" class="nav-link ${currentURI.endsWith('/home') ? 'active' : ''}">Trang Chủ</a></li>
            <li class="nav-item"><a href="shop?category=featured" class="nav-link ${param.category == 'featured' ? 'active' : ''}">Sách Nổi Bật</a></li>
            <li class="nav-item"><a href="shop?category=new" class="nav-link ${param.category == 'new' ? 'active' : ''}">Sách Mới</a></li>
            <li class="nav-item"><a href="shop?category=most-viewed" class="nav-link ${param.category == 'most-viewed' ? 'active' : ''}">Được Xem Nhiều</a></li>
            <li class="nav-item"><a href="shop?category=hot" class="nav-link ${param.category == 'hot' ? 'active' : ''}">Bán Chạy</a></li>
            <li class="nav-item"><a href="shop?category=ebook" class="nav-link ${param.category == 'ebook' ? 'active' : ''}">Truyện Online</a></li>
        </ul>
    </div>
</nav>

<div id="ai-chat-box">
    <div class="chat-header"><span><i class="bi bi-robot me-2"></i>AI ASSISTANT</span><i class="bi bi-x-lg" onclick="toggleAIChat()" style="cursor:pointer"></i></div>
    <div id="chat-content" style="flex:1; overflow-y:auto; padding:20px; background:#f9f9f9; color:#333; font-weight:600;">Chào Hải! Sếp cần tìm cuốn sách gì?</div>
    <div class="chat-input-group"><input type="text" id="user-msg-input" placeholder="Hỏi AI gì đi sếp..."><button onclick="sendChatMessage()"><i class="bi bi-send-fill"></i></button></div>
</div>
<div class="chatbot-btn" onclick="toggleAIChat()"><i class="bi bi-chat-right-dots-fill"></i></div>

<script>
    function toggleAIChat() { const box = document.getElementById('ai-chat-box'); box.style.display = (box.style.display === 'flex') ? 'none' : 'flex'; }
    function sendChatMessage() {
        const input = document.getElementById('user-msg-input');
        const content = document.getElementById('chat-content');
        if(input.value.trim()){
            content.innerHTML += `<div style="text-align:right; margin-bottom:10px;"><span style="background:#e3f2fd; padding:8px 12px; border-radius:15px; display:inline-block; font-size:0.85rem; font-weight:600;">` + input.value + `</span></div>`;
            input.value = ''; content.scrollTop = content.scrollHeight;
        }
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    window.onload = function() {
        <c:if test="${not empty sessionScope.borrowMsg}">
            Swal.fire({ icon: 'info', title: 'Thông báo', text: '${sessionScope.borrowMsg}', confirmButtonColor: '#173F5F' });
        </c:if>
        <c:if test="${not empty sessionScope.cartMsg}">
            Swal.fire({ icon: 'success', title: 'Thành công', text: '${sessionScope.cartMsg}', confirmButtonColor: '#ED553B' });
        </c:if>
    }
</script>

<c:if test="${not empty sessionScope.wishMsg}">
    <script>
        Swal.fire({
            icon: '${sessionScope.wishType}', 
            title: 'Thông báo',
            text: '${sessionScope.wishMsg}',
            showConfirmButton: false,
            timer: 2500,
            toast: true,
            position: 'top-end'
        });
    </script>
    <c:remove var="wishMsg" scope="session" />
    <c:remove var="wishType" scope="session" />
</c:if>

<c:remove var="borrowMsg" scope="session" />
<c:remove var="cartMsg" scope="session" />