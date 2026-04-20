<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>
    .footer-area { background-color: #fcfcfc; padding: 60px 0 30px; font-family: 'Inter', sans-serif; border-top: 1px solid #eee; margin-top: 50px; }
    .footer-area h5 { font-weight: 800; color: #173F5F; margin-bottom: 25px; font-size: 1.05rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .footer-area p { color: #666; font-size: 0.9rem; line-height: 1.8; }
    .footer-link { display: block; color: #555; text-decoration: none; margin-bottom: 12px; font-size: 0.9rem; font-weight: 500; transition: 0.3s; }
    .footer-link:hover { color: #ED553B; padding-left: 5px; }
    
    .contact-item { display: flex; align-items: flex-start; margin-bottom: 15px; color: #555; font-size: 0.9rem; }
    .contact-item i { color: #ED553B; font-size: 1.1rem; margin-right: 12px; margin-top: 2px; }
    
    .newsletter-input { border: 1px solid #ddd; border-radius: 8px 0 0 8px; padding: 10px 15px; font-size: 0.9rem; outline: none; width: 100%; background: #fff;}
    .newsletter-btn { background: #ED553B; color: white; border: none; border-radius: 0 8px 8px 0; padding: 10px 20px; font-weight: bold; transition: 0.3s;}
    .newsletter-btn:hover { background: #173F5F; }
    
    .social-icon { display: inline-flex; align-items: center; justify-content: center; width: 35px; height: 35px; border-radius: 50%; background: #eee; color: #555; transition: 0.3s; text-decoration: none; }
    .social-icon:hover { background: #ED553B; color: #fff; transform: translateY(-3px); }
    
    .footer-bottom { text-align: center; padding-top: 20px; margin-top: 40px; border-top: 1px solid #eee; color: #999; font-size: 0.85rem; font-weight: 500;}
</style>

<footer class="footer-area">
    <div class="container">
        <div class="row g-4">
            <div class="col-lg-4 col-md-6 pe-lg-4">
                <h5><i class="bi bi-book-half" style="color: #ED553B;"></i> SMART LIB.</h5>
                <p>Hệ thống Thư viện thông minh mang đến trải nghiệm mượn sách và đọc truyện trực tuyến hoàn hảo. Nơi cung cấp hàng ngàn đầu sách đa dạng, cập nhật liên tục để phục vụ nhu cầu của sinh viên UTE.</p>
                <div class="d-flex gap-2 mt-3">
                    <a href="#" class="social-icon"><i class="bi bi-facebook"></i></a>
                    <a href="#" class="social-icon"><i class="bi bi-instagram"></i></a>
                    <a href="#" class="social-icon"><i class="bi bi-youtube"></i></a>
                </div>
            </div>

            <div class="col-lg-2 col-md-6">
                <h5>Khám Phá</h5>
                <a href="shop?category=new" class="footer-link">Sách Mới Cập Nhật</a>
                <a href="shop?category=hot" class="footer-link">Sách Bán Chạy</a>
                <a href="shop?category=most-viewed" class="footer-link">Được Xem Nhiều</a>
                <a href="shop?category=ebook" class="footer-link">Truyện Online</a>
                <a href="shop" class="footer-link">Tất Cả Sách</a>
            </div>

            <div class="col-lg-3 col-md-6">
                <h5>Thông Tin Liên Hệ</h5>
                <div class="contact-item">
                    <i class="bi bi-geo-alt-fill"></i>
                    <span>Trường Đại Học Sư Phạm Kỹ Thuật - Đại Học Đà Nẵng</span>
                </div>
                <div class="contact-item">
                    <i class="bi bi-telephone-fill"></i>
                    <span>+84 123 456 789</span>
                </div>
                <div class="contact-item">
                    <i class="bi bi-envelope-fill"></i>
                    <span>support@smartlib.ute.udn.vn</span>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <h5>Đăng Ký Nhận Tin</h5>
                <p>Nhập email của Hải để không bỏ lỡ những tựa sách "đỉnh" nhất sắp ra mắt.</p>
                <form class="d-flex mt-3" onsubmit="event.preventDefault();">
                    <input type="email" class="newsletter-input" placeholder="Nhập email...">
                    <button type="submit" class="newsletter-btn"><i class="bi bi-send-fill"></i></button>
                </form>
            </div>
        </div>
        
        <div class="footer-bottom">
            &copy; 2026 Smart Library. Được thiết kế và phát triển bởi Lê Hoàng Hải (Lớp 225LTC#01).
        </div>
    </div>
</footer>

<div class="toast-container position-fixed bottom-0 end-0 p-4" style="z-index: 1100;">
    <div id="cartToast" class="toast align-items-center text-white bg-success border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body fw-bold fs-6" id="toastMessage">
                </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Tìm tất cả các nút Thêm Giỏ Hàng (Loại trừ các nút Mua Ngay)
    const cartLinks = document.querySelectorAll('a[href^="add-to-cart?id="]');
    
    cartLinks.forEach(link => {
        if(!link.href.includes('action=buy')) {
            link.addEventListener('click', function(e) {
                e.preventDefault(); // 🛑 CHẶN LOAD LẠI TRANG
                
                // Gửi yêu cầu ngầm xuống AddToCartServlet
                fetch(this.href + '&ajax=true')
                .then(res => res.json())
                .then(data => {
                    if(data.status === 'success') {
                        // Nảy số lượng trên thanh Header
                        const badge = document.getElementById('cart-badge');
                        if(badge) {
                            badge.innerText = data.cartSize;
                        }
                        
                        // Bắn Toast thông báo trượt từ góc màn hình ra
                        document.getElementById('toastMessage').innerHTML = `<i class="bi bi-check-circle-fill me-2"></i> Đã thêm <b>${data.bookName}</b> vào giỏ!`;
                        const toastEl = document.getElementById('cartToast');
                        const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
                        toast.show();
                    }
                })
                .catch(err => console.error("Lỗi thêm giỏ hàng:", err));
            });
        }
    });
});
</script>