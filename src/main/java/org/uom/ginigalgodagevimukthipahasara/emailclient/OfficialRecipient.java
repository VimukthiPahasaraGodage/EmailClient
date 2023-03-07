package org.uom.ginigalgodagevimukthipahasara.emailclient;

class OfficialRecipient implements Recipient {

    private String name;
    private String email;
    private String designation;
    private String birthday;

    public String getName() {
        return name;
    }

    @Override
    public String getNickname() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public String getDesignation() {
        return designation;
    }

    public boolean isOfficialRecipient() {
        return true;
    }

    public String getBirthday() {
        return birthday;
    }

    @Override
    public void addInfo(String[] info) {
        this.name = info[0];
        this.email = info[1];
        this.designation = info[2];
        if (info.length == 4) {
            this.birthday = info[3];
        }
    }
}
