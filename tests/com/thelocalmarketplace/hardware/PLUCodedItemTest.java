package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

import ca.ucalgary.seng300.simulation.SimulationException;

@SuppressWarnings("javadoc")
public class PLUCodedItemTest {
	@Test
	public void test() {
		PriceLookUpCode pluCode = new PriceLookUpCode("01234");
		PLUCodedItem item = new PLUCodedItem(pluCode, new Mass(BigInteger.ONE));

		assertEquals(new Mass(BigInteger.ONE), item.getMass());
		assertTrue(pluCode == item.getPLUCode());
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new PLUCodedItem(null, new Mass(BigInteger.ONE));
	}
	
	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new PLUCodedItem(new PriceLookUpCode("0000"), new Mass(BigInteger.ZERO));
	}
}
