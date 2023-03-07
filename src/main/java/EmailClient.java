//Ginigal Godage Vimukthi Pahasara

//import libraries

import org.uom.ginigalgodagevimukthipahasara.emailclient.EmailManager;
import org.uom.ginigalgodagevimukthipahasara.emailclient.InitializationFailedException;
import org.uom.ginigalgodagevimukthipahasara.emailclient.RecipientManager;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EmailClient {
    private EmailManager emailManager;
    private RecipientManager recipientManager;
    private BufferedReader recipientListReader;
    private BufferedWriter recipientListWriter;

    public static void main(String[] args) throws IOException {
        EmailClient emailClient = new EmailClient();
        emailClient.printToConsole("Initializing Email Client.");
        try {
            // must call initialize immediately after creating an instance of EmailClient
            emailClient.initialize();
            // initialization is successful
            emailClient.printToConsole("Initialization successful.\n\nSending birthday greetings to recipients celebrating birthday today.");
            emailClient.sendBirthdayGreetingsToRecipientsCelebratingBirthdayToday();

            Scanner scanner = new Scanner(System.in);
            emailClient.printToConsole("""
                    \nEnter option type:
                    1 - Adding a new recipient
                    2 - Sending an email
                    3 - Printing out all the recipients who have birthdays
                    4 - Printing out details of all the emails sent
                    5 - Printing out the number of recipient objects in the application
                    Any other number - Exit the Email Client""");

            while (true) {
                System.out.print("\nEnter option: ");
                int option;
                try {
                    // get the option user selected
                    option = scanner.nextInt();
                } catch (InputMismatchException e) {
                    emailClient.printToConsole("Please enter a valid option when prompted.");
                    scanner.nextLine();
                    continue;
                }
                // consume the trailing new line remain after getting the integer
                scanner.nextLine();

                switch (option) {
                    case 1 ->
                        // input format - Official: nimal,nimal@gmail.com,ceo
                        // Use a single input to get all the details of a recipient
                        // code to add a new recipient
                        // store details in clientList.txt file
                        // Hint: use methods for reading and writing files
                            emailClient.addNewRecipient(scanner);
                    case 2 ->
                        // input format - email, subject, content
                        // code to send an email
                            emailClient.sendAnEmailToRecipient(scanner);
                    case 3 ->
                        // input format - yyyy/MM/dd (ex: 2018/09/17)
                        // code to print recipients who have birthdays on the given date
                            emailClient.printRecipientsWithBirthdayToDate(scanner);
                    case 4 ->
                        // input format - yyyy/MM/dd (ex: 2018/09/17)
                        // code to print the details of all the emails sent on the input date
                            emailClient.printEmailsSentOnDate(scanner);
                    case 5 ->
                        // code to print the number of recipient objects in the application
                            emailClient.printNumberOfRecipients();
                    default -> emailClient.exit();
                }
            }
        } catch (InitializationFailedException e) {
            emailClient.printToConsole(e.getMessage());
            emailClient.exit();
        }
    }

    /*
     * Initialize the required components for the email client
     * Mut call initialize() immediately after creating an instance of EmailClient
     */
    private void initialize() throws InitializationFailedException {
        // recipientListReader is not closed because the recipient list reader is needed for the entire time the program runs
        try {
            String PRIMARY_EMAIL_ADDRESS = "emailclientassignment200440c@gmail.com";
            emailManager = new EmailManager(PRIMARY_EMAIL_ADDRESS);
            String EMAIL_CLIENT_USER_NAME = "G. G. Vimukthi Pahasara";
            recipientManager = new RecipientManager(EMAIL_CLIENT_USER_NAME, emailManager);
            String CLIENT_LIST_FILE_NAME = "clientList.txt";
            File recipientListFile = new File(CLIENT_LIST_FILE_NAME);
            // create the clientList.txt file if it doesn't exist
            recipientListFile.createNewFile();
            recipientListReader = new BufferedReader(new FileReader(recipientListFile));
            recipientListWriter = new BufferedWriter(new FileWriter(recipientListFile, true));
            updateRecipientsCollection();
        } catch (IOException e) {
            throw new InitializationFailedException("Initialization failed due to an error occurred while reading the client list.");
        }
    }

    /*
     * Read the client list file and create recipient objects and add them to recipient collection
     * If checkBirthdays true, after adding recipient to the recipient collection, birthday greeting will be sent if he/she celebrating birthday today
     */
    private void updateRecipientsCollection() throws IOException {
        String currentRecipientInfo;
        while ((currentRecipientInfo = recipientListReader.readLine()) != null) {
            String[] splitCurrentRecipientInfo = currentRecipientInfo.split(",");
            // remove any white spaces in the beginning and ending of the string
            for (int i = 0; i < splitCurrentRecipientInfo.length; i++) {
                splitCurrentRecipientInfo[i] = trim(splitCurrentRecipientInfo[i]);
            }
            recipientManager.addRecipient(false, splitCurrentRecipientInfo);
        }
    }

    private void sendBirthdayGreetingsToRecipientsCelebratingBirthdayToday() {
        recipientManager.sendBirthdayGreetingsToRecipients();
    }

    private void exit() throws IOException {
        System.out.println("Email Client is terminated.");
        if (recipientListReader != null) {
            recipientListReader.close();
        }
        recipientListReader = null;
        if (recipientListWriter != null) {
            recipientListWriter.close();
        }
        recipientListWriter = null;
        System.exit(0);
    }

    private void addNewRecipient(Scanner scanner) {
        printToConsole("Input format for official recipient: name, email, designation, birthday(if the recipient is a friend)\nInput format for personal recipient: name, nickname, email, birthday");
        String[] splitRecipientInfo = scanner.nextLine().split(",");
        int splitRecipientInfoLength = splitRecipientInfo.length;
        // remove any white spaces in the beginning and ending of the string
        for (int i = 0; i < splitRecipientInfoLength; i++) {
            splitRecipientInfo[i] = trim(splitRecipientInfo[i]);
        }
        boolean isEmailValid, isBirthdayValid = false;
        if (splitRecipientInfoLength < 3 || splitRecipientInfoLength > 4) {
            printToConsole("Invalid number of comma separated inputs for adding a new recipient.");
            return;
        } else {
            String recipientEmailAddressIfOfficialRecipient = splitRecipientInfo[1];
            String recipientEmailAddressIfPersonalFriend = splitRecipientInfo[2];
            isEmailValid = emailManager.isValidEmailAddress(recipientEmailAddressIfOfficialRecipient) || emailManager.isValidEmailAddress(recipientEmailAddressIfPersonalFriend);
            if (splitRecipientInfoLength == 4) {
                isBirthdayValid = isValidDate(splitRecipientInfo[3]);
            }
        }
        String recipientInfoForSaving;
        if (splitRecipientInfoLength == 3) {
            if (isEmailValid) {
                recipientInfoForSaving = String.format("%s,%s,%s\n", splitRecipientInfo[0], splitRecipientInfo[1], splitRecipientInfo[2]);
            } else {
                printToConsole("Recipient email address has a wrong formatting.");
                return;
            }
        } else {
            if (isEmailValid && isBirthdayValid) {
                recipientInfoForSaving = String.format("%s,%s,%s,%s\n", splitRecipientInfo[0], splitRecipientInfo[1], splitRecipientInfo[2], splitRecipientInfo[3]);
            } else if (!isEmailValid && isBirthdayValid) {
                printToConsole("Recipient email address has a wrong formatting.");
                return;
            } else {
                printToConsole("Recipient birthday has a wrong formatting.");
                return;
            }
        }
        try {
            recipientListWriter.write(recipientInfoForSaving);
            recipientManager.addRecipient(true, splitRecipientInfo);
            printToConsole("Adding the new recipient is successful.");
        } catch (IOException e) {
            printToConsole("Unexpected IO error occurred while writing new recipient info to client list file.");
        }
    }

    private void sendAnEmailToRecipient(Scanner scanner) {
        printToConsole("Input format: email (email should belong to an existing client), subject, content");
        String[] splitInput = scanner.nextLine().split(",");
        if (splitInput.length == 3) {
            recipientManager.sendEmailToRecipient(trim(splitInput[0]), trim(splitInput[1]), trim(splitInput[2]));
        } else {
            printToConsole("Invalid number of comma separated inputs for sending an email to a recipient.");
        }
    }

    private void printRecipientsWithBirthdayToDate(Scanner scanner) {
        printToConsole("Input format: date(yyyy/MM/dd)");
        String birthday = trim(scanner.nextLine());
        if (isValidDate(birthday)) {
            recipientManager.printRecipientsWithBirthdayToDate(birthday);
        } else {
            printToConsole("Entered birthday has a wrong formatting.");
        }
    }

    private void printEmailsSentOnDate(Scanner scanner) {
        printToConsole("Input format: date(yyyy/MM/dd)");
        String date = trim(scanner.nextLine());
        if (isValidDate(date)) {
            emailManager.printEmailsSentOnDate(date);
        } else {
            printToConsole("Entered date has a wrong formatting.");
        }
    }

    private void printNumberOfRecipients() {
        printToConsole(String.valueOf(recipientManager.getNumberOfRecipients()));
    }

    private boolean isValidDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd").withResolverStyle(ResolverStyle.STRICT);
        try {
            dateTimeFormatter.parse(date);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private String trim(String string) {
        return string.trim();
    }

    private void printToConsole(String message) {
        System.out.println(message);
    }
}