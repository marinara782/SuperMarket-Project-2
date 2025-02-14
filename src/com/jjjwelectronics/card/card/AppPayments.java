package com.jjjwelectronics.card.card;

import java.io.IOException;
import java.util.Random;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents different payment accounts that are accepted (Google, Apple, or PayPal)
 *
 * @author JJJW Electronics LLP
 */
public final class AppPayments {
    // The type of payment account that is being used (Google, Apple, or PayPal)
    public final String accountType;

    // Name of the account holder
    public final String accountName;

    // The account password to login
    public final String password;

    /**
     * Some cards support the "tap" action; others not.
     */
    public final boolean hasFaceID;
    /**
     * Some cards possess an RFID chip; others not.
     */
    private int failedAttempts = 0;
    private boolean isBlocked;

    /**
     * Create a card instance.
     *
     * @param type
     *            The type of the account.
     * @param password
     *            The password of the account. This has to be a string of digits.
     * @param accountName
     *            The name of the accountName.
     * @throws SimulationException
     *             If type, password, or accountName is null.
     * @throws SimulationException
     *             If hasChip is true but pin is null.
     */
    public AppPayments(String type, String password, String accountName, boolean hasFaceID) {
        if(type == null)
            throw new NullPointerSimulationException("type");

        if(password == null)
            throw new NullPointerSimulationException("password");

        if(accountName == null)
            throw new NullPointerSimulationException("accountName");

        this.accountType = type;
        this.password = password;
        this.accountName = accountName;
        this.hasFaceID = hasFaceID;
    }

    private static final Random random = new Random(0);
    private static final double PROBABILITY_OF_TAP_FAILURE = 0.005;
    private static final double PROBABILITY_OF_NETWORK_ERROR = 0.00001;

    private String randomize(String original, double probability) {
        if(random.nextDouble() <= probability) {
            int length = original.length();
            int index = random.nextInt(length);
            String first;

            if(index == 0)
                first = "";
            else
                first = original.substring(0, index);

            char second = original.charAt(index);
            second++;

            String third;

            if(index == length - 1)
                third = "";
            else
                third = original.substring(index + 1, length);

            return first + second + third;
        }

        return original;
    }


    /**
     * Simulates the action of tapping phone with payment account.
     *
     * @return The card data.
     * @throws IOException
     *             If anything went wrong with the data transfer.
     */
    public final synchronized PaymentTapData tap(String pass) throws IOException {
        if(isBlocked)
            throw new BlockedCardException();

        if(hasFaceID) {
            if(random.nextDouble() <= PROBABILITY_OF_TAP_FAILURE)
                throw new ChipFailureException();

            return new PaymentTapData(pass);
        }

        return null;
    }

    /**
     * The abstract base type of card data.
     */
    public interface PaymentData {
        /**
         * Gets the type of the card.
         *
         * @return The type of the card.
         */
        public String getAccountType();

        /**
         * Gets the password of the card.
         *
         * @return The password of the card.
         */
        public String getPassword();

        /**
         * Gets the accountName's name.
         *
         * @return The accountName's name.
         */
        public String getAccountName();
    }

    /**
     * The data from tapping the payment method
     */
    public final class PaymentTapData implements PaymentData {
        PaymentTapData(String pin) throws InvalidPINException {
            if(testPassword(pin))
                throw new InvalidPINException();
        }
        @Override
        public String getAccountType() {
            return randomize(accountType, PROBABILITY_OF_NETWORK_ERROR);
        }

        @Override
        public String getPassword() {
            return randomize(password, PROBABILITY_OF_NETWORK_ERROR);
        }

        @Override
        public String getAccountName() {
            return randomize(accountName, PROBABILITY_OF_NETWORK_ERROR);
        }

        // tests the user's password input to ensure the password entered is correct
        private boolean testPassword(String userPass) {
            if(userPass.equals(password)) {
                failedAttempts = 0;
                return true;
            }
            // blocks access if there are more than 3 failed attempts
            if(++failedAttempts >= 3)
                isBlocked = true;
            return false;
        }
    }
}
