package softtech.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class BrevoEmailService {
    private static final Logger logger = LoggerFactory.getLogger(BrevoEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${brevo.from.email:nhomweb11@gmail.com}")
    private String fromEmail;

    @Value("${brevo.from.name:Cinema Management System}")
    private String fromName;

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code - Cinema Management System");
            
            String htmlContent = buildOtpEmailContent(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("OTP email sent successfully via Brevo to: {}", toEmail);
            return true;

        } catch (MessagingException e) {
            logger.error("Error sending email via Brevo to {}: {}", toEmail, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error sending email via Brevo to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    public boolean sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Cinema Management System");
            
            String htmlContent = buildWelcomeEmailContent(userName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Welcome email sent successfully via Brevo to: {}", toEmail);
            return true;

        } catch (MessagingException e) {
            logger.error("Error sending welcome email via Brevo to {}: {}", toEmail, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error sending welcome email via Brevo to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private String buildOtpEmailContent(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>OTP Verification</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #2c3e50; text-align: center; padding: 20px; background-color: #ecf0f1; border-radius: 8px; margin: 20px 0; letter-spacing: 5px; }
                    .footer { text-align: center; margin-top: 30px; color: #7f8c8d; font-size: 14px; }
                    .brevo-badge { background: linear-gradient(45deg, #0052CC, #0080FF); color: white; padding: 5px 10px; border-radius: 5px; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎬 Cinema Management System</h1>
                        <h2>OTP Verification Code</h2>
                        <span class="brevo-badge">Powered by Brevo</span>
                    </div>
                    
                    <p>Xin chào,</p>
                    <p>Bạn đã yêu cầu mã OTP để xác thực. Vui lòng sử dụng mã dưới đây:</p>
                    
                    <div class="otp-code">""" + otp + """
                    </div>
                    
                    <p><strong>Lưu ý quan trọng:</strong></p>
                    <ul>
                        <li>Mã này chỉ có hiệu lực trong 5 phút</li>
                        <li>Không chia sẻ mã này với bất kỳ ai</li>
                        <li>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này</li>
                    </ul>
                    
                    <div class="footer">
                        <p>Trân trọng,<br>Đội ngũ Cinema Management</p>
                        <p>Đây là email tự động. Vui lòng không trả lời tin nhắn này.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private String buildWelcomeEmailContent(String userName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to Cinema Management System</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; background: linear-gradient(45deg, #FF6B6B, #4ECDC4); padding: 20px; border-radius: 10px; color: white; }
                    .content { margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #7f8c8d; font-size: 14px; }
                    .brevo-badge { background: linear-gradient(45deg, #0052CC, #0080FF); color: white; padding: 5px 10px; border-radius: 5px; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎬 Chào mừng đến với Cinema Management System!</h1>
                        <span class="brevo-badge">Powered by Brevo</span>
                    </div>
                    
                    <div class="content">
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
                    
                    <div class="footer">
                        <p>Trân trọng,<br>Đội ngũ Cinema Management</p>
                        <p>Đây là email tự động. Vui lòng không trả lời tin nhắn này.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}