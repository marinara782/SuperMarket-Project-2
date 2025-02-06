package com.thelocalmarketplace.hardware.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.external.CardIssuer;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class CardIssuerTest {
	private CardIssuer issuer;
	private GregorianCalendar tomorrow;

	@Before
	public void setup() {
		tomorrow = (GregorianCalendar)GregorianCalendar.getInstance();
		tomorrow.add(1, 1);
		issuer = new CardIssuer("", 10);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new CardIssuer(null, 1);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new CardIssuer("", 0);
	}

	@Test
	public void testAdd() {
		issuer.addCardData("1", "me", tomorrow, "111", 10000);
		assertTrue(issuer.block("1"));
		assertTrue(issuer.unblock("1"));
		assertFalse(issuer.block("0"));
		assertFalse(issuer.unblock("0"));
	}

	@Test(expected = SimulationException.class)
	public void testDoubleAdd() {
		issuer.addCardData("1", "me", tomorrow, "111", 10000);
		issuer.addCardData("1", "me", tomorrow, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd() {
		issuer.addCardData(null, "me", tomorrow, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd2() {
		issuer.addCardData("", "me", tomorrow, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd3() {
		issuer.addCardData("1", null, tomorrow, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd4() {
		issuer.addCardData("1", "", tomorrow, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd5() {
		issuer.addCardData("1", "me", null, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd6() {
		Calendar date = Calendar.getInstance();
		date.roll(Calendar.MONTH, false); // last month
		issuer.addCardData("1", "me", date, "111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd7() {
		issuer.addCardData("1", "me", tomorrow, "11", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd8() {
		issuer.addCardData("1", "me", tomorrow, "1111", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd9() {
		issuer.addCardData("1", "me", tomorrow, null, 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd9A() {
		issuer.addCardData("1", "me", tomorrow, "1a1", 10000);
	}

	@Test(expected = SimulationException.class)
	public void testBadAdd11() {
		issuer.addCardData("1", "me", tomorrow, "111", 0L);
	}

	@Test
	public void testPurchase() {
		issuer.addCardData("1", "me", tomorrow, "111", 10L);
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(holdNumber >= 0);
		assertTrue(issuer.postTransaction("1", holdNumber, 5));
	}

	@Test
	public void testPurchase2() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(holdNumber >= 0);
		assertTrue(issuer.postTransaction("1", holdNumber, 5));
		long holdNumber2 = issuer.authorizeHold("1", 4.5);
		assertTrue(holdNumber2 >= 0);
		assertTrue(issuer.postTransaction("1", holdNumber2, 4));
	}

	@Test
	public void testBadPurchase() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(holdNumber >= 0);
		assertFalse(issuer.postTransaction("1", holdNumber, 6));
	}

	@Test
	public void testBlock() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		assertTrue(issuer.block("1"));
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(holdNumber == -1);
	}

	@Test
	public void testBlock2() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(issuer.block("1"));
		assertFalse(issuer.postTransaction("1", holdNumber, 5));
	}

	@Test
	public void testMultipleBlocks() {
		issuer.addCardData("1", "me", tomorrow, "111", 100000);
		ArrayList<Long> holdNumbers = new ArrayList<>();
		for(long i = 0; i < 5; i++) {
			long holdNumber = issuer.authorizeHold("1", 1.0);
			holdNumbers.add(holdNumber);
		}

		Set<Long> uniqueHoldNumbers = new HashSet<>();
		uniqueHoldNumbers.addAll(holdNumbers);

		assertEquals(holdNumbers.size(), uniqueHoldNumbers.size());
	}

	@Test
	public void testTooManyBlocks() {
		issuer.addCardData("1", "me", tomorrow, "111", 100000);
		ArrayList<Long> holdNumbers = new ArrayList<>();
		for(long i = 0; i < 10; i++) {
			long holdNumber = issuer.authorizeHold("1", 1.0);
			holdNumbers.add(holdNumber);
		}

		assertEquals(-1, issuer.authorizeHold("1", 1.0));
	}

	@Test
	public void testReleaseWhileBlocked() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		long holdNumber = issuer.authorizeHold("1", 5.5);
		assertTrue(issuer.block("1"));
		assertFalse(issuer.releaseHold("1", holdNumber));
	}

	@Test
	public void testBadBlock() {
		assertFalse(issuer.block("0"));
	}

	@Test
	public void testBadAuthorizeHold() {
		assertTrue(issuer.authorizeHold("0", 1.0) == -1);
	}

	@Test
	public void testBadReleaseHold() {
		assertFalse(issuer.releaseHold("0", 1));
	}

	@Test
	public void testBadReleaseHold2() {
		assertFalse(issuer.releaseHold("0", -1));
	}

	@Test
	public void testExcessHold() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		long hold = issuer.authorizeHold("1", 5);
		assertTrue(hold >= 0);
		assertTrue(issuer.authorizeHold("1", 3) >= 0);
		assertTrue(issuer.authorizeHold("1", 5) == -1);
		assertTrue(issuer.releaseHold("1", hold));
		assertTrue(issuer.authorizeHold("1", 7) >= 0);
	}

	@Test
	public void testHold() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		for(long i = 0; i <= 5000; i++) {
			long hold = issuer.authorizeHold("1", 5);
			assertTrue(issuer.releaseHold("1", hold));
		}
	}

	@Test
	public void testBadPost() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		assertFalse(issuer.postTransaction("1", -1, 1.0));
	}

	@Test
	public void testBadPost2() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		assertFalse(issuer.postTransaction("1", 0, 1.0));
		assertFalse(issuer.postTransaction("0", 0, 1.0));
	}
	
	@Test
	public void testBadHold() {
		issuer.addCardData("1", "me", tomorrow, "111", 10);
		issuer.authorizeHold("1", 0.0);
	}
}
