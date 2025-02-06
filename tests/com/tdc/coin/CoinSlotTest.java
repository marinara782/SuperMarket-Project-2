package com.tdc.coin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
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
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinSlotObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinSlotTest {
	private CoinSlot slot;
	private Coin coin;
	private Currency currency = Currency.getInstance(Locale.CANADA);
	private int found;

	@Before
	public void setup() {
		slot = new CoinSlot();
		coin = new Coin(currency, BigDecimal.ONE);
		slot.connect(PowerGrid.instance());
		slot.activate();
		found = 0;
		slot.sink = new StandardSinkStub<Coin>(true) {
			@Override
			public void receive(Coin thing) throws CashOverloadException, DisabledException {
				found++;
			}
		};
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = SimulationException.class)
	public void testBadCoin() {
		Coin.DEFAULT_CURRENCY = null;
		new Coin(BigDecimal.TEN);
	}

	@Test(expected = SimulationException.class)
	public void testBadCoin3() {
		Coin.DEFAULT_CURRENCY = currency;
		new Coin(BigDecimal.valueOf(-1));
	}

	@Test
	public void testDefaultCoin() {
		Coin.DEFAULT_CURRENCY = currency;
		Coin c = new Coin(BigDecimal.TEN);
		assertEquals(currency, c.getCurrency());
		assertEquals(BigDecimal.TEN, c.getValue());
	}

	@Test(expected = SimulationException.class)
	public void testBadAttach2() {
		slot = new CoinSlot();
		slot.attach(null);
	}

	@Test
	public void testEnable() {
		found = 0;
		slot = new CoinSlot();
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.disable();
		
		slot.attach(new CoinSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				found++;
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
			public void coinInserted(CoinSlot slot) {
				fail();
			}
		});

		slot.enable();
		assertEquals(1, found);
	}

	@Test
	public void testDisable() {
		found = 0;
		slot = new CoinSlot();
		slot.attach(new CoinSlotObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				found++;
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
			public void coinInserted(CoinSlot slot) {
				fail();
			}
		});
		slot.connect(PowerGrid.instance());
		slot.activate();
		slot.disable();
		assertEquals(1, found);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		slot.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept2() throws CashOverloadException, DisabledException {
		slot.receive(null);
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException {
		slot.attach(new CoinSlotObserver() {
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
			public void coinInserted(CoinSlot slot) {
				found++;
			}
		});
		slot.receive(coin);
		assertEquals(2, found);
	}

	@Test(expected = DisabledException.class)
	public void testDisabledAccept() throws DisabledException, CashOverloadException {
		slot.disable();
		slot.receive(coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptLyingSink() throws DisabledException, CashOverloadException {
		slot.sink = new StandardSinkStub<Coin>(true) {
			@Override
			public void receive(Coin thing) throws CashOverloadException, DisabledException {
				throw new CashOverloadException();
			}
		};
		slot.receive(coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptBadSink() throws DisabledException, CashOverloadException {
		slot.sink = new StandardSinkStub<Coin>(false) {
			@Override
			public void receive(Coin thing) throws CashOverloadException, DisabledException {
				fail();
			}
		};
		slot.receive(coin);
	}

	@Test
	public void testHasSpaceBadSink() throws DisabledException {
		slot.sink = new StandardSinkStub<Coin>(false) {
			@Override
			public void receive(Coin thing) throws CashOverloadException, DisabledException {
				fail();
			}
		};
		assertEquals(false, slot.hasSpace());
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		slot.disactivate();
		slot.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		slot.disactivate();
		slot.hasSpace();
	}
}
