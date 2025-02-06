package com.tdc.banknote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;
import com.tdc.banknote.BanknoteStorageUnit;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BanknoteDispensationSlotTest {
	private BanknoteDispensationSlot slot;
	private Currency currency;
	private Banknote banknote;

	@Before
	public void setup() {
		slot = new BanknoteDispensationSlot();
		// slot.sink = new StandardSinkStub<>(true);
		currency = Currency.getInstance(Locale.CANADA);
		banknote = new Banknote(currency, BigDecimal.ONE);
		found = 0;
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		slot.connect(PowerGrid.instance());
		slot.activate();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void receiveDispenseAndRemove() throws DisabledException, CashOverloadException {
		slot = new BanknoteDispensationSlot();
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.receive(banknote);
		slot.dispense();
		assertTrue(slot.hasDanglingBanknotes());

		List<Banknote> notes = slot.removeDanglingBanknotes();
		assertEquals(1, notes.size());
		assertEquals(banknote, notes.get(0));
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept2() throws DisabledException, CashOverloadException {
		slot.receive(null);
	}

	@Test
	public void testReceiveNormalAndNoSpace() throws DisabledException, CashOverloadException {
		slot = new BanknoteDispensationSlot();
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.receive(banknote);
		slot.dispense();

		assertTrue(slot.hasDanglingBanknotes());
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException {
		BanknoteStorageUnit bsu = new BanknoteStorageUnit(1);
		// slot.sink = new StandardSinkStub<>(true);
		bsu.connect(PowerGrid.instance());
		bsu.activate();
		slot.attach(new BanknoteDispensationSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
				fail();
			}

			@Override
			public void banknotesRemoved(BanknoteDispensationSlot slot) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});
		slot.receive(banknote);
		assertEquals(0, found);
	}

	@Test
	public void testAccept2() throws DisabledException, CashOverloadException {
		BanknoteStorageUnit bsu = new BanknoteStorageUnit(1);
		// slot.sink = new StandardSinkStub<>(true);
		bsu.connect(PowerGrid.instance());
		bsu.activate();
		slot.receive(banknote);
		slot.receive(banknote);
	}

	@Test
	public void testHasSpaceWhenDisabled() {
		slot.disable();
		assertFalse(slot.hasSpace());
	}

	@Test
	public void testHasSpaceWhenEmpty() {
		assertTrue(slot.hasSpace());
	}
	
	@Test(expected = NullPointerSimulationException.class)
	public void testRemoveWhenEmpty() {
		assertFalse(slot.hasDanglingBanknotes());
		slot.removeDanglingBanknotes();
	}

	@Test(expected = CashOverloadException.class)
	public void testReceiveWhenFull() throws DisabledException, CashOverloadException {
		for(int i = 0; i < 20; i++)
			slot.receive(banknote);

		slot.receive(banknote);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws DisabledException, CashOverloadException {
		slot.disable();
		slot.receive(banknote);
	}

	@Test(expected = ComponentFailure.class)
	public void testReceiveWhenDangling() throws SimulationException, DisabledException, CashOverloadException {
		try {
			slot.receive(banknote);
			slot.dispense();
			slot.receive(banknote);
		}
		catch(Throwable t) {
			fail();
		}
		slot.dispense();
	}

	@Test(expected = ComponentFailure.class)
	public void testReceiveWhenDanglingWithObserver()
		throws SimulationException, DisabledException, CashOverloadException {
		slot.attach(new BanknoteDispensationSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
				found++;
			}

			@Override
			public void banknotesRemoved(BanknoteDispensationSlot slot) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});
		slot.receive(banknote);
		slot.dispense();
		assertEquals(1, found);

		slot.dispense();
	}

	private int found;

	@Test
	public void testRemoveDangling() throws SimulationException, DisabledException, CashOverloadException {
		slot.attach(new BanknoteDispensationSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
				found++;
			}

			@Override
			public void banknotesRemoved(BanknoteDispensationSlot slot) {
				found++;
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});
		slot.receive(banknote);
		slot.dispense();
		slot.removeDanglingBanknotes();
		assertEquals(2, found);
	}

	@Test
	public void testDetach() {
		slot.disable();

		BanknoteDispensationSlotObserver observer = new BanknoteDispensationSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				found++;
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
				found++;
			}

			@Override
			public void banknotesRemoved(BanknoteDispensationSlot slot) {
				fail();
			}
		};

		slot.attach(observer);
		slot.enable();
		assertEquals(1, found);

		found = 0;
		slot.enable();
		assertEquals(0, found);
	}

	@Test
	public void testRemoveWithoutPower() throws CashOverloadException, DisabledException {
		slot.receive(banknote);
		slot.dispense();
		slot.disactivate();
		slot.removeDanglingBanknotes();
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testDispenseWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.dispense();
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.hasSpace();
	}
}
