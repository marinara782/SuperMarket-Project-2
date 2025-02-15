package com.jjjwelectronics.card;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.card.Card.CardInsertData;
import com.jjjwelectronics.card.Card.CardSwipeData;
import com.jjjwelectronics.card.Card.CardTapData;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class CardTest {
	private Card card, otherCard;

	@Before
	public void setup() {
		card = new Card("Visa", "1", "Me", "111", "1111", true, true);
		otherCard = new Card("other", "1", "You", null, null, false, false);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new Card(null, "", "", "", "", false, false);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new Card("", null, "", "", "", false, false);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate3() {
		new Card("", "", null, "", "", false, false);
	}

	@Test
	public void testCreate4() {
		new Card("", "", "", null, "", false, false);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate5() {
		new Card("", "", "", "", null, true, true);
	}

	@Test
	public void testTap() {
		boolean failure = false, success = false;

		for(int i = 0; i < 1000000; i++)
			try {
				success = (card.tap() != null);
			}
			catch(IOException e) {
				failure = true;
			}

		assertTrue(failure);
		assertTrue(success);
	}

	@Test
	public void testBadTap() throws IOException {
		assertTrue(otherCard.tap() == null);
	}

	@Test
	public void testBadInsert() throws IOException {
		assertTrue(otherCard.insert("") == null);
	}

	@Test
	public void testTapData() {
		int count = 0;
		CardTapData data = null;

		while(true) {
			while(true) {
				try {
					data = card.tap();
					if(data != null)
						break;
				}
				catch(IOException e) {}
			}

			boolean differenceFound = false;
			differenceFound |= !data.getType().equals("Visa");
			differenceFound |= !data.getNumber().equals("1");
			differenceFound |= !data.getCardholder().equals("Me");
			differenceFound |= !data.getCVV().equals("111");

			if(differenceFound) {
				System.out.println(count);
				return;
			}

			count++;
		}
	}

	@Test
	public void testSwipeData() {
		int count = 0;
		CardSwipeData data = null;

		while(true) {
			while(true) {
				try {
					data = card.swipe();
					if(data != null)
						break;
				}
				catch(IOException e) {}
			}

			boolean differenceFound = false;
			differenceFound |= !data.getType().equals("Visa");
			differenceFound |= !data.getNumber().equals("1");
			differenceFound |= !data.getCardholder().equals("Me");

			if(differenceFound) {
				System.out.println(count);
				return;
			}

			try {
				data.getCVV();
				fail();
			}
			catch(UnsupportedOperationException e) {}

			count++;
		}
	}

	@Test
	public void testInsertData() {
		int count = 0;
		CardInsertData data = null;

		while(true) {
			while(true) {
				try {
					data = card.insert("1111");
					if(data != null)
						break;
				}
				catch(IOException e) {}
			}

			boolean differenceFound = false;
			differenceFound |= !data.getType().equals("Visa");
			differenceFound |= !data.getNumber().equals("1");
			differenceFound |= !data.getCardholder().equals("Me");
			differenceFound |= !data.getCVV().equals("111");

			if(differenceFound) {
				System.out.println(count);
				return;
			}

			count++;
		}
	}

	@Test(expected = BlockedCardException.class)
	public void testBlocking() throws IOException {
		try {
			card.insert("0");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("1");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("2");
		}
		catch(InvalidPINException e) {}
		card.insert("1111");
	}

	@Test(expected = BlockedCardException.class)
	public void testBlocking2() throws IOException {
		try {
			card.insert("0");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("1");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("2");
		}
		catch(InvalidPINException e) {}
		card.swipe();
	}

	@Test(expected = BlockedCardException.class)
	public void testBlocking3() throws IOException {
		try {
			card.insert("0");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("1");
		}
		catch(InvalidPINException e) {}
		try {
			card.insert("2");
		}
		catch(InvalidPINException e) {}
		card.tap();
	}
}
