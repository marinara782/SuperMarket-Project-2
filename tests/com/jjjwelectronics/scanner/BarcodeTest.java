package com.jjjwelectronics.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class BarcodeTest {
	private Numeral[] convert(String s) {
		int len = s.length();
		Numeral[] digits = new Numeral[len];

		for(int i = 0; i < len; i++)
			try {
				digits[i] = Numeral.valueOf((byte)Character.digit(s.charAt(i), 10));
			}
			catch(IllegalDigitException e) {
				throw new InvalidArgumentSimulationException("s");
			}
		
		return digits;
	}

	@Test
	public void testZeroCode() {
		String code = "0";
		Barcode plu = new Barcode(convert(code));

		assertEquals(1, plu.digitCount());
		assertEquals(Numeral.zero, plu.getDigitAt(0));
		assertEquals(code, plu.toString());
	}

	@Test(expected = SimulationException.class)
	public void testNullCode() {
		new Barcode(null);
	}

	@Test(expected = SimulationException.class)
	public void testEmptyCode() {
		new Barcode(convert(""));
	}
	
	@Test(expected = SimulationException.class)
	public void testBadIndex() {
		new Barcode(convert("0")).getDigitAt(-1);
	}
	
	@Test
	public void test48DigitCode() {
		String code = "000000000000000000000000000000000000000000000000";
		Barcode plu = new Barcode(convert(code));

		assertEquals(48, code.length());
		assertEquals(48, plu.digitCount());
		for(int i = 0; i < 48; i++)
			assertEquals(Numeral.zero, plu.getDigitAt(i));
		assertEquals(code, plu.toString());
	}

	@Test(expected = SimulationException.class)
	public void test49DigitCode() {
		String code = "0000000000000000000000000000000000000000000000000";
		assertEquals(49, code.length());
		new Barcode(convert(code));
	}

	@Test(expected = SimulationException.class)
	public void testBadCharacterCode() {
		String code = "/";
		new Barcode(convert(code));
	}

	@Test(expected = SimulationException.class)
	public void testBadCharacterCode2() {
		String code = ":";
		new Barcode(convert(code));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testCodeNotEqualToString() {
		String code = "00000";
		Barcode plu = new Barcode(convert(code));

		assertFalse(plu.equals(code));
	}

	@Test
	public void testCodesEqual() {
		String code = "00000";
		Barcode plu = new Barcode(convert(code));
		Barcode plu2 = new Barcode(convert(code));

		assertTrue(plu.equals(plu2));
	}

	@Test
	public void testCodesNotEqual() {
		String code = "00000";
		Barcode plu = new Barcode(convert(code));
		String code2 = "0000";
		Barcode plu2 = new Barcode(convert(code2));

		assertFalse(plu.equals(plu2));
	}

	@Test
	public void testCodesNotEqual2() {
		String code = "00000";
		Barcode plu = new Barcode(convert(code));
		String code2 = "00001";
		Barcode plu2 = new Barcode(convert(code2));

		assertFalse(plu.equals(plu2));
	}

	@Test
	public void testHashCodesEqual() {
		String code = "00000";
		Barcode plu = new Barcode(convert(code));
		Barcode plu2 = new Barcode(convert(code));

		assertEquals(plu.hashCode(), plu2.hashCode());
	}
}
