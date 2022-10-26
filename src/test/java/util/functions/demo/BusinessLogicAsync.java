package util.functions.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BusinessLogicAsync {


    private final String email;
    private final String postalAddress;
    private final String phoneNumber;


    public BusinessLogicAsync(String email, String postalAddress, String phoneNumber) {
        this.email = email;
        this.postalAddress = postalAddress;
        this.phoneNumber = phoneNumber;
    }

    public CompletableFuture<String> appendEmail(String payload) {

        return CompletableFuture.supplyAsync(() -> payload + " email: " + email);
    }

    public CompletableFuture<String> appendPostalAddress(String payload) {
        return CompletableFuture.supplyAsync(() -> payload + " postalAddress: " + postalAddress);
    }

    public CompletableFuture<String> appendPhoneNumber(String payload) {
        return CompletableFuture.supplyAsync(() -> payload + " phoneNumber: " + phoneNumber);
    }

}
