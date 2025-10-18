package softtech.server.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class SendGridEmailService {
    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    @Value("${sendgrid.api.key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email:noreply@yourcinema.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:Cinema Management System}")
    private String fromName;

    public boolean sendOtpEmail(String toEmail, String otp) {
        if (sendGridApiKey.isEmpty()) {
            logger.info("SendGrid API key not configured. Mock sending OTP {} to {}", otp, toEmail);
            return true; // For development
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Your OTP Code - Cinema Management System";
            
            String htmlContent = buildOtpEmailContent(otp);
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("OTP email sent successfully to: {}", toEmail);
                return true;
            } else {
                logger.error("Failed to send email. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                return false;
            }
            
        } catch (IOException e) {
            logger.error("Error sending email to {}: {}", toEmail, e.getMessage());
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
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎬 Cinema Management System</h1>
                        <h2>OTP Verification Code</h2>
                    </div>
                    
                    <p>Hello,</p>
                    <p>You have requested an OTP code for verification. Please use the code below:</p>
                    
                    <div class="otp-code">""" + otp + """
                    </div>
                    
                    <p><strong>Important:</strong></p>
                    <ul>
                        <li>This code is valid for 5 minutes only</li>
                        <li>Do not share this code with anyone</li>
                        <li>If you didn't request this code, please ignore this email</li>
                    </ul>
                    
                    <div class="footer">
                        <p>Best regards,<br>Cinema Management Team</p>
                        <p>This is an automated email. Please do not reply to this message.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}