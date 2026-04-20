package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
   private List<Item> items;

    public Cart() { items = new ArrayList<>(); }

    public List<Item> getItems() { return items; }

    // Hàm phụ: Tìm xem một cuốn sách đã nằm trong giỏ chưa dựa vào ID
   private Item getItemById(int id) {
        for (Item i : items) {
            if (i.getBook().getId() == id) return i;
        }
        return null;
    }

    // 🛑 HÀM MỚI: Cập nhật số lượng (Dùng để fix lỗi 400)
    public void updateQuantity(int id, int q) {
        Item existItem = getItemById(id);
        if (existItem != null) {
            existItem.setQuantity(q);
        }
    }

    // THAO TÁC 1: THÊM VÀO GIỎ HÀNG
    public void addItem(Item newItem) {
        Item existItem = getItemById(newItem.getBook().getId());
        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    // THAO TÁC 2: XÓA KHỎI GIỎ HÀNG
    public void removeItem(int id) {
        Item existItem = getItemById(id);
        if (existItem != null) items.remove(existItem);
    }

    // THAO TÁC 3: TÍNH TỔNG TIỀN CỦA CẢ GIỎ
    public double getTotalMoney() {
        double total = 0;
        for (Item i : items) {
            total += i.getQuantity() * i.getBook().getPrice();
        }
        return total;
    }
}