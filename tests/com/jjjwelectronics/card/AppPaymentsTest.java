package com.jjjwelectronics.card;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.card.Card.CardTapData;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class AppPaymentsTest {
    private AppPayments Google, Apple;

    @Before
    public void setup() {
        Google = new AppPayments("GooglePay", "1234", "Bob", true);
        Apple = new AppPayments("ApplePay", "pass", "", false);
    }

    @Test(expected = SimulationException.class)
    public void testBadCreate() {
        new AppPayments(null, "", "", false);
    }

    @Test(expected = SimulationException.class)
    public void testBadCreate2() {
        new AppPayments("", null, "", false);
    }

    @Test(expected = SimulationException.class)
    public void testBadCreate3() {
        new AppPayments("", "", null, false);
    }

    @Test
    public void testTap() {
        boolean failure = false, success = false;

        for (int i = 0; i < 1000000; i++)
            try {
                success = (Google.tap("1234") != null);
            } catch (IOException e) {
                failure = true;
            }

        assertTrue(failure);
        assertTrue(success);
    }

    @Test
    public void testBadTap() throws IOException {
        assertTrue(Apple.tap("") == null);
    }

    @Test
    public void testTapNoFaceID() throws IOException {
        assertTrue(Apple.tap("pass") == null);
    }

    @Test
    public void testTapData() {
        int count = 0;
        AppPayments.PaymentTapData data = null;

        while (true) {
            while (true) {
                try {
                    data = Google.tap("1234");
                    if (data != null)
                        break;
                } catch (IOException e) {
                }
            }

            boolean differenceFound = false;
            differenceFound |= !data.getAccountType().equals("GooglePay");
            differenceFound |= !data.getPassword().equals("1234");
            differenceFound |= !data.getAccountName().equals("Bob");

            if (differenceFound) {
                System.out.println(count);
                return;
            }

            count++;
        }
    }
}