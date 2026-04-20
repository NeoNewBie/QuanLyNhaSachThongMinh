package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import utils.DBContext;

public class CategoryDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String query = "SELECT * FROM Categories"; // Hải check xem DB có bảng này chưa nhé
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("CategoryID"),   // Check đúng tên cột trong SQL nhé
                    rs.getString("CategoryName")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
}