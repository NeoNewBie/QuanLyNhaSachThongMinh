<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chương ${currentNum} - ${detail.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body { background-color: #1a1a1a; color: #d1d1d1; font-family: 'Inter', sans-serif; scroll-behavior: smooth; }
        .reader-wrapper { max-width: 1000px; margin: 0 auto; min-height: 100vh; background: #242424; padding: 20px 0; box-shadow: 0 0 100px rgba(0,0,0,0.5); }
        .comic-nav-bar { background: #333; padding: 10px 20px; border-radius: 8px; margin: 20px; border: 1px solid #444; }
        .chapter-select { background: #444; color: white; border: 1px solid #555; border-radius: 20px; padding: 5px 15px; font-weight: bold; }
        .nav-btn { min-width: 100px; font-weight: bold; border-radius: 20px; }
        .content-area { font-size: 1.25rem; line-height: 2; text-align: justify; }
        .book-title-link { color: #aaa; text-decoration: none; transition: 0.3s; }
        .book-title-link:hover { color: #ED553B; }
        .comic-page-container img { display: block; margin: 0 auto; width: 100%; max-width: 850px; height: auto; margin-bottom: 2px; }
        .lock-overlay { background: rgba(36, 36, 36, 0.98); border: 2px solid #ED553B; backdrop-filter: blur(15px); }
        ::-webkit-scrollbar { width: 10px; }
        ::-webkit-scrollbar-track { background: #1a1a1a; }
        ::-webkit-scrollbar-thumb { background: #ED553B; border-radius: 10px; }
        
        .modal-content.smart-modal {
            border: none;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 20px 50px rgba(0,0,0,0.5);
            background-color: #ffffff;
            color: #333333;
        }
        .smart-modal .modal-header {
            background: #f8f9fa;
            border-bottom: 1px solid #eeeeee;
            padding: 20px 30px;
        }
        .smart-modal .modal-title {
            color: #173F5F;
            font-weight: 800;
            font-size: 1.2rem;
        }
        .smart-modal .btn-close {
            filter: grayscale(1);
        }
        .smart-modal .modal-body {
            padding: 40px 30px;
        }
        .price-highlight {
            background: #fff5f3;
            border: 1px dashed #fcdbd5;
            color: #ED553B;
        }
        .qr-container {
            background: #ffffff;
            padding: 15px;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
            border: 1px solid #f0f0f0;
            display: inline-block;
        }
        .status-pulse {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-weight: 600;
            color: #173F5F;
            margin-top: 25px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 50px;
        }
        .btn-smart {
            background-color: #173F5F;
            color: white;
            border-radius: 50px;
            font-weight: bold;
            padding: 14px;
            border: none;
            transition: 0.3s;
        }
        .btn-smart:hover {
            background-color: #ED553B;
            color: white;
        }
    </style>
</head>
<body>
    <div class="reader-wrapper">
        <div class="px-4 mb-3 d-flex justify-content-between align-items-center">
            <a href="book-detail?id=${detail.id}" class="book-title-link fw-bold">
                <i class="bi bi-arrow-left"></i> ${detail.title}
            </a>
            <span class="badge bg-secondary rounded-pill px-3">CHẾ ĐỘ ĐỌC TẬP TRUNG</span>
        </div>

        <div class="comic-nav-bar d-flex justify-content-center align-items-center gap-3 shadow-lg">
            <a href="read?id=${detail.id}&num=${prevNum}" class="btn btn-outline-warning nav-btn ${prevNum == -1 ? 'disabled' : ''}">
                <i class="bi bi-chevron-left"></i> TRƯỚC
            </a>
            <select class="chapter-select" onchange="window.location.href='read?id=${detail.id}&num=' + this.value">
                <c:forEach items="${allChapters}" var="allC">
                    <option value="${allC.chapterNumber}" ${allC.chapterNumber == currentNum ? 'selected' : ''}>
                        Chương ${allC.chapterNumber}: ${allC.title}
                    </option>
                </c:forEach>
            </select>
            <a href="read?id=${detail.id}&num=${nextNum}" class="btn btn-warning nav-btn text-dark ${nextNum == -1 ? 'disabled' : ''}">
                SAU <i class="bi bi-chevron-right"></i>
            </a>
        </div>

        <div class="text-center mb-4">
            <h2 class="text-white fw-bold">Chương ${chapter.chapterNumber}: ${chapter.title}</h2>
            <p class="text-muted small">Tác giả: ${detail.author}</p>
        </div>

        <div class="content-area">
            <c:choose>
                <c:when test="${chapter.isFree or chapter.unlocked}">
                    <div class="comic-page-container">
                        <c:choose>
                            <c:when test="${not empty chapter.imageList}">
                                <c:forEach items="${chapter.imageList}" var="imgUrl">
                                    <img src="${pageContext.request.contextPath}/image-proxy?url=${imgUrl}" loading="lazy" alt="Page">
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="p-5 text-center text-light">${chapter.content}</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="container text-center py-5">
                        <div class="p-5 lock-overlay rounded-4 shadow-lg mx-auto" style="max-width: 550px;">
                            <i class="bi bi-lock-fill display-1 text-warning mb-3"></i>
                            <h3 class="text-white fw-bold mb-3">CHƯƠNG TRUYỆN VIP</h3>
                            <p class="text-muted mb-4">Thanh toán để mở khóa toàn bộ nội dung chương này</p>
                            <div class="bg-dark p-3 rounded-4 mb-4 border border-secondary">
                                <span class="text-muted small d-block">Phí mở khóa:</span>
                                <h2 class="text-warning fw-bold m-0"><fmt:formatNumber value="${chapter.price}" pattern="###,###"/> đ</h2>
                            </div>
                            <button onclick="showVipModal('${chapter.chapterID}', 'Chương ${chapter.chapterNumber}: ${chapter.title}', '${chapter.price}')" 
                                    class="btn btn-warning text-dark btn-lg rounded-pill px-5 fw-bold w-100 py-3">
                                <i class="bi bi-qr-code-scan me-2"></i> QUÉT MÃ MỞ KHÓA
                            </button>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="comic-nav-bar d-flex justify-content-center align-items-center gap-3 mt-5 mb-5 shadow-lg">
            <c:choose>
                <c:when test="${prevNum != -1}">
                    <a href="read?id=${detail.id}&num=${prevNum}" class="btn btn-outline-warning nav-btn">
                        <i class="bi bi-chevron-left"></i> CHƯƠNG TRƯỚC
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-outline-secondary nav-btn disabled">CHƯƠNG TRƯỚC</button>
                </c:otherwise>
            </c:choose>

            <select class="chapter-select" onchange="window.location.href='read?id=${detail.id}&num=' + this.value">
                <c:forEach items="${allChapters}" var="allC">
                    <option value="${allC.chapterNumber}" ${allC.chapterNumber == currentNum ? 'selected' : ''}>
                        Chương ${allC.chapterNumber}
                    </option>
                </c:forEach>
            </select>

            <c:choose>
                <c:when test="${nextNum != -1}">
                    <a href="read?id=${detail.id}&num=${nextNum}" class="btn btn-warning nav-btn text-dark">
                        SAU <i class="bi bi-chevron-right"></i>
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-outline-secondary nav-btn disabled">SAU</button>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

    <div class="modal fade" id="vipModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content smart-modal">
          <div class="modal-header">
            <h5 class="modal-title"><i class="bi bi-shield-lock-fill me-2"></i>Xác Nhận Mở Khóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" onclick="stopChecking()"></button>
          </div>
          <div class="modal-body text-center">
            
            <div id="step1_info">
                <h4 class="fw-bold mb-2 text-dark" id="txt_chapter_name"></h4>
                <div class="p-3 rounded-4 mb-4 price-highlight">
                    <span class="fs-3 fw-bold" id="txt_price_display"></span>
                </div>
                <input type="hidden" id="txt_chapterId">
                <button type="button" onclick="showQRCode()" class="btn-smart w-100 shadow-sm">
                    TIẾP TỤC THANH TOÁN <i class="bi bi-arrow-right ms-2"></i>
                </button>
            </div>

            <div id="step2_qr" style="display: none;">
                <h5 class="fw-bold mb-3" style="color: #173F5F;">QUÉT MÃ BẰNG APP NGÂN HÀNG</h5>
                <div class="qr-container">
                    <img id="img_qr_code" src="" style="width: 230px; height: 230px;">
                </div>
                
                <div class="status-pulse shadow-sm">
                    <div class="spinner-grow spinner-grow-sm text-success" role="status"></div>
                    <span id="waiting-text">Hệ thống đang chờ nhận tiền...</span>
                </div>

                <button type="button" onclick="backToStep1()" class="btn btn-link text-decoration-none text-muted mt-4 fw-bold small">
                    <i class="bi bi-arrow-left me-1"></i> Quay lại
                </button>
            </div>

          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let currentPrice = 0;
        let currentUserId = '${sessionScope.acc.id}'; 
        let checkInterval = null;

        function showVipModal(id, name, price) {
            document.getElementById('txt_chapterId').value = id;
            document.getElementById('txt_chapter_name').innerText = name;
            currentPrice = price;
            document.getElementById('txt_price_display').innerHTML = Number(price).toLocaleString('vi-VN') + ' VNĐ';
            document.getElementById('step1_info').style.display = 'block';
            document.getElementById('step2_qr').style.display = 'none';
            new bootstrap.Modal(document.getElementById('vipModal')).show();
        }

        function showQRCode() {
            let chapterId = document.getElementById('txt_chapterId').value;
            // Chuỗi bí mật ghép nối vạn vật
            let desc = "MUACHAP U" + currentUserId + " C" + chapterId;
            
            document.getElementById('step1_info').style.display = 'none';
            document.getElementById('step2_qr').style.display = 'block';

            // Hiện ảnh Loading mượt mà trong lúc gõ cửa PayOS
            document.getElementById('img_qr_code').src = "https://i.gifer.com/ZKZg.gif"; 
            document.getElementById('waiting-text').innerText = "Đang kết nối hệ thống PayOS...";
            document.getElementById('waiting-text').className = "text-muted";

            // Gọi Servlet để lấy QR Xịn
            fetch('generate-qr?amount=' + currentPrice + '&desc=' + encodeURIComponent(desc))
            .then(res => res.text())
            .then(qrData => {
                if(qrData !== "error") {
                    // Cắm mã QR thật vào
                    document.getElementById('img_qr_code').src = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + encodeURIComponent(qrData);
                    document.getElementById('waiting-text').innerText = "Hệ thống đang chờ nhận tiền...";
                    document.getElementById('waiting-text').className = "text-warning fw-bold";
                    
                    // Bắt đầu rình tiền
                    startChecking(desc);
                } else {
                    Swal.fire('Lỗi kết nối', 'Không thể kết nối PayOS. Sếp kiểm tra lại API Key nhé!', 'error');
                    backToStep1();
                }
            });
        }

        function startChecking(orderId) {
            if(checkInterval) clearInterval(checkInterval);
            
            checkInterval = setInterval(function() {
                fetch('check-payment-status?orderId=' + encodeURIComponent(orderId))
                    .then(res => res.text())
                    .then(status => {
                        if (status.trim() === "success") {
                            clearInterval(checkInterval);
                            
                            document.getElementById('waiting-text').innerText = "Thanh toán thành công!";
                            document.getElementById('waiting-text').classList.remove('text-dark');
                            document.getElementById('waiting-text').classList.add('text-success');

                            Swal.fire({
                                icon: 'success',
                                title: 'Giao dịch thành công!',
                                text: 'Đang mở khóa truyện...',
                                timer: 2000,
                                showConfirmButton: false,
                                backdrop: `rgba(0,0,0,0.8)`
                            }).then(() => {
                                window.location.reload();
                            });
                        }
                    })
                    .catch(err => console.log("Waiting..."));
            }, 3000);
        }

        function stopChecking() {
            if(checkInterval) clearInterval(checkInterval);
        }

        function backToStep1() {
            stopChecking();
            document.getElementById('step1_info').style.display = 'block';
            document.getElementById('step2_qr').style.display = 'none';
        }
    </script>
</body>
</html>