package org.uom.ginigalgodagevimukthipahasara.emailclient;

import com.google.code.samples.oauth2.OAuth2Authenticator;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

class GmailAPIsEmailSender {
    private final SMTPTransport smtpTransport;

    GmailAPIsEmailSender(String primaryEmailAddress) throws InitializationFailedException {
        OAuth2Authenticator oAuth2Authenticator = new OAuth2Authenticator();
        try {
            smtpTransport = oAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, primaryEmailAddress, oAuth2Authenticator.getAccessToken(), true);
        } catch (MessagingException | IOException e) {
            throw new InitializationFailedException("Initialization failed due to an error occurred while connecting to the SMTP server.");
        }
        System.out.println("Successfully authenticated to SMTP server.");
    }

    /*
     * Send the email to the recipient through Google smtp server
     */
    boolean sendEmail(Email email) {
        try {
            String recipientEmailAddress = email.getRecipientEmail();
            Address[] addresses = new Address[]{new InternetAddress(recipientEmailAddress)};
            smtpTransport.sendMessage(createMimeMessage(email), addresses);
            System.out.printf("Successfully sent the email to recipient email address: %s.%n", recipientEmailAddress);
            return true;
        } catch (MessagingException e) {
            System.out.printf("Failed sending the email to recipient email address: %s.%n", email.getRecipientEmail());
            return false;
        }
    }

    private Session getSession() {
        Properties prop = System.getProperties();
        prop.put("mail.smtps.host", "smtp.gmail.com");
        prop.put("mail.smtps.auth", "true");
        return Session.getInstance(prop);
    }

    private MimeMessage createMimeMessage(Email email) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(getSession());
        mimeMessage.setFrom(new InternetAddress(email.getSenderEmail()));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        mimeMessage.setSubject(email.getSubject());
        mimeMessage.setText(email.getContent());
        return mimeMessage;
    }
}
