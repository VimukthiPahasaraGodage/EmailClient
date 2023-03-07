package org.uom.ginigalgodagevimukthipahasara.emailclient;

interface Recipient {

    void addInfo(String[] info);

    String getName();

    String getNickname();

    String getEmail();

    String getBirthday();

    String getDesignation();

    boolean isOfficialRecipient();
}
