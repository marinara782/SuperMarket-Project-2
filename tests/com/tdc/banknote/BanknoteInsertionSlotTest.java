package com.tdc.banknote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.StandardSinkStub;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteInsertionSlotObserver;
import com.tdc.banknote.BanknoteStorageUnit;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BanknoteInsertionSlotTest {
	private BanknoteInsertionSlot slot;
	private Currency currency;
	private Banknote banknote;

	@Before
	public void setup() {
		slot = new BanknoteInsertionSlot();
		slot.sink = new StandardSinkStub<>(true);
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
	public void testHasSpaceWhenDangling() throws DisabledException, CashOverloadException {
		slot.reject(banknote);
		assertFalse(slot.hasSpace());
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept2() throws DisabledException, CashOverloadException {
		slot.receive(null);
	}

	@Test
	public void testReceiveInverted() throws DisabledException, CashOverloadException {
		slot = new BanknoteInsertionSlot();
		slot.sink = new StandardSinkStub<>(false);
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.receive(banknote);

		assertTrue(slot.hasDanglingBanknotes());
	}

	@Test
	public void testReceiveNormalAndNoSpace() throws DisabledException, CashOverloadException {
		slot = new BanknoteInsertionSlot();
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.sink = new StandardSinkStub<>(false);
		slot.receive(banknote);

		assertTrue(slot.hasDanglingBanknotes());
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException {
		BanknoteStorageUnit bsu = new BanknoteStorageUnit(1);
		slot.sink = new StandardSinkStub<>(true);
		bsu.connect(PowerGrid.instance());
		bsu.activate();
		slot.attach(new BanknoteInsertionSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteRemoved(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteInserted(BanknoteInsertionSlot slot) {
				found++;
			}

			@Override
			public void banknoteEjected(BanknoteInsertionSlot slot) {
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
		assertEquals(1, found);
	}

	@Test
	public void testAccept2() throws DisabledException, CashOverloadException {
		BanknoteStorageUnit bsu = new BanknoteStorageUnit(1);
		slot.sink = new StandardSinkStub<>(true);
		bsu.connect(PowerGrid.instance());
		bsu.activate();
		slot.receive(banknote);
		slot.receive(banknote);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws DisabledException, CashOverloadException {
		slot.disable();
		slot.receive(banknote);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptWhenDangling() throws SimulationException, DisabledException, CashOverloadException {
		slot.emit(banknote);
		slot.receive(banknote);
	}

	@Test(expected = CashOverloadException.class)
	public void testEmitWhenDangling() throws SimulationException, DisabledException, CashOverloadException {
		slot.attach(new BanknoteInsertionSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteRemoved(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteInserted(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteEjected(BanknoteInsertionSlot slot) {
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
		slot.emit(banknote);
		assertEquals(1, found);

		slot.emit(banknote);
	}

	@Test(expected = SimulationException.class)
	public void testEmitNull() throws SimulationException, DisabledException, CashOverloadException {
		slot.emit(null);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledEmit() throws SimulationException, DisabledException, CashOverloadException {
		slot.disable();
		slot.emit(null);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptOnLyingSink() throws DisabledException, CashOverloadException {
		slot.sink = new StandardSinkStub<Banknote>(true) {
			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				throw new CashOverloadException();
			}
		};
		slot.receive(banknote);
	}

	@Test
	public void testAcceptOnInvertedSlot() throws DisabledException, CashOverloadException {
		slot = new BanknoteInsertionSlot();
		slot.connect(PowerGrid.instance());
		slot.activate();
		assertEquals(true, slot.hasSpace());
	}

	@Test(expected = SimulationException.class)
	public void testRemoveNullDangling() {
		slot.removeDanglingBanknote();
	}

	private int found;

	@Test
	public void testRemoveDangling() throws SimulationException, DisabledException, CashOverloadException {
		slot.emit(banknote);
		slot.attach(new BanknoteInsertionSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteRemoved(BanknoteInsertionSlot slot) {
				found++;
			}

			@Override
			public void banknoteInserted(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteEjected(BanknoteInsertionSlot slot) {
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
		assertEquals(banknote, slot.removeDanglingBanknote());
		assertEquals(1, found);
	}

	@Test(expected = ComponentFailure.class)
	public void testRejectWhileDangling() {
		assertEquals(false, slot.hasDanglingBanknotes());
		slot.reject(banknote);
		assertEquals(true, slot.hasDanglingBanknotes());
		slot.reject(banknote);
	}

	@Test
	public void testDetach() {
		slot.disable();

		BanknoteInsertionSlotObserver observer = new BanknoteInsertionSlotObserver() {
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
			public void banknoteInserted(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteEjected(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteRemoved(BanknoteInsertionSlot slot) {
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

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testEmitWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.emit(null);
	}

	@Test(expected = NoPowerException.class)
	public void testRejectWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.reject(null);
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException {
		slot.disactivate();
		slot.hasSpace();
	}
}
