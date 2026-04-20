<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đang đọc: ${detail.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        body { background-color: #1a1a1a; color: #d1d1d1; font-family: 'Inter', sans-serif; scroll-behavior: smooth; }
        .reader-wrapper { max-width: 900px; margin: 0 auto; min-height: 100vh; background: #242424; padding: 60px; box-shadow: 0 0 100px rgba(0,0,0,0.5); }
        .reader-nav { border-bottom: 1px solid #333; padding-bottom: 20px; margin-bottom: 40px; }
        .content-area { font-size: 1.25rem; line-height: 2; text-align: justify; white-space: pre-line; }
        .book-title { color: #fff; font-weight: 800; letter-spacing: -1px; }
        ::-webkit-scrollbar { width: 10px; }
        ::-webkit-scrollbar-track { background: #1a1a1a; }
        ::-webkit-scrollbar-thumb { background: #ED553B; border-radius: 10px; }
        .chapter-content { padding-bottom: 40px; margin-bottom: 40px; border-bottom: 1px dashed #444; }
        
        /* Style cho rèm khóa */
        .lock-overlay {
            background: rgba(36, 36, 36, 0.95);
            border: 1px solid #444;
            backdrop-filter: blur(10px);
        }
    </style>
</head>
<body>
    <div class="reader-wrapper">
        <div class="reader-nav d-flex justify-content-between align-items-center">
            <a href="book-detail?id=${detail.id}" class="text-decoration-none text-muted fw-bold">
                <i class="bi bi-arrow-left"></i> QUAY LẠI
            </a>
            <span class="badge bg-secondary rounded-pill px-3">CHẾ ĐỘ ĐỌC TẬP TRUNG</span>
        </div>

        <div class="text-center mb-5 border-bottom border-secondary pb-4">
            <h1 class="book-title display-5">${detail.title}</h1>
            <p class="text-muted small text-uppercase tracking-widest">Tác giả: ${detail.author}</p>
        </div>

        <div class="content-area">
            <c:choose>
                <%-- Trường hợp DB chưa có chương nào --%>
                <c:when test="${empty listC}">
                    <div class="text-center py-5">
                        <i class="bi bi-journal-x display-1 text-secondary mb-3"></i>
                        <h4 class="text-muted">Truyện đang được cập nhật nội dung...</h4>
                    </div>
                </c:when>

                <%-- Đã có dữ liệu trong DB --%>
                <c:otherwise>
                    <c:set var="hitLocked" value="false" />
                    
                    <%-- Vòng lặp quét qua từng Chương --%>
                    <c:forEach items="${listC}" var="c">
                        <%-- Chỉ hiển thị khi chưa đụng trúng Rèm khóa --%>
                        <c:if test="${not hitLocked}">
                            
                            <c:choose>
                                <%-- XÉT ĐIỀU KIỆN: Chương Free HOẶC (Chương tính phí nhưng User đã mua) --%>
                                <c:when test="${c.isFree or not locked}">
                                    <div class="chapter-content">
                                        <h3 class="text-warning text-center mb-4 fw-bold">Chương ${c.chapterNumber}: ${c.title}</h3>
                                        <div>${c.content}</div>
                                    </div>
                                </c:when>
                                
                                <%-- XÉT ĐIỀU KIỆN: Chương Tính phí MÀ User chưa mua -> QUĂNG Ổ KHÓA --%>
                                <c:otherwise>
                                    <c:set var="hitLocked" value="true" />
                                    
                                    <div class="position-relative mt-5 pt-4">
                                        <div style="filter: blur(8px); opacity: 0.2; user-select: none;">
                                            <h3 class="text-center mb-4 fw-bold">Chương ${c.chapterNumber}: ${c.title}</h3>
                                            <p>Nội dung chương này đã bị khóa. Vui lòng mở khóa để đọc tiếp...</p>
                                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit...</p>
                                            <p>Cảm ơn sếp Hải đã ủng hộ tác giả và Smart Library!</p>
                                        </div>
                                        
                                        <div class="position-absolute top-50 start-50 translate-middle text-center w-100 mt-4">
                                            <div class="p-5 lock-overlay rounded-4 shadow-lg mx-auto" style="max-width: 550px;">
                                                <i class="bi bi-lock-fill display-3 text-warning mb-3"></i>
                                                <h4 class="text-white fw-bold mb-3">BẢN QUYỀN TRUYỆN ĐANG KHÓA</h4>
                                                <p class="text-muted fs-6 mb-4">Sếp cần mua trọn bộ để đọc tiếp chương này và các chương sau nhé!</p>
                                                
                                                <div class="bg-dark p-3 rounded-4 mb-4 border border-secondary">
                                                    <span class="text-muted small d-block">Giá mở khóa trọn bộ:</span>
                                                    <h2 class="text-warning fw-bold m-0"><fmt:formatNumber value="${detail.price}" pattern="###,###"/> đ</h2>
                                                </div>
                                                
                                                <%-- 🛑 ĐÃ FIX: Dẫn sang trang Checkout chọn phương thức thanh toán --%>
                                                <a href="checkout?id=${detail.id}&checkoutType=single" class="btn btn-danger btn-lg rounded-pill px-5 shadow-lg fw-bold w-100 py-3">
                                                    <i class="bi bi-cart-check-fill me-2"></i> MUA ĐỂ ĐỌC TIẾP NGAY
                                                </a>
                                                
                                                <div class="mt-3">
                                                    <a href="book-detail?id=${detail.id}" class="text-muted small text-decoration-none">Quay lại xem thông tin sách</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                        </c:if>
                    </c:forEach>
                    
                    <%-- Nếu đọc hết mà không bị khóa (Đã mua Full) --%>
                    <c:if test="${not locked and not empty listC}">
                        <div class="text-center mt-5 py-5 border-top border-secondary">
                            <p class="text-warning fst-italic">--- Bạn đã đọc đến chương mới nhất. Chúc sếp Hải đọc truyện vui vẻ! ---</p>
                            <a href="home" class="btn btn-outline-light rounded-pill px-4 mt-3">Khám phá truyện khác</a>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>