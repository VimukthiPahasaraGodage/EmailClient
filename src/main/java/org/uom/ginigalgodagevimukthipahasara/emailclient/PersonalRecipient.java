package org.uom.ginigalgodagevimukthipahasara.emailclient;

class PersonalRecipient implements Recipient {

    private String name;
    private String nickname;
    private String email;
    private String birthday;

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    @Override
    public String getDesignation() {
        return null;
    }

    @Override
    public boolean isOfficialRecipient() {
        return false;
    }

    public void addInfo(String[] info) {
        this.name = info[0];
        this.nickname = info[1];
        this.email = info[2];
        this.birthday = info[3];
    }
}
