package org.uom.ginigalgodagevimukthipahasara.emailclient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class RecipientManager {

    private final String preferenceKey = "lastBirthdayGreetingsSentDate";
    private final Preferences preferences;
    private final ItemCollection<Recipient> recipientCollection;
    private final Iterator<Recipient> recipientIterator;
    private final BridgeToEmailManager bridgeToEmailManager;
    private final OfficialRecipientCreator officialRecipientCreator;
    private final PersonalRecipientCreator personalRecipientCreator;
    private final String emailClientUserName;
    private final String currentDate;
    private boolean areBirthdayGreetingsSentToday = false;
    private ArrayList<Recipient> recipientsCelebratingBirthdayToday;

    public RecipientManager(String emailClientUserName, BridgeToEmailManager bridgeToEmailManager) {
        this.bridgeToEmailManager = bridgeToEmailManager;
        this.emailClientUserName = emailClientUserName;
        officialRecipientCreator = new OfficialRecipientCreator();
        personalRecipientCreator = new PersonalRecipientCreator();
        recipientCollection = new ItemCollection<>();
        recipientIterator = recipientCollection.createIterator();
        currentDate = DateTimeFormatter.ofPattern("MM/dd").format(LocalDateTime.now());

        // get the last date birthday greetings had been sent from preferences and compare that date with current date
        preferences = Preferences.userNodeForPackage(RecipientManager.class);
        String birthdayGreetingsSentDate = preferences.get(preferenceKey, null);
        if (birthdayGreetingsSentDate != null && birthdayGreetingsSentDate.equals(currentDate)) {
            areBirthdayGreetingsSentToday = true;
        }
    }

    /*
     * Create a recipient object and add the recipient to recipient collection.
     * If the checkBirthday is true, send a birthday greeting email to recipient if he\she is celebrating birthday today.
     * This function assumes that no duplicate recipients are going to be added.
     */
    public void addRecipient(boolean checkBirthday, String[] recipientInfo) {
        Recipient recipient;
        boolean isOfficialRecipient = bridgeToEmailManager.isValidEmailAddress(recipientInfo[1]);
        if (isOfficialRecipient) {
            recipient = officialRecipientCreator.addRecipientInfo(recipientInfo);
        } else {
            recipient = personalRecipientCreator.addRecipientInfo(recipientInfo);
        }
        recipientCollection.addItem(recipient);
        if (checkBirthday) {
            String birthday = recipient.getBirthday();
            if (birthday != null) {
                if (birthday.substring(5).equals(currentDate)) {
                    recipientsCelebratingBirthdayToday.add(recipient);
                    sendBirthdayGreetingEmail(recipient);
                }
            }
        }
    }

    public int getNumberOfRecipients() {
        return recipientCollection.getNumberOfItems();
    }

    /*
     * Send an email with specified subject and content if there is a recipient with specified recipient email address
     */
    public void sendEmailToRecipient(String email, String subject, String content) {
        recipientIterator.reset();
        while (recipientIterator.hasNext()) {
            Recipient recipient = recipientIterator.next();
            if (recipient.getEmail().equals(email)) {
                if (recipient.isOfficialRecipient()) {
                    bridgeToEmailManager.sendEmail(recipient.getEmail(), subject, String.format("Dear %s,\n%s\n\nBest Regards,\n%s.", recipient.getName(), content, emailClientUserName));
                } else {
                    bridgeToEmailManager.sendEmail(recipient.getEmail(), subject, String.format("Dear %s,\n%s\n\nBest Regards,\n%s.", recipient.getNickname(), content, emailClientUserName));
                }
                return;
            }
        }
        System.out.printf("Couldn't find a recipient with the email address: %s.%n", email);
    }

    /*
     * Filter recipient collection for recipients having birthday on specified date.
     * Print the name and email address of the filtered recipients.
     */
    public void printRecipientsWithBirthdayToDate(String date) {
        ArrayList<Recipient> recipientsWithBirthdayToDate = getRecipientsWithBirthdayToDate(true, date);
        if (recipientsWithBirthdayToDate.size() > 0) {
            int index = 1;
            for (Recipient recipient : recipientsWithBirthdayToDate) {
                System.out.printf("%5d) %s, %s%n", index, recipient.getName(), recipient.getEmail());
                index++;
            }
        } else {
            System.out.printf("There are no recipients who has birthday on %s.%n", date);
        }
    }

    /*
     * Send birthday greeting email to each recipient celebrating his/her birthday today
     * If birthday greetings have been sent earlier this day, do nothing.
     */
    public void sendBirthdayGreetingsToRecipients() {
        recipientsCelebratingBirthdayToday = getRecipientsWithBirthdayToDate(false, currentDate);
        if (!areBirthdayGreetingsSentToday) {
            if (recipientsCelebratingBirthdayToday.size() > 0) {
                for (Recipient recipient : recipientsCelebratingBirthdayToday) {
                    sendBirthdayGreetingEmail(recipient);
                }
            } else {
                System.out.println("No recipients celebrating birthday today.");
            }
            preferences.put(preferenceKey, currentDate);
        } else {
            System.out.println("Birthday greetings have been sent to all recipients celebrating birthday today.");
        }
    }

    /*
     * Filter the recipient collection for recipients having birthday on specified date.
     * If includeYear is true, filtering is done for the date including year.(Eg: recipients born on 1987/08/04)
     * If includeYear is false, filtering is done for the month and date only.(Eg: recipients born on 08/04)
     */
    private ArrayList<Recipient> getRecipientsWithBirthdayToDate(boolean includeYear, String date) {
        ArrayList<Recipient> recipientsWithBirthday = new ArrayList<>();
        recipientIterator.reset();
        while (recipientIterator.hasNext()) {
            Recipient recipient = recipientIterator.next();
            String birthday = recipient.getBirthday();
            if (birthday != null) {
                if ((includeYear && birthday.equals(date)) || (!includeYear && birthday.substring(5).equals(date))) {
                    recipientsWithBirthday.add(recipient);
                }
            }
        }
        return recipientsWithBirthday;
    }

    private void sendBirthdayGreetingEmail(Recipient recipient) {
        System.out.printf("Sending a birthday greeting email to recipient email address: %s.%n", recipient.getEmail());
        if (recipient.isOfficialRecipient()) {
            bridgeToEmailManager.sendEmail(recipient.getEmail(), "Happy Birthday", String.format("Dear %s,\nWish you a happy birthday.\n\nBest Regards,\n%s.", recipient.getName(), emailClientUserName));
        } else {
            bridgeToEmailManager.sendEmail(recipient.getEmail(), "Happy Birthday", String.format("Dear %s,\nHugs and love on your birthday.\n\nBest Regards,\n%s.", recipient.getNickname(), emailClientUserName));
        }
    }
}
