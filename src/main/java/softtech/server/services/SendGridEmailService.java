package softtech.server.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("SendGrid API key not configured. Mock sending OTP {} to {}", otp, toEmail);
        return true; // For development
    }
}