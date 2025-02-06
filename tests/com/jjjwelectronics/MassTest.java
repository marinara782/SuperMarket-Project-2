package com.jjjwelectronics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.jjjwelectronics.Mass;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

@SuppressWarnings("javadoc")
public class MassTest {
	@Test(expected = NullPointerSimulationException.class)
	public void testCreateNull() {
		new Mass((BigInteger)null);
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testCreateNegative() {
		new Mass(BigInteger.valueOf(-1));
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testCreateNegativeLong() {
		new Mass(-1);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testCreateNullDecimal() {
		new Mass((BigDecimal)null);
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testCreateNegativeDecimal() {
		new Mass(BigDecimal.valueOf(-1.0));
	}

	@Test
	public void testCreateDouble() {
		new Mass(1.0);
	}

	@Test
	public void testCreateBigDecimal() {
		Mass mass = new Mass(BigDecimal.valueOf(1.0));

		assertEquals(new Mass(1_000_000).inMicrograms().intValue(), mass.inMicrograms().intValue());
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testCompareToNull() {
		Mass mass = new Mass(1);
		mass.compareTo(null);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testDifferenceFromNull() {
		Mass mass = new Mass(1);
		mass.difference(null);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testSumNull() {
		Mass mass = new Mass(1);
		mass.sum(null);
	}

	@Test
	public void testInGrams() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertEquals(1, mass.inGrams().intValueExact());
	}

	@Test
	public void testNotEquals() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertFalse(mass.equals(Mass.ZERO));
	}

	@Test
	public void testNotEqualsNull() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertFalse(mass.equals(null));
	}

	@Test
	public void testEquals() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertTrue(mass.equals(new Mass(Mass.MICROGRAMS_PER_GRAM)));
	}

	@Test
	public void testHashCode() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertEquals(mass.hashCode(), new Mass(Mass.MICROGRAMS_PER_GRAM).hashCode());
	}

	@Test
	public void testToString() {
		Mass mass = new Mass(0);

		assertEquals("0 mcg", mass.toString());
	}

	@Test
	public void testCompareToDifference() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertEquals(0, mass.difference(new Mass(Mass.MICROGRAMS_PER_GRAM)).compareTo(Mass.ZERO));
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testCompareDifferenceToNull() {
		Mass mass = new Mass(Mass.MICROGRAMS_PER_GRAM);

		assertEquals(0, mass.difference(new Mass(Mass.MICROGRAMS_PER_GRAM)).compareTo(null));
	}
}
