package com.jjjwelectronics.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class BarcodedItemTest {
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
	public void test() {
		Barcode barcode = new Barcode(convert("0123456789"));
		BarcodedItem item = new BarcodedItem(barcode, new Mass(BigInteger.ONE));

		assertEquals(item.getMass().inMicrograms(), BigInteger.ONE);
		assertTrue(barcode == item.getBarcode());
	}
	
	@Test(expected = NullPointerSimulationException.class)
	public void testWithNullMass() {
		Barcode barcode = new Barcode(convert("0123456789"));
		new BarcodedItem(barcode, null);		
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new BarcodedItem(null, Mass.ONE_GRAM);
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new BarcodedItem(new Barcode(convert("0")), Mass.ZERO);
	}
}
