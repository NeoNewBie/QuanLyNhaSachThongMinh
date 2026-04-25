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

    /* 🛑 GIAO DIỆN CHATBOT AI VIP PRO 🛑 */
    #ai-chat-box { 
        display: none; position: fixed; bottom: 105px; right: 30px; 
        width: 360px; height: 520px; background: #f8f9fa; 
        border-radius: 20px; box-shadow: 0 15px 40px rgba(0,0,0,0.15); 
        z-index: 9999; flex-direction: column; overflow: hidden; border: 1px solid #e0e0e0;
        animation: chatSlideUp 0.3s ease-out;
    }
    @keyframes chatSlideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
    
    .chat-header { 
        background: linear-gradient(135deg, var(--primary-color) 0%, #2A618A 100%); 
        color: white; padding: 18px 20px; font-weight: 800; 
        display: flex; justify-content: space-between; align-items: center; 
        box-shadow: 0 2px 10px rgba(0,0,0,0.1); z-index: 2;
    }
    
    /* Khu vực chứa tin nhắn */
    #chat-content { 
        flex: 1; overflow-y: auto; padding: 20px; background: #fdfdfd; 
        display: flex; flex-direction: column; gap: 15px; scroll-behavior: smooth;
    }
    #chat-content::-webkit-scrollbar { width: 6px; }
    #chat-content::-webkit-scrollbar-thumb { background: #ccc; border-radius: 10px; }

    /* Bong bóng tin nhắn */
    .msg-bubble { max-width: 85%; padding: 10px 15px; font-size: 0.85rem; line-height: 1.4; position: relative; word-wrap: break-word;}
    .msg-user { 
        background: linear-gradient(135deg, #e3f2fd, #bbdefb); color: #0d47a1; 
        border-radius: 18px 18px 0 18px; align-self: flex-end; font-weight: 600;
        box-shadow: 0 2px 5px rgba(13, 71, 161, 0.1);
    }
    .msg-ai { 
        background: #fff; color: #333; border: 1px solid #eee;
        border-radius: 18px 18px 18px 0; align-self: flex-start; font-weight: 500;
        box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    
    /* Khung nhập liệu */
    .chat-input-group { padding: 15px; background: #fff; border-top: 1px solid #eee; display: flex; gap: 10px; z-index: 2;}
    .chat-input-group input { 
        flex: 1; border-radius: 20px; border: 1px solid #ddd; 
        padding: 10px 15px; outline: none; font-size: 0.85rem; transition: 0.3s; background: #f9f9f9;
    }
    .chat-input-group input:focus { border-color: var(--primary-color); background: #fff; box-shadow: 0 0 0 3px rgba(23, 63, 95, 0.1);}
    .chat-input-group button { 
        background: linear-gradient(135deg, var(--primary-color), var(--accent-color)); 
        color: white; border: none; border-radius: 50%; width: 42px; height: 42px; 
        display: flex; align-items: center; justify-content: center; transition: 0.3s; box-shadow: 0 4px 10px rgba(237, 85, 59, 0.3);
    }
    .chat-input-group button:hover { transform: scale(1.1); }

    /* Nút kích hoạt Chatbot (Có hiệu ứng nhịp đập) */
    .chatbot-btn { 
        position: fixed; bottom: 30px; right: 30px; width: 65px; height: 65px; 
        border-radius: 50%; background: linear-gradient(135deg, var(--accent-color), #d4442d); 
        color: white; display: flex; align-items: center; justify-content: center; 
        font-size: 28px; cursor: pointer; z-index: 9998; transition: 0.3s;
        animation: pulse-glow 2s infinite;
    }
    .chatbot-btn:hover { transform: scale(1.1) rotate(10deg); animation: none; box-shadow: 0 10px 25px rgba(237, 85, 59, 0.5);}
    @keyframes pulse-glow {
        0% { box-shadow: 0 0 0 0 rgba(237, 85, 59, 0.6); }
        70% { box-shadow: 0 0 0 15px rgba(237, 85, 59, 0); }
        100% { box-shadow: 0 0 0 0 rgba(237, 85, 59, 0); }
    }
    
    /* Hiệu ứng gõ chữ của AI */
    .typing-dots span { display: inline-block; width: 6px; height: 6px; background-color: #888; border-radius: 50%; margin: 0 2px; animation: bounce 1.4s infinite ease-in-out both; }
    .typing-dots span:nth-child(1) { animation-delay: -0.32s; }
    .typing-dots span:nth-child(2) { animation-delay: -0.16s; }
    @keyframes bounce { 0%, 80%, 100% { transform: scale(0); } 40% { transform: scale(1); } }
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
    <div class="chat-header">
        <span class="fs-6"><i class="bi bi-robot fs-4 me-2"></i> SMART AI</span>
        <i class="bi bi-x-lg fs-5" onclick="toggleAIChat()" style="cursor:pointer; opacity: 0.8; transition: 0.3s;" onmouseover="this.style.opacity='1'" onmouseout="this.style.opacity='0.8'"></i>
    </div>
    
    <div id="chat-content">
        <div class="msg-bubble msg-ai">
            <div class="d-flex align-items-center mb-1"><i class="bi bi-stars text-warning me-1"></i><strong style="font-size:0.75rem; color:#ED553B;">AI Assistant</strong></div>
            Chào Hải! Sếp cần em tư vấn cuốn sách nào hay có tâm sự gì muốn giải bày không? 🥰
        </div>
    </div>
    
    <div class="chat-input-group">
        <input type="text" id="user-msg-input" placeholder="Gõ tin nhắn cho AI..." autocomplete="off">
        <button onclick="sendChatMessage()"><i class="bi bi-send-fill fs-5"></i></button>
    </div>
</div>
<div class="chatbot-btn" onclick="toggleAIChat()" title="Chat với AI"><i class="bi bi-chat-dots-fill"></i></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // 🛑 1. PHỤC HỒI TRÍ NHỚ KHI VỪA LOAD TRANG
    document.addEventListener("DOMContentLoaded", function() {
        const savedChat = sessionStorage.getItem('smartLibChatHistory');
        const contentBox = document.getElementById('chat-content');
        
        // Nếu đã có lịch sử chat trong session thì moi ra in lại
        if (savedChat) {
            contentBox.innerHTML = savedChat;
            contentBox.scrollTop = contentBox.scrollHeight;
        }
    });

    // 2. Bật tắt khung chat
    function toggleAIChat() { 
        const box = document.getElementById('ai-chat-box'); 
        box.style.display = (box.style.display === 'flex') ? 'none' : 'flex'; 
        if(box.style.display === 'flex') document.getElementById('user-msg-input').focus();
    }

    // 3. Bắt sự kiện ấn phím Enter để gửi
    document.getElementById("user-msg-input").addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            sendChatMessage();
        }
    });

    // 🛑 4. HÀM GỬI VÀ NHẬN TIN NHẮN TỪ SERVLET CÓ LƯU TRÍ NHỚ 🛑
    function sendChatMessage() {
        const input = document.getElementById('user-msg-input');
        const content = document.getElementById('chat-content');
        const msg = input.value.trim();
        
        if(msg) {
            // In tin nhắn của Sếp
            content.innerHTML += `<div class="msg-bubble msg-user">` + msg + `</div>`;
            input.value = ''; 
            content.scrollTop = content.scrollHeight;
            
            // 💾 LƯU LỊCH SỬ NGAY LẬP TỨC
            sessionStorage.setItem('smartLibChatHistory', content.innerHTML);
            
            // Hiện hiệu ứng AI đang gõ chữ...
            const typingId = "typing-" + Date.now();
            content.innerHTML += `
                <div id="`+typingId+`" class="msg-bubble msg-ai" style="padding: 12px 15px;">
                    <div class="typing-dots"><span></span><span></span><span></span></div>
                </div>`;
            content.scrollTop = content.scrollHeight;

            // GỌI API TỚI CHATBOT SERVLET
            fetch('chatbot', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'message=' + encodeURIComponent(msg)
            })
            .then(response => response.text())
            .then(data => {
                // Xóa cục "đang gõ"
                const typingEl = document.getElementById(typingId);
                if(typingEl) typingEl.remove();
                
                // In câu trả lời của AI ra màn hình
                content.innerHTML += `
                    <div class="msg-bubble msg-ai">
                        <div class="d-flex align-items-center mb-1"><i class="bi bi-stars text-warning me-1"></i><strong style="font-size:0.75rem; color:#ED553B;">AI Assistant</strong></div>
                        ` + data + `
                    </div>`;
                content.scrollTop = content.scrollHeight;
                
                // 💾 LƯU LỊCH SỬ SAU KHI AI TRẢ LỜI XONG
                sessionStorage.setItem('smartLibChatHistory', content.innerHTML);
            })
            .catch(err => {
                const typingEl = document.getElementById(typingId);
                if(typingEl) typingEl.remove();
                content.innerHTML += `<div class="msg-bubble msg-ai" style="color: red;"><i class="bi bi-exclamation-triangle-fill me-1"></i> Oops! AI đang bận rộn, sếp thử lại sau nhé!</div>`;
                content.scrollTop = content.scrollHeight;
                
                // 💾 LƯU LỊCH SỬ CẢ THÔNG BÁO LỖI
                sessionStorage.setItem('smartLibChatHistory', content.innerHTML);
            });
        }
    }

    // 🛑 5. XỬ LÝ THÔNG BÁO SWEET ALERT (GIỮ NGUYÊN HOÀN TOÀN) 🛑
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