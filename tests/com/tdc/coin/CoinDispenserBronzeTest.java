package com.tdc.coin;

import static org.junit.Assert.assertEquals;
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
import com.tdc.NoCashAvailableException;
import com.tdc.StandardSinkStub;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.ICoinDispenser;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinDispenserBronzeTest {
	private CoinDispenserBronze dispenser;
	private Currency currency = Currency.getInstance(Locale.CANADA);

	@Before
	public void setup() {
		dispenser = new CoinDispenserBronze(1);
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
		dispenser = new CoinDispenserBronze(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load((Coin)null);
	}

	@Test(expected = SimulationException.class)
	public void testBadLoad3() throws SimulationException, CashOverloadException {
		dispenser = new CoinDispenserBronze(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load((Coin)null);
	}

	@Test(expected = NoCashAvailableException.class)
	public void testBadEmit() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser = new CoinDispenserBronze(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.emit();
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new CoinDispenserBronze(0);
	}

	@Test
	public void testSizeAndCapacity() {
		assertEquals(0, dispenser.size());
		assertEquals(1, dispenser.getCapacity());
	}

	private int found;

	@Test(expected = NoCashAvailableException.class)
	public void testEmitOnEmpty() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser.emit();
	}

	@Test
	public void testEmitUntilEmpty() throws CashOverloadException, NoCashAvailableException, DisabledException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenserBronze(1);
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
		dispenser = new CoinDispenserBronze(2);
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
		dispenser = new CoinDispenserBronze(1);
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		dispenser.load(coin, coin);
	}

	@Test
	public void testUnload() throws SimulationException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		dispenser = new CoinDispenserBronze(2);
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

	@Test(expected = ComponentFailure.class)
	public void testReject() throws DisabledException, CashOverloadException {
		dispenser.reject(null);
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
	public void testEmitWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser.disactivate();
		dispenser.emit();
	}
	
	@Test(expected = NoPowerException.class)
	public void testRejectWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		dispenser.disactivate();
		dispenser.reject(null);
	}
}
