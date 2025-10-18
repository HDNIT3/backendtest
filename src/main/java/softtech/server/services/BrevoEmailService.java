package softtech.server.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class BrevoEmailService {
    private static final Logger logger = LoggerFactory.getLogger(BrevoEmailService.class);
    
    private final HttpClient httpClient;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email:nhomweb11@gmail.com}")
    private String fromEmail;

    @Value("${brevo.from.name:Cinema Management System}")
    private String fromName;

    public BrevoEmailService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            logger.info("Sending OTP email via Brevo API to: {}", toEmail);
            logger.debug("Using from email: {}, from name: {}", fromEmail, fromName);
            logger.debug("API Key present: {}", apiKey != null && !apiKey.isEmpty() && !apiKey.equals("${BREVO_API_KEY}"));
            
            // Kiểm tra API key
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${BREVO_API_KEY}") || apiKey.equals("YOUR_ACTUAL_BREVO_API_KEY")) {
                logger.error("Brevo API key is not configured properly. Current value: {}", apiKey);
                return false;
            }

            String requestBody = """
                {
                    "sender": {
                        "name": "%s",
                        "email": "%s"
                    },
                    "to": [
                        {
                            "email": "%s"
                        }
                    ],
                    "subject": "Your OTP Code - Cinema Management System",
                    "htmlContent": "%s"
                }
                """.formatted(fromName, fromEmail, toEmail, buildOtpEmailContent(otp));

            logger.debug("Request body: {}", requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.debug("Brevo API response status: {}", response.statusCode());
            logger.debug("Brevo API response body: {}", response.body());

            if (response.statusCode() == 201 || response.statusCode() == 202) {
                logger.info("OTP email sent successfully via Brevo API to: {}", toEmail);
                return true;
            } else {
                logger.error("Brevo API error: Status {}, Body: {}", response.statusCode(), response.body());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending OTP email via Brevo API to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendWelcomeEmail(String toEmail, String userName) {
        try {
            logger.info("Sending welcome email via Brevo API to: {}", toEmail);
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${BREVO_API_KEY}") || apiKey.equals("YOUR_ACTUAL_BREVO_API_KEY")) {
                logger.error("Brevo API key is not configured properly");
                return false;
            }

            String requestBody = """
                {
                    "sender": {
                        "name": "%s",
                        "email": "%s"
                    },
                    "to": [
                        {
                            "email": "%s"
                        }
                    ],
                    "subject": "Welcome to Cinema Management System",
                    "htmlContent": "%s"
                }
                """.formatted(fromName, fromEmail, toEmail, buildWelcomeEmailContent(userName));

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.debug("Welcome email response status: {}", response.statusCode());
            
            if (response.statusCode() == 201 || response.statusCode() == 202) {
                logger.info("Welcome email sent successfully via Brevo API to: {}", toEmail);
                return true;
            } else {
                logger.error("Brevo API error for welcome email: Status {}, Body: {}", response.statusCode(), response.body());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending welcome email via Brevo API to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    private String buildOtpEmailContent(String otp) {
        // Escape quotes for JSON
        return """
            <div style=\\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;\\">
                <div style=\\"background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\\">
                    <div style=\\"text-align: center; margin-bottom: 30px;\\">
                        <h1 style=\\"color: #2c3e50; margin: 0;\\">🎬 Cinema Management System</h1>
                        <h2 style=\\"color: #34495e; margin: 10px 0;\\">OTP Verification Code</h2>
                        <span style=\\"background: linear-gradient(45deg, #0052CC, #0080FF); color: white; padding: 5px 10px; border-radius: 5px; font-size: 12px;\\">Powered by Brevo API</span>
                    </div>
                    
                    <p>Xin chào,</p>
                    <p>Bạn đã yêu cầu mã OTP để xác thực. Vui lòng sử dụng mã dưới đây:</p>
                    
                    <div style=\\"text-align: center; margin: 20px 0;\\">
                        <span style=\\"font-size: 32px; font-weight: bold; color: #2c3e50; background-color: #ecf0f1; padding: 20px; border-radius: 8px; letter-spacing: 5px; display: inline-block;\\">
                            """ + otp + """
                        </span>
                    </div>
                    
                    <div style=\\"background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;\\">
                        <p style=\\"margin: 0; font-weight: bold; color: #856404;\\">Lưu ý quan trọng:</p>
                        <ul style=\\"margin: 10px 0; color: #856404;\\">
                            <li>Mã này chỉ có hiệu lực trong 5 phút</li>
                            <li>Không chia sẻ mã này với bất kỳ ai</li>
                            <li>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này</li>
                        </ul>
                    </div>
                    
                    <div style=\\"text-align: center; margin-top: 30px; color: #7f8c8d; font-size: 14px;\\">
                        <p>Trân trọng,<br><strong>Đội ngũ Cinema Management</strong></p>
                        <p style=\\"font-style: italic;\\">Đây là email tự động. Vui lòng không trả lời tin nhắn này.</p>
                    </div>
                </div>
            </div>
            """.replaceAll("\\n", "").replaceAll("\\s+", " ");
    }

    private String buildWelcomeEmailContent(String userName) {
        return """
            <div style=\\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;\\">
                <div style=\\"background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\\">
                    <div style=\\"text-align: center; margin-bottom: 30px; background: linear-gradient(45deg, #FF6B6B, #4ECDC4); padding: 20px; border-radius: 10px; color: white;\\">
                        <h1 style=\\"margin: 0;\\">🎬 Chào mừng đến với Cinema Management System!</h1>
                        <span style=\\"background: rgba(255,255,255,0.2); color: white; padding: 5px 10px; border-radius: 5px; font-size: 12px;\\">Powered by Brevo API</span>
                    </div>
                    
                    <div style=\\"margin: 20px 0;\\">
                        <p>Xin chào <strong>""" + userName + """
                        </strong>,</p>
                        <p>Chào mừng bạn đến với hệ thống quản lý rạp chiếu phim của chúng tôi!</p>
                        
                        <p>Tài khoản của bạn đã được tạo thành công. Bây giờ bạn có thể:</p>
                        <ul>
                            <li>🎫 Đặt vé xem phim</li>
                            <li>🍿 Quản lý thông tin cá nhân</li>
                            <li>📱 Nhận thông báo về phim mới</li>
                            <li>⭐ Đánh giá và bình luận phim</li>
                        </ul>
                        
                        <p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi!</p>
                    </div>
                    
                    <div style=\\"text-align: center; margin-top: 30px; color: #7f8c8d; font-size: 14px;\\">
                        <p>Trân trọng,<br><strong>Đội ngũ Cinema Management</strong></p>
                        <p style=\\"font-style: italic;\\">Đây là email tự động. Vui lòng không trả lời tin nhắn này.</p>
                    </div>
                </div>
            </div>
            """.replaceAll("\\n", "").replaceAll("\\s+", " ");
    }
}
