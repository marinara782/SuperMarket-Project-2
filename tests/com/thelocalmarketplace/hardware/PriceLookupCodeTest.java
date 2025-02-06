package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class PriceLookupCodeTest {
	@Test(expected = SimulationException.class)
	public void testZeroCode() {
		String code = "0";
		PriceLookUpCode plu = new PriceLookUpCode(code);

		assertEquals(1, plu.numeralCount());
		assertEquals(Numeral.zero, plu.getNumeralAt(0));
		assertEquals(code, plu.toString());
	}

	@Test(expected = SimulationException.class)
	public void testNullCode() {
		new PriceLookUpCode(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCode() {
		new PriceLookUpCode("0a00");
	}

	@Test(expected = SimulationException.class)
	public void testBadIndex() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);
		plu.getNumeralAt(-1);
	}

	@Test
	public void testFiveDigitCode() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);

		assertEquals(5, plu.numeralCount());
		for(int i = 0; i < 5; i++)
			assertEquals(Numeral.zero, plu.getNumeralAt(i));
		assertEquals(code, plu.toString());
	}

	@Test(expected = SimulationException.class)
	public void testSixDigitCode() {
		String code = "000000";
		new PriceLookUpCode(code);
	}

	@Test(expected = SimulationException.class)
	public void testBadCharacterCode() {
		String code = "/";
		new PriceLookUpCode(code);
	}

	@Test(expected = SimulationException.class)
	public void testBadCharacterCode2() {
		String code = ":";
		new PriceLookUpCode(code);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testCodeNotEqualToString() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);

		assertFalse(plu.equals(code));
	}

	@Test
	public void testCodesEqual() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);
		PriceLookUpCode plu2 = new PriceLookUpCode(code);

		assertTrue(plu.equals(plu2));
	}

	@Test
	public void testCodesNotEqual() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);
		String code2 = "0000";
		PriceLookUpCode plu2 = new PriceLookUpCode(code2);

		assertFalse(plu.equals(plu2));
	}

	@Test
	public void testCodesNotEqual2() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);
		String code2 = "00001";
		PriceLookUpCode plu2 = new PriceLookUpCode(code2);

		assertFalse(plu.equals(plu2));
	}

	@Test
	public void testHashCodesEqual() {
		String code = "00000";
		PriceLookUpCode plu = new PriceLookUpCode(code);
		PriceLookUpCode plu2 = new PriceLookUpCode(code);

		assertEquals(plu.hashCode(), plu2.hashCode());
	}
}
