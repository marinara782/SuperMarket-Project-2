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
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BanknoteStorageUnitTest {
	private BanknoteStorageUnit unit;
	private Currency currency;
	private Banknote banknote;
	private int found;

	@Before
	public void setup() {
		unit = new BanknoteStorageUnit(1);
		currency = Currency.getInstance(Locale.CANADA);
		banknote = new Banknote(currency, BigDecimal.ONE);
		found = 0;
		unit.connect(PowerGrid.instance());
		unit.activate();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		unit.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept3() throws CashOverloadException, DisabledException {
		unit.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new BanknoteStorageUnit(0);
	}

	@Test
	public void testCapacity() {
		assertEquals(1, unit.getCapacity());
		assertEquals(0, unit.getBanknoteCount());
	}

	@Test(expected = SimulationException.class)
	public void testBadLoad() throws SimulationException, CashOverloadException {
		unit.load((Banknote[])null);
	}

	@Test(expected = SimulationException.class)
	public void testNullLoad() throws SimulationException, CashOverloadException {
		unit.load((Banknote)null);
	}

	@Test(expected = CashOverloadException.class)
	public void testOverload() throws SimulationException, CashOverloadException {
		unit.load(banknote, banknote);
	}

	@Test
	public void testLoad() throws SimulationException, CashOverloadException {
		unit.attach(new BanknoteStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				found++;
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
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
		unit.load(banknote);
		assertEquals(1, found);
	}

	@Test
	public void testNullUnload() {
		unit.attach(new BanknoteStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				found++;
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
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
		List<Banknote> result = unit.unload();
		assertEquals(1, result.size());
		assertEquals(null, result.get(0));
		assertEquals(1, found);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws DisabledException, CashOverloadException {
		unit.disable();
		unit.receive(null);
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException {
		unit.attach(new BanknoteStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				found++;
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
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
		unit.receive(banknote);
		assertEquals(1, unit.getBanknoteCount());
		assertEquals(2, found);
	}

	@Test
	public void testAcceptWithoutFilling() throws DisabledException, CashOverloadException {
		unit = new BanknoteStorageUnit(2);
		unit.connect(PowerGrid.instance());
		unit.activate();
		unit.attach(new BanknoteStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
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
		unit.receive(banknote);
		assertEquals(1, unit.getBanknoteCount());
		assertEquals(1, found);
	}

	@Test(expected = CashOverloadException.class)
	public void testOverloadAccept() throws DisabledException, CashOverloadException {
		unit.receive(banknote);
		unit.receive(banknote);
	}

	@Test(expected = NoPowerException.class)
	public void testSizeWithoutPower() throws CashOverloadException, DisabledException {
		unit.disactivate();
		unit.getBanknoteCount();
	}

	@Test(expected = NoPowerException.class)
	public void testLoadWithoutPower() throws CashOverloadException, DisabledException {
		unit.disactivate();
		unit.load((Banknote[])null);
	}

	@Test(expected = NoPowerException.class)
	public void testUnloadWithoutPower() throws CashOverloadException, DisabledException {
		unit.disactivate();
		unit.unload();
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException {
		unit.disactivate();
		unit.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException {
		unit.disactivate();
		unit.hasSpace();
	}

	@Test
	public void testHasSpaceWithPower() throws CashOverloadException, DisabledException {
		unit.activate();
		assertTrue(unit.hasSpace());
	}

	@Test
	public void testHasNoSpaceWithPower() throws CashOverloadException, DisabledException {
		unit.activate();
		unit.receive(banknote);
		assertFalse(unit.hasSpace());
	}
}
