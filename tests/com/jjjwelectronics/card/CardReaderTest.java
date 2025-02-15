package com.jjjwelectronics.card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;
import powerutility.PowerSurge;

@SuppressWarnings("javadoc")
public class CardReaderTest {
	private AbstractCardReader reader;
	private Card card;
	private Card coopMemberCard;
	private int found;
	private CardData data;

	@Before
	public void setup() {
		reader = new CardReader();
		card = new Card("Visa", "1111", "I. P. Freely", "111", "1111", true, true);
		coopMemberCard = new Card("Calgary Coop", "123456", "I. P. Freely", null, null, false, false);
		found = 0;
		data = null;
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		reader.plugIn(PowerGrid.instance());
		reader.turnOn();
	}

	@After
	public void teardown() {
		card = null;
		PowerGrid.reconnectToMains();
	}

	@Test(expected = NoPowerException.class)
	public void testTapWithoutTurningOn() throws IOException {
		reader = new CardReader();
		reader.tap(card);
	}

	@Test(expected = NoPowerException.class)
	public void testSwipeWithoutTurningOn() throws IOException {
		reader = new CardReader();
		reader.swipe(card);
	}

	@Test(expected = NoPowerException.class)
	public void testInsertWithoutTurningOn() throws IOException {
		reader = new CardReader();
		reader.insert(card, "");
	}

	@Test(expected = NoPowerException.class)
	public void testRemoveWithoutTurningOn() throws IOException {
		reader = new CardReader();
		reader.remove();
	}

	@Test
	public void testTap() {
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				found = 1;
			}

			@Override
			public void aCardHasBeenSwiped() {
				fail();

			}

			@Override
			public void theCardHasBeenRemoved() {
				fail();

			}

			@Override
			public void aCardHasBeenInserted() {
				fail();
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				CardReaderTest.this.data = data;
			}
		});

		boolean retry;

		do {
			retry = false;
			try {
				reader.tap(card);
			}
			catch(IOException | NoPowerException | PowerSurge e) {
				retry = true;
			}
		}
		while(retry);

		assertEquals(1, found);
		assertTrue(data != null);
	}

	@Test(expected = ChipFailureException.class)
	public void testChipFailureOnTap() throws IOException {
		for(int i = 0; i < 1_000_000; i++)
			reader.tap(card);

		fail();
	}

	@Test
	public void testBadTap() throws IOException {
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				fail();
			}

			@Override
			public void aCardHasBeenSwiped() {
				fail();

			}

			@Override
			public void theCardHasBeenRemoved() {
				fail();

			}

			@Override
			public void aCardHasBeenInserted() {
				fail();
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				fail();
			}
		});

		reader.tap(coopMemberCard);
	}

	@Test(expected = IOException.class)
	public void testBadTap2() throws IOException {
		for(int i = 0; i < 10000000; i++)
			reader.tap(card);
	}

	@Test(expected = InvalidPINException.class)
	public void testBadInsert4() throws IOException {
		reader = new CardReader();
		reader.plugIn(PowerGrid.instance());
		reader.turnOn();
		while(true) {
			try {
				reader.insert(card, "");
			}
			catch(ChipFailureException e) {}

			reader.remove();
		}
	}

	@Test(expected = SimulationException.class)
	public void testBadRemove() throws IOException {
		reader = new CardReader();
		reader.plugIn(PowerGrid.instance());
		reader.turnOn();
		reader.remove();
	}

	@Test
	public void testSwipe() throws IOException {
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				fail();
			}

			@Override
			public void aCardHasBeenSwiped() {
				found++;
			}

			@Override
			public void theCardHasBeenRemoved() {
				fail();

			}

			@Override
			public void aCardHasBeenInserted() {
				fail();
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				CardReaderTest.this.data = data;
			}
		});

		for(int i = 0; i < 1000000; i++) {
			try {
				reader.swipe(card);
			}
			catch(IOException e) {}
		}

		assertEquals(1000000, found);
		assertTrue(data != null);
	}

	@Test
	public void testInsert() throws IOException {
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				fail();
			}

			@Override
			public void aCardHasBeenSwiped() {
				fail();
			}

			@Override
			public void theCardHasBeenRemoved() {
				found++;
			}

			@Override
			public void aCardHasBeenInserted() {
				found++;
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				CardReaderTest.this.data = data;
			}
		});

		String localPin = "11111".substring(0, 4);

		// Insertion might fail randomly, so keep trying.
		boolean succeeded = false;
		found = 0;
		while(!succeeded)
			try {
				reader.insert(card, localPin);
				succeeded = true;
			}
			catch(IOException e) {
				try {
					// Temporarily deregister the listener so the remove() does not cause failure
					CardReaderListener listener = reader.listeners().get(0);
					reader.deregister(listener);
					reader.remove();
					reader.register(listener);
					found = 0;
				}
				catch(NullPointerSimulationException e2) {}
			}

		assertEquals(1, found);
		assertTrue(data != null);
	}

	@Test
	public void testBadInsert() throws IOException {
		reader.plugIn(PowerGrid.instance());
		reader.turnOn();
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				fail();
			}

			@Override
			public void aCardHasBeenSwiped() {
				fail();
			}

			@Override
			public void theCardHasBeenRemoved() {
				found++;
			}

			@Override
			public void aCardHasBeenInserted() {
				found++;
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				fail();
			}
		});

		for(int i = 0; i < 1000000; i++) {
			try {
				reader.insert(coopMemberCard, null);
			}
			catch(IOException e) {}

			reader.remove();
		}

		assertEquals(2000000, found);
	}

	@Test(expected = IllegalStateException.class)
	public void testBadInsert2() throws IOException {
		while(true)
			try {
				reader.insert(card, "1111");
				break;
			}
			catch(ChipFailureException e) {}

		reader.insert(coopMemberCard, null);
	}

	@Test(expected = ChipFailureException.class)
	public void testBadInsert3() throws IOException {
		reader.register(new CardReaderListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aCardHasBeenTapped() {
				fail();
			}

			@Override
			public void aCardHasBeenSwiped() {
				fail();
			}

			@Override
			public void theCardHasBeenRemoved() {
				fail();
			}

			@Override
			public void aCardHasBeenInserted() {
				found++;
			}

			@Override
			public void theDataFromACardHasBeenRead(CardData data) {
				fail();
			}
		});
		reader.insert(coopMemberCard, null);
	}

	@Test
	public void testInsertWithBadPIN() throws IOException {
		try {
			reader.insert(card, "1");
			fail();
		}
		catch(InvalidPINException e) {}
		catch(ChipFailureException e) {}
	}
}
