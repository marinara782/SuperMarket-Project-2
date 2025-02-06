package com.tdc.coin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.tdc.coin.Coin;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class CoinTest {
	private Currency currency = Currency.getInstance(Locale.CANADA);

	@Test
	public void test() {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		assertTrue(currency == coin.getCurrency());
		assertEquals(BigDecimal.ONE, coin.getValue());
		assertEquals("1 CAD", coin.toString());
	}

	@Test(expected = SimulationException.class)
	public void testNullCurrency() {
		new Coin(null, BigDecimal.ONE);
	}

	@Test(expected = SimulationException.class)
	public void testZeroValue() {
		new Coin(currency, BigDecimal.ZERO);
	}
}
