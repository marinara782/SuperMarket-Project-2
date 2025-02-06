package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.CoinTray;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinTrayTest {
	private CoinTray tray;
	private Currency currency;
	private Coin coin;
	private int found;

	@Before
	public void setup() {
		tray = new CoinTray(1);
		currency = Currency.getInstance(Locale.CANADA);
		coin = new Coin(currency, BigDecimal.ONE);
		found = 0;
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new CoinTray(0);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		tray.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept3() throws CashOverloadException, DisabledException {
		tray = new CoinTray(1);
		tray.receive(null);
	}

	@Test
	public void testAccept() throws CashOverloadException, DisabledException {
		tray.receive(coin);
		List<Coin> results = tray.collectCoins();
		assertEquals(1, results.size());
		assertEquals(coin, results.get(0));
		assertTrue(tray.hasSpace());
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptOverflow() throws CashOverloadException, DisabledException {
		tray.receive(coin);
		assertEquals(true, tray.hasSpace());
		tray.receive(coin);
	}

	@Test
	public void receiveDuringPowerTroubles() throws CashOverloadException, DisabledException {
		PowerGrid.instance().forcePowerSurge();
		tray.receive(coin);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testPlugIn() {
		tray.connect(PowerGrid.instance());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnplug() {
		tray.disconnect();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testTurnOn() {
		tray.activate();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testTurnOff() {
		tray.disactivate();
	}

	@Test
	public void testCollectCoins() {
		List<Coin> coins = tray.collectCoins();
		assertTrue(coins != null);
		assertTrue(coins.isEmpty());
	}
}
