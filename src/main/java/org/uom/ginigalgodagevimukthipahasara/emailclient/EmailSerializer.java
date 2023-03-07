package org.uom.ginigalgodagevimukthipahasara.emailclient;

import java.io.*;

class EmailSerializer {
    private final String savedEmailsDirectoryPath = "savedEmails/";

    /*
     * Save the email object in the savedEmails folder(create a new folder if not exists)
     * fileName: currentTimeInMilliseconds_recipientEmailAddress.ser
     */
    boolean serializeEmail(Email email) {
        File savedEmailsDirectory = new File(savedEmailsDirectoryPath);
        boolean isDirectoryExistsOrCreated = true;
        if (!savedEmailsDirectory.exists()) {
            isDirectoryExistsOrCreated = savedEmailsDirectory.mkdir();
        }
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream = null;
        if (isDirectoryExistsOrCreated) {
            String fileName = System.currentTimeMillis() + "_" + email.getRecipientEmail() + ".ser";
            try {
                fileOutputStream = new FileOutputStream(savedEmailsDirectoryPath + fileName);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(email);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException e) {
                        // Ignore this exception
                    }
                }
            }
        }
        return false;
    }

    /*
     * Convert all the saved email files in savedEmail folder into email objects
     * Add all the email objects to email collection in application
     */
    boolean deserializeEmail(ItemCollection<Email> emailCollection) {
        File file = new File(savedEmailsDirectoryPath);
        if (file.exists()) {
            File[] savedEmailObjectFiles = file.listFiles();
            FileInputStream fileInputStream;
            ObjectInputStream objectInputStream = null;
            if (savedEmailObjectFiles != null) {
                for (File emailObjectFile : savedEmailObjectFiles) {
                    try {
                        fileInputStream = new FileInputStream(savedEmailsDirectoryPath + emailObjectFile.getName());
                        objectInputStream = new ObjectInputStream(fileInputStream);
                        Email emailh = new Email("f", "f", "f", "f", "f");
                        Email email = (Email) objectInputStream.readObject();
                        emailCollection.addItem(email);
                    } catch (IOException | ClassNotFoundException e) {
                        return false;
                    } finally {
                        if (objectInputStream != null) {
                            try {
                                objectInputStream.close();
                            } catch (IOException e) {
                                // Ignore this exception
                            }
                            objectInputStream = null;
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }
}
