package org.uom.ginigalgodagevimukthipahasara.emailclient;

interface BridgeToEmailManager {

    void sendEmail(String recipientEmail, String subject, String content);

    boolean isValidEmailAddress(String email);
}
