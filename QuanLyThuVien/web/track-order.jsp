<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%-- Phần CSS cho thanh tiến trình --%>
<style>
    .stepper { display: flex; justify-content: space-between; position: relative; margin-bottom: 30px; }
    .stepper::before { content: ""; position: absolute; top: 50%; left: 0; height: 2px; width: 100%; background: #ddd; z-index: 1; transform: translateY(-50%); }
    .step { position: relative; z-index: 2; background: white; width: 40px; height: 40px; border-radius: 50%; border: 3px solid #ddd; display: flex; align-items: center; justify-content: center; transition: 0.3s; }
    .step.active { border-color: #ED553B; background: #ED553B; color: white; }
    .step.completed { border-color: #173F5F; background: #173F5F; color: white; }
    .step-label { position: absolute; top: 45px; font-size: 0.8rem; width: 100px; text-align: center; font-weight: bold; }
</style>

<div class="container mt-5">
    <div class="auth-card">
        <h4 class="text-center mb-5 fw-bold">THEO DÕI ĐƠN MƯỢN SÁCH</h4>
        
        <div class="stepper">
            <%-- Dựa vào biến status từ Servlet gửi sang để gán class active/completed --%>
            <div class="step ${status >= 0 ? 'completed' : ''} ${status == 0 ? 'active' : ''}">
                <i class="bi bi-file-earmark-plus"></i>
                <div class="step-label">Đã đặt</div>
            </div>
            <div class="step ${status >= 1 ? 'completed' : ''} ${status == 1 ? 'active' : ''}">
                <i class="bi bi-box-seam"></i>
                <div class="step-label">Đang chuẩn bị</div>
            </div>
            <div class="step ${status >= 2 ? 'completed' : ''} ${status == 2 ? 'active' : ''}">
                <i class="bi bi-geo-alt"></i>
                <div class="step-label">Sẵn sàng</div>
            </div>
            <div class="step ${status >= 3 ? 'completed' : ''} ${status == 3 ? 'active' : ''}">
                <i class="bi bi-book"></i>
                <div class="step-label">Đang mượn</div>
            </div>
        </div>
        
        <div class="text-center mt-5">
            <p class="text-muted">Mã phiếu mượn: <strong>#LIB12345</strong></p>
            <c:choose>
                <c:when test="${status == 2}">
                    <div class="alert alert-success">Sách đã sẵn sàng tại quầy! Sếp Hải qua nhận ngay nhé.</div>
                </c:when>
                <c:otherwise>
                    <p>Hệ thống đang xử lý, sếp vui lòng đợi chút nhé.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>