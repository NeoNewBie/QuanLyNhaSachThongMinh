<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Smart Library - Trang chủ</title>
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { font-family: 'Inter', sans-serif; background-color: #ffffff; }

        /* HERO BANNER TỰ CHẠY */
        .hero-section { background: linear-gradient(135deg, #FFF5F3 0%, #F4F8FA 100%); padding: 80px 0; overflow: hidden; }
        .hero-title { font-size: 3.5rem; font-weight: 800; color: var(--primary-color); line-height: 1.2; }
        .slider-container { border-radius: 20px; overflow: hidden; box-shadow: 0 25px 50px rgba(0,0,0,0.15); border: 5px solid white; }
        .hero-slider-img { height: 450px; width: 100%; object-fit: cover; }

        /* 6 Ô DANH MỤC */
        .category-box { height: 200px; border-radius: 15px; background-size: cover; background-position: center; position: relative; overflow: hidden; display: flex; align-items: flex-end; justify-content: center; padding: 20px; transition: 0.3s; border: 1px solid #eee; margin-bottom: 24px; text-decoration: none;}
        .category-box::before { content: ""; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(to top, rgba(0,0,0,0.7), transparent); z-index: 1; }
        .category-box h5 { color: white; position: relative; z-index: 2; font-weight: 700; margin: 0; }
        .category-box:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.15); }
        .category-box.view-all { background: white; flex-direction: column; align-items: center; justify-content: center; border: 2px solid #eee; }
        .category-box.view-all::before { display: none; }

        /* CARD SÁCH (Fix Ảnh Không Vỡ) */
        .book-card { border: none; border-radius: 15px; transition: 0.4s; padding: 20px; background: #fff; box-shadow: 0 5px 15px rgba(0,0,0,0.05); height: 100%; display: flex; flex-direction: column; position: relative;}
        .book-card:hover { transform: translateY(-10px); box-shadow: 0 20px 40px rgba(0,0,0,0.1); }
        .book-img { height: 220px; width: 100%; object-fit: contain; margin-bottom: 15px; }

        .section-header { text-align: center; margin: 60px 0 40px 0; }
        .section-header h2 { font-weight: 800; color: var(--primary-color); text-transform: uppercase; }

        /* ITEM NỔI BẬT */
        .featured-item { background: white; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); padding: 40px; margin: 60px 0; border: 1px solid #f0f0f0; }
        .featured-img { width: 100%; max-height: 400px; object-fit: contain; border-radius: 10px; }

        /* BANNER COMING SOON */
        .coming-soon-banner { background: url('https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=1200') center/cover; padding: 80px 0; border-radius: 20px; position: relative; margin: 60px 0; color: white; text-align: center; }
        .coming-soon-banner::before { content: ""; position: absolute; top:0; left:0; width:100%; height:100%; background: rgba(23, 63, 95, 0.85); border-radius: 20px; }
        
        /* 🛑 STYLE MỚI CHO NÚT ADD TO CART AJAX */
        .btn-ajax-cart { background: var(--accent-color); color: white; border: none; border-radius: 50%; width: 35px; height: 35px; display: flex; align-items: center; justify-content: center; transition: 0.3s; cursor: pointer; }
        .btn-ajax-cart:hover { transform: scale(1.1); background: #d94c33; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp">
        <jsp:param name="activeMenu" value="home" />
    </jsp:include>

    <section class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-5">
                    <h1 class="hero-title">Khám Phá <br><span style="color: var(--accent-color)">Tri Thức Mới</span></h1>
                    <p class="mt-4 text-secondary fs-5">Smart Library - Trải nghiệm mượn sách giấy và đọc ebook thông minh cho sinh viên UTE.</p>
                    <a href="shop" class="btn btn-danger rounded-pill px-5 py-3 fw-bold mt-4 shadow-lg" style="background: var(--accent-color); border:none;">KHÁM PHÁ NGAY &rarr;</a>
                </div>
                <div class="col-lg-7 d-none d-lg-block">
                    <div id="heroCarousel" class="carousel slide carousel-fade slider-container" data-bs-ride="carousel" data-bs-interval="3000">
                        <div class="carousel-inner">
                            <div class="carousel-item active"><img src="https://images.unsplash.com/photo-1507842217343-583bb7270b66?q=80&w=1000" class="hero-slider-img"></div>
                            <div class="carousel-item"><img src="https://images.unsplash.com/photo-1481627834876-b7833e8f5570?q=80&w=1000" class="hero-slider-img"></div>
                            <div class="carousel-item"><img src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=1000" class="hero-slider-img"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <div class="container">
        <div class="row mt-5">
            <div class="col-md-4"><a href="shop?cateId=1" class="category-box" style="background-image: url('https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600');"><h5>Sách Học</h5></a></div>
            <div class="col-md-4"><a href="shop" class="category-box view-all text-decoration-none"><h4>Xem tất cả<br>danh mục</h4><span class="btn btn-danger rounded-3 px-4 fw-bold mt-2" style="background: var(--accent-color); border:none;">XEM NGAY</span></a></div>
            <div class="col-md-4"><a href="shop?cateId=2" class="category-box" style="background-image: url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=600');"><h5>Sách Quản Lý</h5></a></div>
            <div class="col-md-4"><a href="shop?cateId=3" class="category-box" style="background-image: url('https://images.unsplash.com/photo-1664575602276-acd073f104c1?w=600');"><h5>Kinh Doanh</h5></a></div>
            <div class="col-md-4"><a href="shop?cateId=4" class="category-box" style="background-image: url('https://images.unsplash.com/photo-1618666012174-83b441c0bc76?w=600');"><h5>Hành Động</h5></a></div>
            <div class="col-md-4"><a href="shop?cateId=5" class="category-box" style="background-image: url('https://images.unsplash.com/photo-1587876931567-564ce588bfbd?w=600');"><h5>Trinh Thám</h5></a></div>
        </div>

        <div class="section-header"><h2>Sách Mới Xuất Bản</h2><a href="shop?category=new" class="text-danger fw-bold">Xem tất cả sản phẩm &rarr;</a></div>
        <div class="row row-cols-1 row-cols-md-4 g-4">
            <c:forEach items="${listNew}" var="b">
                <div class="col">
                    <div class="book-card text-center text-dark">
                        <a href="book-detail?bid=${b.id}" class="text-decoration-none text-dark d-block">
                            <img src="${b.coverImage.startsWith('http') ? b.coverImage : pageContext.request.contextPath.concat('/').concat(b.coverImage)}" class="book-img">
                            <h6 class="fw-bold text-dark text-truncate">${b.title}</h6>
                            <p class="small text-muted mb-2">${b.author}</p>
                        </a>
                        <div class="d-flex justify-content-between align-items-center mt-auto pt-2">
                            <h5 class="fw-bold text-danger m-0"><fmt:formatNumber value="${b.price}" pattern="###,###"/> đ</h5>
                            <button class="btn-ajax-cart shadow-sm" onclick="addToCartAsync(event, ${b.id})"><i class="bi bi-cart-plus"></i></button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="featured-item row align-items-center">
            <div class="col-md-4 text-center"><img src="https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=500" class="featured-img shadow-lg"></div>
            <div class="col-md-8 ps-md-5 mt-4 mt-md-0">
                <p class="text-muted small fw-bold">SÁCH NỔI BẬT TUẦN NÀY</p>
                <h2 class="fw-bold" style="color: var(--primary-color)">Birds gonna be happy</h2>
                <div class="text-warning mb-3">★★★★☆</div>
                <p class="text-secondary lh-lg">Hàng ngàn cuốn sách được xuất bản bởi các tác giả khác nhau mỗi ngày. Hãy mua những cuốn sách yêu thích của Hải ngay hôm nay trên Smart Lib.</p>
                <a href="shop" class="btn btn-outline-dark rounded-pill px-4 fw-bold mt-2">XEM CHI TIẾT &rarr;</a>
            </div>
        </div>

        <div class="section-header"><h2>Truyện Mới Xuất Bản</h2><a href="shop?category=ebook" class="text-danger fw-bold">Xem tất cả truyện &rarr;</a></div>
        <div class="row row-cols-1 row-cols-md-4 g-4">
            <c:forEach items="${listTruyen}" var="b">
                <div class="col">
                    <div class="book-card text-center text-dark">
                        <a href="book-detail?bid=${b.id}" class="text-decoration-none text-dark d-block">
                            <img src="${b.coverImage.startsWith('http') ? b.coverImage : pageContext.request.contextPath.concat('/').concat(b.coverImage)}" class="book-img">
                            <h6 class="fw-bold text-dark text-truncate">${b.title}</h6>
                            <div class="text-warning small mb-2">★★★★☆</div>
                        </a>
                        <div class="d-flex justify-content-center align-items-center mt-auto pt-2">
                             <button class="btn btn-sm text-white fw-bold rounded-pill px-3" style="background: var(--primary-color);" onclick="addToCartAsync(event, ${b.id})">Lưu Ebook <i class="bi bi-bookmark-plus ms-1"></i></button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="featured-item row align-items-center">
            <div class="col-md-4 text-center"><img src="https://upload.wikimedia.org/wikipedia/en/4/46/Jujutsu_kaisen.jpg" class="featured-img shadow-lg" style="height: 350px; object-fit: cover;"></div>
            <div class="col-md-8 ps-md-5 mt-4 mt-md-0">
                <p class="text-muted small fw-bold">TRUYỆN NỔI BẬT TUẦN NÀY</p>
                <h2 class="fw-bold" style="color: var(--primary-color)">Jujutsu Kaisen</h2>
                <div class="text-warning mb-3">★★★★★</div>
                <p class="text-secondary lh-lg">Yuji Itadori với sức mạnh thể chất khổng lồ sẽ đưa bạn vào những cuộc phiêu lưu đầy kịch tính tại Câu lạc bộ Nghiên cứu Ác linh...</p>
                <a href="shop?category=ebook" class="btn btn-outline-dark rounded-pill px-4 fw-bold mt-2">ĐỌC NGAY &rarr;</a>
            </div>
        </div>

        <div class="coming-soon-banner shadow">
            <div style="position: relative; z-index: 2;">
                <h2 class="display-5 fw-bold">Sắp ra mắt</h2>
                <p class="fs-5 opacity-75">Những tựa sách độc quyền đang trên đường cập bến Smart Library.</p>
                <a href="shop" class="btn btn-light rounded-pill px-5 py-2 mt-3 fw-bold">KHÁM PHÁ</a>
            </div>
        </div>

        <div class="row align-items-stretch shadow-sm mb-5" style="background: #FFF5F3; border-radius: 20px; overflow: hidden; border: 1px solid #f0f0f0;">
            <div class="col-md-5 d-none d-md-block" style="background: url('https://images.unsplash.com/photo-1620712943543-bcc4688e7485?q=80&w=800') center/cover; min-height: 380px;"></div>
            <div class="col-md-7 p-5 d-flex flex-column justify-content-center">
                <h2 class="fw-bold mb-3" style="color: var(--primary-color); font-size: 2.2rem;">Hải cần gợi ý sách hay?</h2>
                <p class="text-muted mb-4 fs-6">Đừng mất thời gian tìm kiếm! Hãy nói cho Trợ lý AI biết chủ đề sếp quan tâm, AI sẽ đề xuất những cuốn sách "chân ái" ngay lập tức.</p>
                <div class="d-flex bg-white p-2 rounded-pill shadow-sm" style="border: 1px solid #ddd;">
                    <input type="text" id="inline-ai-msg" class="form-control border-0 shadow-none px-4 bg-transparent" placeholder="Ví dụ: Tìm sách tự học C# cho người mới bắt đầu...">
                    <button class="btn text-white rounded-pill px-4 py-2 fw-bold" style="background: var(--accent-color);" onclick="openChat()">HỎI AI NGAY</button>
                </div>
            </div>
        </div>

    </div>

    <jsp:include page="footer.jsp" />
    
    <script>
        function openChat() {
            const val = document.getElementById('inline-ai-msg').value;
            const box = document.getElementById('ai-chat-box');
            if (box) { 
                box.style.display = 'flex'; 
                if(val.trim()){ 
                    document.getElementById('user-msg-input').value = val; 
                    if(typeof sendChatMessage === "function") sendChatMessage(); 
                } 
            }
        }
        
        // 🛑 CODE AJAX THÊM VÀO GIỎ HÀNG THẦN THÁNH NẰM Ở ĐÂY SẾP NHÉ
        function addToCartAsync(event, bookId) {
            if(event) event.preventDefault(); 

            // ĐÃ ĐỔI SANG ADD-TO-CART KÈM AJAX=TRUE
            fetch('add-to-cart?id=' + bookId + '&ajax=true')
                .then(response => response.json()) // Nhận về JSON thay vì text
                .then(data => {
                    const cartBadge = document.getElementById('cart-badge');
                    if (cartBadge) cartBadge.innerText = data.cartSize; 

                    Swal.fire({
                        icon: 'success',
                        title: 'Đã thêm vào giỏ!',
                        text: 'Sách ' + data.bookName + ' đã sẵn sàng trong giỏ nhé Hải.',
                        showConfirmButton: false,
                        timer: 1500,
                        toast: true,
                        position: 'top-end'
                    });
                })
                .catch(error => console.error('Lỗi AJAX:', error));
        }
    </script>
</body>
</html>