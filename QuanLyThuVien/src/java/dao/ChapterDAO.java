package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Chapter;
import utils.DBContext;

public class ChapterDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public List<Chapter> getChaptersByBookId(int bookId) {
        List<Chapter> list = new ArrayList<>();
        String query = "SELECT * FROM Chapters WHERE BookID = ? ORDER BY ChapterNumber ASC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Chapter c = new Chapter();
                // Nhớ đảm bảo trong file model.Chapter.java sếp đã tạo đủ Getter/Setter nhé
                c.setChapterNumber(rs.getInt("ChapterNumber"));
                c.setTitle(rs.getString("Title"));
                c.setContent(rs.getString("Content"));
                c.setIsFree(rs.getBoolean("IsFree"));
                list.add(c);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}