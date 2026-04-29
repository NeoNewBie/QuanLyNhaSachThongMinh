package controller.SecondInteractionController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle; // 🛑 ĐÃ THÊM: Thư viện đọc file properties
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ChatbotServlet", urlPatterns = {"/chatbot"})
public class ChatbotServlet extends HttpServlet {

    // 🛑 HÀM MÓC API KEY TỪ KÉT SẮT (config.properties)
    private String getApiKey() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("config");
            return rb.getString("gemini.apikey");
        } catch (Exception e) {
            System.out.println("❌ LỖI: Không tìm thấy file config.properties hoặc chưa khai báo gemini.apikey!");
            return "";
        }
    }
    // 🛑 BẢO HIỂM CHỐNG LỖI 405: Khách gõ link bậy thì đá về trang chủ
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("home");
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        
        String userMessage = request.getParameter("message");
        PrintWriter out = response.getWriter();

        if (userMessage == null || userMessage.trim().isEmpty()) {
            out.print("Bạn muốn tôi tư vấn sách gì nào?");
            return;
        }

        // 🛑 KIỂM TRA CHÌA KHÓA TRƯỚC KHI GỌI API
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            out.print("Hệ thống AI chưa được cấp chìa khóa (API Key). Sếp kiểm tra lại file config nhé!");
            return;
        }
        
        // Link API của Google Gemini
        // 🛑 ĐÃ SỬA: Đổi tên Model thành 'gemini-1.5-flash-latest' cho chuẩn tài liệu mới nhất của Google
        // 🛑 ĐÃ ĐỔI SANG BẢN 'gemini-pro' CỰC KỲ ỔN ĐỊNH
// Đổi v1beta thành v1
// 🛑 ĐÃ ĐỔI: Dùng bản v1beta và model 'gemini-pro' (Bảo kê chạy 100%)
String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
        try {
            // 1. Tạo "Nhân cách" cho AI: Ép nó đóng vai thủ thư
            String systemInstruction = "Bạn là một nhân viên thủ thư nhiệt tình tại Smart Library (Đại học SPKT Đà Nẵng). " +
                    "Nhiệm vụ của bạn là tư vấn các đầu sách phù hợp với tâm trạng hoặc yêu cầu của sinh viên. " +
                    "Hãy trả lời ngắn gọn, thân thiện, có dùng emoji, độ dài không quá 4 câu.";
            
            String fullPrompt = systemInstruction + "\n\nKhách hàng hỏi: " + userMessage;

            // 2. Đóng gói câu hỏi thành định dạng JSON chuẩn của Google
            JsonObject content = new JsonObject();
            JsonObject parts = new JsonObject();
            parts.addProperty("text", fullPrompt);
            JsonArray partsArray = new JsonArray();
            partsArray.add(parts);
            content.add("parts", partsArray);
            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);
            JsonObject requestBodyJson = new JsonObject();
            requestBodyJson.add("contents", contentsArray);

            // 🛑 Chuẩn OkHttp 3: MediaType nằm trước
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                requestBodyJson.toString()
            );

            // 3. Bắn tín hiệu sang Server Google
            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            // 4. Nhận câu trả lời từ Google AI
            Response aiResponse = client.newCall(req).execute();
            String responseData = aiResponse.body().string();

            // 5. Bóc tách cục JSON khổng lồ để lấy đúng cái câu trả lời
            if (aiResponse.isSuccessful()) {
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                String aiText = jsonObject.getAsJsonArray("candidates")
                                        .get(0).getAsJsonObject()
                                        .getAsJsonObject("content")
                                        .getAsJsonArray("parts")
                                        .get(0).getAsJsonObject()
                                        .get("text").getAsString();
                
                out.print(aiText); // Gửi câu trả lời về màn hình chat của khách
            } else {
                out.print("Xin lỗi sếp, hệ thống AI đang nghẽn mạng xíu ạ!");
                System.out.println("Lỗi gọi API: " + responseData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("Dạ em là AI đang bị quá tải, sếp hỏi lại sau nha!");
        }
    }
}