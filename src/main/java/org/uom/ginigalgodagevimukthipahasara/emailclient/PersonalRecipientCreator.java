package org.uom.ginigalgodagevimukthipahasara.emailclient;

class PersonalRecipientCreator extends RecipientCreator {

    @Override
    Recipient createRecipient() {
        return new PersonalRecipient();
    }
}
