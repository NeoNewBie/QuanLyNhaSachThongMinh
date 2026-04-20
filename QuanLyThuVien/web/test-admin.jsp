<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
    <h2>GIẢ LẬP DUYỆT ĐƠN (ADMIN)</h2>
    <form action="manage-order" method="POST">
        Mã phiếu mượn (ID): <input type="text" name="ticketId" value="1"><br><br>
        Trạng thái: 
        <select name="status">
            <option value="0">Chờ duyệt</option>
            <option value="1">Đang chuẩn bị sách</option>
            <option value="2">SẴN SÀNG NHẬN (Sẽ gửi mail)</option>
            <option value="3">Đã lấy sách</option>
        </select><br><br>
        <button type="submit">CẬP NHẬT TRẠNG THÁI</button>
    </form>
</body>
</html>