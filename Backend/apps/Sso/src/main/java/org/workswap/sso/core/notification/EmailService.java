package org.workswap.sso.core.notification;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Emailv31;

import com.mailjet.client.ClientOptions;

@Service
public class EmailService {

    private final MailjetClient client;
    private final String sender;

    public EmailService(
            @Value("${mailjet.api-key}") String apiKey,
            @Value("${mailjet.secret-key}") String secretKey,
            @Value("${mailjet.sender}") String sender
    ) {
        this.sender = sender;

        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(secretKey)
                .build();

        this.client = new MailjetClient(options);
    }

    public void sendEmail(String toEmail, String subject, String text) {
        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject().put("Email", sender))
                .put(Emailv31.Message.TO, new JSONArray()
                        .put(new JSONObject().put("Email", toEmail))
                )
                .put(Emailv31.Message.SUBJECT, subject)
                .put(Emailv31.Message.TEXTPART, text);

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        try {
            client.post(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendVerificationEmail(String email, String code) {
        
        String text = ""
        + "Your verification code: " + code + "\n\n"
        + "Enter this code on the website to complete the verification.\n"
        + "If you didn't request it, simply ignore this email.";

        String subject = "WorkSwap email verification";

        sendEmail(email, subject, text);
    }
}