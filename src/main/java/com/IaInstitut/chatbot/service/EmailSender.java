package com.IaInstitut.chatbot.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public class EmailSender {

    public static void sendEmailWithSendGrid(String toEmail, String subject, String bodyContent) throws Exception {
        String apiKey = "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"; // Replace with your actual SendGrid API key
        Email from = new Email("mohammed29addi@gmail.com"); // Replace with your SendGrid verified sender email
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
}
