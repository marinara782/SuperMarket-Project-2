package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class PLUCodedProductTest {
	private PLUCodedProduct product;
	private PriceLookUpCode pluCode;

	@Before
	public void setup() {
		pluCode = new PriceLookUpCode("0000");
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new PLUCodedProduct(null, "", 1);
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new PLUCodedProduct(pluCode, null, 1);
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate3() {
		new PLUCodedProduct(pluCode, "", 0);
	}

	@Test
	public void testGoodCreate() {
		product = new PLUCodedProduct(pluCode, "", 1);
		assertEquals(pluCode, product.getPLUCode());
		assertEquals("", product.getDescription());
		assertEquals(1, product.getPrice());
		assertEquals(false, product.isPerUnit());
	}
}
