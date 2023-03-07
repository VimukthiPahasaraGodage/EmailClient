package org.uom.ginigalgodagevimukthipahasara.emailclient;

class OfficialRecipientCreator extends RecipientCreator {

    @Override
    Recipient createRecipient() {
        return new OfficialRecipient();
    }
}
