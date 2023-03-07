package org.uom.ginigalgodagevimukthipahasara.emailclient;

import java.io.Serializable;

public class Email implements Serializable {

    private final String senderEmail;
    private final String recipientEmail;
    private final String date;
    private final String subject;
    private final String content;

    public Email(String senderEmail, String recipientEmail, String date, String subject, String content) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.date = date;
        this.subject = subject;
        this.content = content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
