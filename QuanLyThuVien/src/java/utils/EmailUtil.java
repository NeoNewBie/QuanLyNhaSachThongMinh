package utils;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {
    private static final Properties config = new Properties();

    static {
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                System.out.println("❌ Không tìm thấy file email.properties rồi sếp ơi!");
            } else {
                config.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String to, String subject, String content) {
        final String from = config.getProperty("email.username");
        final String password = config.getProperty("email.password");

        // ... Toàn bộ phần Properties SMTP và Transport.send(msg) bên dưới giữ nguyên ...
    
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Thêm dòng này cho chắc cú

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.setFrom(new InternetAddress(from, "SMART LIBRARY ADMIN"));
            msg.setReplyTo(InternetAddress.parse(from, false));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(content, "text/html; charset=utf-8");
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            
            Transport.send(msg);
            System.out.println("✅ MAIL ĐÃ BAY ĐI THÀNH CÔNG!");
        } catch (Exception e) {
            System.out.println("❌ LỖI GỬI MAIL RỒI SẾP HẢI ƠI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}