package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Item> items;

    public Cart() { 
        items = new ArrayList<>(); 
    }

    public List<Item> getItems() { 
        return items; 
    }

    // 🛑 FIX QUAN TRỌNG: Hàm tính lại toàn bộ tiền trong giỏ
    public double getTotalMoney() {
        double total = 0;
        for (Item i : items) {
            total += (i.getQuantity() * i.getPrice());
        }
        return total;
    }

    private Item getItemById(int id) {
        for (Item i : items) {
            if (i.getBook().getId() == id) return i;
        }
        return null;
    }

    public void updateQuantity(int id, int q) {
        Item existItem = getItemById(id);
        if (existItem != null) {
            existItem.setQuantity(q);
        }
    }

    public void addItem(Item newItem) {
        Item existItem = getItemById(newItem.getBook().getId());
        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    public void removeItem(int id) {
        Item item = getItemById(id);
        if (item != null) {
            items.remove(item);
        }
    }
}