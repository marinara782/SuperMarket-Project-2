package com.tdc.coin;

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
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinStorageUnitObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinStorageUnitTest {
	private CoinStorageUnit storage;
	private Currency currency;
	private Coin coin;
	private int found;

	@Before
	public void setup() {
		storage = new CoinStorageUnit(1);
		currency = Currency.getInstance(Locale.CANADA);
		coin = new Coin(currency, BigDecimal.ONE);
		found = 0;
		storage.connect(PowerGrid.instance());
		storage.activate();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}	

	@Test(expected = SimulationException.class)
	public void testBadCoin() throws DisabledException, CashOverloadException {
		storage.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new CoinStorageUnit(0);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		storage.receive(null);
	}

	@Test
	public void testCapacity() {
		assertEquals(1, storage.getCapacity());
		assertEquals(0, storage.getCoinCount());
	}

	@Test(expected = SimulationException.class)
	public void testNullLoad() throws SimulationException, CashOverloadException {
		storage.load((Coin[])null);
	}

	@Test(expected = SimulationException.class)
	public void testNullLoad2() throws SimulationException, CashOverloadException {
		storage.load((Coin)null);
	}

	@Test(expected = SimulationException.class)
	public void testNullLoad3() throws SimulationException, CashOverloadException {
		storage = new CoinStorageUnit(2);
		storage.connect(PowerGrid.instance());
		storage.activate();
		storage.load(coin, (Coin)null);
	}

	@Test(expected = SimulationException.class)
	public void testNullLoad4() throws SimulationException, CashOverloadException {
		storage = new CoinStorageUnit(2);
		storage.connect(PowerGrid.instance());
		storage.activate();
		storage.load((Coin)null, coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testOverLoad() throws SimulationException, CashOverloadException {
		storage.load(coin, coin);
	}

	@Test
	public void testLoad() throws SimulationException, CashOverloadException {
		storage = new CoinStorageUnit(2);
		storage.connect(PowerGrid.instance());
		storage.activate();
		storage.attach(new CoinStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void coinsUnloaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsLoaded(CoinStorageUnit unit) {
				found++;
			}

			@Override
			public void coinsFull(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinAdded(CoinStorageUnit unit) {
				fail();
			}
		});
		storage.load(coin, coin);
		assertEquals(1, found);
	}

	@Test
	public void testUnload() throws SimulationException, CashOverloadException {
		storage = new CoinStorageUnit(2);
		storage.connect(PowerGrid.instance());
		storage.activate();
		storage.load(coin, coin);
		storage.attach(new CoinStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void coinsUnloaded(CoinStorageUnit unit) {
				found++;
			}

			@Override
			public void coinsLoaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsFull(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinAdded(CoinStorageUnit unit) {
				fail();
			}
		});
		List<Coin> unloaded = storage.unload();
		assertEquals(1, found);
		assertEquals(2, unloaded.size());
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException {
		storage.attach(new CoinStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void coinsUnloaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsLoaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsFull(CoinStorageUnit unit) {
				found++;
			}

			@Override
			public void coinAdded(CoinStorageUnit unit) {
				found++;
			}
		});

		storage.receive(coin);
		assertEquals(2, found);
	}

	@Test
	public void testAccept2() throws DisabledException, CashOverloadException {
		storage = new CoinStorageUnit(2);
		storage.connect(PowerGrid.instance());
		storage.activate();
		storage.attach(new CoinStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void coinsUnloaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsLoaded(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinsFull(CoinStorageUnit unit) {
				fail();
			}

			@Override
			public void coinAdded(CoinStorageUnit unit) {
				found++;
			}
		});

		storage.receive(coin);
		assertEquals(1, found);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws DisabledException, CashOverloadException {
		storage.disable();
		storage.receive(coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptOverflow() throws DisabledException, CashOverloadException {
		assertTrue(storage.hasSpace());
		storage.receive(coin);
		assertFalse(storage.hasSpace());
		storage.receive(coin);
	}
	
	@Test(expected = NoPowerException.class)
	public void testSizeWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		storage.disactivate();
		storage.getCoinCount();
	}
	
	@Test(expected = NoPowerException.class)
	public void testLoadWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		storage.disactivate();
		storage.load((Coin[])null);
	}
	
	@Test(expected = NoPowerException.class)
	public void testUnloadWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		storage.disactivate();
		storage.unload();
	}
	
	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		storage.disactivate();
		storage.receive(null);
	}
	
	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		storage.disactivate();
		storage.hasSpace();
	}
}
