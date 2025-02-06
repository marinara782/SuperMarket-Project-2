package com.jjjwelectronics.scanner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jjjwelectronics.Numeral;

@SuppressWarnings("javadoc")
public class NumeralTest {
	@Test
	public void testValues() {
		assertEquals(10, Numeral.values().length);
	}
	
	@Test
	public void testValueOf0() {
		assertEquals(Numeral.zero, Numeral.valueOf((byte)0));
	}
	
	@Test
	public void testValueOf1() {
		assertEquals(Numeral.one, Numeral.valueOf((byte)1));
	}
	
	@Test
	public void testValueOf2() {
		assertEquals(Numeral.two, Numeral.valueOf((byte)2));
	}
	
	@Test
	public void testValueOf3() {
		assertEquals(Numeral.three, Numeral.valueOf((byte)3));
	}
	
	@Test
	public void testValueOf4() {
		assertEquals(Numeral.four, Numeral.valueOf((byte)4));
	}
	
	@Test
	public void testValueOf5() {
		assertEquals(Numeral.five, Numeral.valueOf((byte)5));
	}
	
	@Test
	public void testValueOf6() {
		assertEquals(Numeral.six, Numeral.valueOf((byte)6));
	}
	
	@Test
	public void testValueOf7() {
		assertEquals(Numeral.seven, Numeral.valueOf((byte)7));
	}
	
	@Test
	public void testValueOf8() {
		assertEquals(Numeral.eight, Numeral.valueOf((byte)8));
	}
	
	@Test
	public void testValueOf9() {
		assertEquals(Numeral.nine, Numeral.valueOf((byte)9));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadValueOf() {
		Numeral.valueOf((byte)-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadValueOf2() {
		Numeral.valueOf((byte)10);
	}
}
