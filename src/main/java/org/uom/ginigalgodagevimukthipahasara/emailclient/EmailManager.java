package org.uom.ginigalgodagevimukthipahasara.emailclient;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailManager implements BridgeToEmailManager {

    private final ItemCollection<Email> emailCollection;
    private final Iterator<Email> emailIterator;
    private final EmailSerializer emailSerializer;
    private final GmailAPIsEmailSender gmailAPIsEmailSender;
    private final String primaryEmailAddress;

    public EmailManager(String primaryEmailAddress) throws InitializationFailedException {
        this.primaryEmailAddress = primaryEmailAddress;
        gmailAPIsEmailSender = new GmailAPIsEmailSender(primaryEmailAddress);
        emailSerializer = new EmailSerializer();
        emailCollection = new ItemCollection<>();
        emailIterator = emailCollection.createIterator();
        boolean deserializationSuccessful = emailSerializer.deserializeEmail(emailCollection);
        if (!deserializationSuccessful) {
            throw new InitializationFailedException("Initialization failed due to an error occurred while retrieving the saved emails.");
        }
    }

    /*
     * Send an email with specified subject and content to the recipient email address.
     * If the email sent status is successful, then save the email in the disk.
     * If the email save status is successful, then add the email to email collection in application.
     */
    public void sendEmail(String recipientEmail, String subject, String content) {
        Email email = new Email(primaryEmailAddress, recipientEmail, DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()), subject, content);
        if (gmailAPIsEmailSender.sendEmail(email)) {
            if (emailSerializer.serializeEmail(email)) {
                emailCollection.addItem(email);
            }
        }
    }

    /*
     * Filter out the emails sent on the specified date from email collection.
     * Print the details of the filtered emails.
     */
    public void printEmailsSentOnDate(String date) {
        boolean isEmpty = true;
        emailIterator.reset();
        while (emailIterator.hasNext()) {
            Email email = emailIterator.next();
            if (email.getDate().equals(date)) {
                isEmpty = false;
                String detailsOfEmail = String.format("""
                        ================================================================================================
                        From: %s
                        To: %s
                        Date: %s
                        Subject: %s
                        %s
                        """, email.getSenderEmail(), email.getRecipientEmail(), email.getDate(), email.getSubject(), email.getContent());
                System.out.println(detailsOfEmail);
            }
        }
        if (isEmpty) {
            System.out.printf("There are no emails sent on %s.%n", date);
        }
    }

    /*
     * Return true if the specified string is a valid email address.
     * Return false otherwise.
     */
    public boolean isValidEmailAddress(String email) {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}
