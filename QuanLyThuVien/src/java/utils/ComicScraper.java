package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class ComicScraper {
    public static List<String> getImages(String chapterUrl) {
        List<String> images = new ArrayList<>();
        try {
            // Giả danh trình duyệt để trang gốc không chặn
            Document doc = Jsoup.connect(chapterUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .referrer("https://www.google.com/")
                    .timeout(10000)
                    .get();
            
            // Lọc lấy tất cả ảnh trong khung đọc truyện (thử nhiều class phổ biến)
            Elements imgElements = doc.select(".page-chapter img, .reading-detail img, .reading-content img");
            
            for (Element img : imgElements) {
                // Ưu tiên lấy data-original (link thật) trước, nếu không có mới lấy src
                String src = img.attr("data-original");
                if (src.isEmpty()) src = img.attr("data-src");
                if (src.isEmpty()) src = img.attr("src");
                
                if (src != null && !src.isEmpty()) {
                    if (src.startsWith("//")) src = "https:" + src;
                    images.add(src);
                }
            }
            System.out.println("🚀 [DEBUG] Đã cào được " + images.size() + " ảnh từ: " + chapterUrl);
        } catch (Exception e) {
            System.err.println("❌ Lỗi cào truyện: " + e.getMessage());
        }
        return images;
    }
}