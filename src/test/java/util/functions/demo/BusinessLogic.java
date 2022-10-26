package util.functions.demo;

public class BusinessLogic {


    private final String email;
    private final String postalAddress;
    private final String phoneNumber;

    public BusinessLogic(String email, String postalAddress, String phoneNumber) {
        this.email = email;
        this.postalAddress = postalAddress;
        this.phoneNumber = phoneNumber;
    }

    public String appendEmail(String payload) {
        return payload + " email: " + email;
    }

    public String appendPostalAddress(String payload) {
        return payload + " postalAddress: " + postalAddress;
    }

    public String appendPhoneNumber(String payload) {
        return payload + " phoneNumber: " + phoneNumber;
    }
}
