package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class BarcodedProductTest {
	private BarcodedProduct product;
	private Barcode barcode;

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

	@Before
	public void setup() {
		barcode = new Barcode(convert("0"));
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new BarcodedProduct(null, "", 1, 5.0);
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new BarcodedProduct(barcode, null, 1, 5.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadCreate5() {
		new BarcodedProduct(barcode, "", 1, 0.0);
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate3() {
		new BarcodedProduct(barcode, "", 0, 5.0);
	}

	@Test
	public void testGoodCreate() {
		product = new BarcodedProduct(barcode, "", 1, 5.0);
		assertEquals(barcode, product.getBarcode());
		assertEquals("", product.getDescription());
		assertEquals(1, product.getPrice());
		assertEquals(5.0, product.getExpectedWeight(), 0.01);
		assertEquals(true, product.isPerUnit());
	}
}
