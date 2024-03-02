package com.esprit.models;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;

public class StripeFilm {
    public static void main(String[] args) {
// Set your secret key here
        Stripe.apiKey = "[REDACTED]";

        try {
// Retrieve your account information
            Account account = Account.retrieve();
            System.out.println("Account ID: " + account.getId());
// Print other account information as needed
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}
