package org.uom.ginigalgodagevimukthipahasara.emailclient;

abstract class RecipientCreator {

    abstract Recipient createRecipient();

    Recipient addRecipientInfo(String[] info) {
        Recipient recipient = createRecipient();
        recipient.addInfo(info);
        return recipient;
    }
}
