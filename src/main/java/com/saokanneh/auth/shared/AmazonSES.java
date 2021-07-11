package com.saokanneh.auth.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.saokanneh.auth.shared.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class AmazonSES {

    final String FROM = "donotreply@saokanneh.com";
    final String SUBJECT = "Sao Kanneh - Verify Your Email";

    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thanks for registering with <a href=\"https://saokanneh.com\">saokanneh.com</a><p>"
            + "<p>You're nearly done! Please verify your email address by "
            +"<a href=\"https://localhost:3000/users/verify?token=$tokenValue\">clicking here</a> </p>";

    final String TEXTBODY = "Please verify your email address"
            + "Thanks for registering with https://saokanneh.com"
            + "You're nearly done! Please verify your email address by following this link: "
            +"https://localhost:3000/users/verify?token=$tokenValue";

    public void  verifyEmail(UserDto dto) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_2).build();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", dto.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", dto.getEmailVerificationToken());

        SendEmailRequest req = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(dto.getEmail()))
                .withMessage(new Message()
                .withBody(new Body()
                        .withHtml(new Content()
                                .withCharset("UTF-8")
                                .withData(htmlBodyWithToken))
                        .withText(new Content()
                                .withCharset("UTF-8")
                                .withData(textBodyWithToken)))
                .withSubject(new Content()
                        .withCharset("UTF-8")
                        .withData(SUBJECT)))

                .withSource(FROM);
        client.sendEmail(req);

        System.out.println("Verification Email Sent");
    }
}
