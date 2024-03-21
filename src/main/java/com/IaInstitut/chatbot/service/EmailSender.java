package com.IaInstitut.chatbot.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailSender {

    public static void sendEmailWithSendGrid(String toEmail, String subject, String bodyContent) throws Exception {
        if (!isValidEmailAddress(toEmail)) {
            throw new IllegalArgumentException("Email address is invalid.");
        }
        String apiKey = "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"; // Replace with your actual SendGrid API key
        Email from = new Email("turbocouscous@gmail.com"); // Replace with your SendGrid verified sender email
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", bodyContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}