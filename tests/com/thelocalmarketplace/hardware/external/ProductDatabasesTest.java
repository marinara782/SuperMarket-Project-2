package com.thelocalmarketplace.hardware.external;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

@SuppressWarnings("javadoc")
public class ProductDatabasesTest {
	PriceLookUpCode plu = new PriceLookUpCode("1111");
	PLUCodedProduct pluProduct = new PLUCodedProduct(plu, "bar", 1);
	Barcode barcode = new Barcode(convert("1234"));
	BarcodedProduct barcodeProduct = new BarcodedProduct(barcode, "foo", 10, 5.0);

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
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodeProduct);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu, pluProduct);
		ProductDatabases.INVENTORY.put(pluProduct, 1);
		ProductDatabases.INVENTORY.put(barcodeProduct, 2);
	}

	@Test
	public void test() {
		assertEquals(1, ProductDatabases.BARCODED_PRODUCT_DATABASE.size());
		assertEquals(1, ProductDatabases.PLU_PRODUCT_DATABASE.size());
		assertEquals(2, ProductDatabases.INVENTORY.size());
	}
}
