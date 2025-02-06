package com.tdc.banknote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.tdc.banknote.Banknote;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class BanknoteTest {
	private Currency currency = Currency.getInstance(Locale.CANADA);
	
	@Test
	public void test() {
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		
		assertTrue(currency == banknote.getCurrency());
		assertEquals(BigDecimal.ONE, banknote.getDenomination());
		assertEquals("1 CAD", banknote.toString());
	}
	
	@Test(expected = SimulationException.class)
	public void testNullCurrency() {
		new Banknote(null, BigDecimal.ONE);
	}
	
	@Test(expected = SimulationException.class)
	public void testZeroValue() {
		new Banknote(currency, BigDecimal.ZERO);
	}
}
