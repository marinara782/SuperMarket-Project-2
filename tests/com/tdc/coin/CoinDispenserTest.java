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
import com.tdc.StandardSinkStub;
import com.tdc.StandardSourceStub;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinDispenserTest {
	private CoinDispenser dispenser;
	private Currency currency = Currency.getInstance(Locale.CANADA);

	@Before
	public void setup() {
		dispenser = new CoinDispenser(1);
		found = 0;
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = SimulationException.class)
	public void testBadLoad() throws SimulationException, CashOverloadException {
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load((Coin)null);
	}

	@Test(expected = SimulationException.class)
	public void testBadLoad3() throws SimulationException, CashOverloadException {
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load((Coin)null);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		dispenser.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept2() throws CashOverloadException, DisabledException {
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.receive(null);
	}

	@Test(expected = NoCashAvailableException.class)
	public void testBadEmit() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.emit();
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new CoinDispenser(0);
	}

	@Test
	public void testSizeAndCapacity() {
		assertEquals(0, dispenser.size());
		assertEquals(1, dispenser.getCapacity());
	}

	private int found;

	@Test
	public void testGoodAccept() throws CashOverloadException, DisabledException {
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				found++;
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				found++;
			}
		});
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser.receive(coin);
		assertEquals(2, found);
	}

	@Test
	public void testGoodAccept2() throws CashOverloadException, DisabledException {
		dispenser = new CoinDispenser(2);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				found++;
			}
		});
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser.receive(coin);
		assertEquals(1, found);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws CashOverloadException, DisabledException {
		dispenser.disable();
		dispenser.receive(null);
	}

	@Test(expected = CashOverloadException.class)
	public void testFullAccept() throws CashOverloadException, DisabledException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		assertTrue(dispenser.hasSpace());
		dispenser.receive(coin);
		assertFalse(dispenser.hasSpace());
		dispenser.receive(coin);
	}

	@Test(expected = NoCashAvailableException.class)
	public void testEmitOnEmpty() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser.emit();
	}

	@Test
	public void testEmitUntilEmpty() throws CashOverloadException, NoCashAvailableException, DisabledException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load(coin);
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				found++;
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				found++;
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				fail();
			}
		});
		dispenser.sink = new StandardSinkStub<>(true);
		dispenser.emit();
		assertEquals(2, found);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledEmit() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser.disable();
		dispenser.emit();
	}

	@Test
	public void testEmitWithoutBeingEmpty() throws CashOverloadException, NoCashAvailableException, DisabledException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenser(2);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				found++;
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				fail();
			}
		});
		dispenser.load(coin, coin);
		assertEquals(1, found);

		found = 0;
		dispenser.detachAll();

		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				found++;
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				fail();
			}
		});
		dispenser.sink = new StandardSinkStub<>(true);
		dispenser.emit();
		assertEquals(1, found);
	}

	@Test(expected = CashOverloadException.class)
	public void testTooMuchCash() throws SimulationException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenser(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load(coin, coin);
	}

	@Test
	public void testUnload() throws SimulationException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenser(2);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load(coin, coin);
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				found++;
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				fail();
			}
		});
		List<Coin> unloadedCoins = dispenser.unload();
		assertEquals(2, unloadedCoins.size());
		assertEquals(1, found);
	}

	@Test
	public void testHasSpace() throws DisabledException, CashOverloadException, NoCashAvailableException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		StandardSourceStub<Coin> source = new StandardSourceStub<>(dispenser);
		dispenser.attach(new CoinDispenserObserver() {
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
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				found++;
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				found++;
			}
		});
		source.receive(coin);
		assertEquals(2, found);
		assertEquals(false, dispenser.hasSpace());
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testRejectNull() throws DisabledException, CashOverloadException {
		dispenser.source = new StandardSourceStub<Coin>(dispenser) {
			@Override
			public void reject(Coin c) {
				throw new NullPointerSimulationException();
			}
		};
		dispenser.reject(null);
	}

	@Test
	public void testReject() throws DisabledException, CashOverloadException {
		dispenser.source = new StandardSourceStub<Coin>(dispenser);
		dispenser.reject(new Coin(currency, BigDecimal.ONE));
	}

	@Test(expected = NoPowerException.class)
	public void testSizeWithoutPower() throws CashOverloadException, DisabledException {
		dispenser.disactivate();
		dispenser.size();
	}

	@Test(expected = NoPowerException.class)
	public void testLoadWithoutPower() throws CashOverloadException, DisabledException {
		dispenser.disactivate();
		dispenser.load((Coin[])null);
	}

	@Test(expected = NoPowerException.class)
	public void testUnloadWithoutPower() throws CashOverloadException, DisabledException {
		dispenser.disactivate();
		dispenser.unload();
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException {
		dispenser.disactivate();
		dispenser.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testEmitWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser.disactivate();
		dispenser.emit();
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser.disactivate();
		dispenser.hasSpace();
	}

	@Test(expected = NoPowerException.class)
	public void testRejectWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser.disactivate();
		dispenser.reject(null);
	}
}
