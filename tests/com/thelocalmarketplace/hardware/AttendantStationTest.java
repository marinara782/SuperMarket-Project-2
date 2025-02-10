package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;

import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class AttendantStationTest {
	private AttendantStation supervisionStation;
	private SelfCheckoutStation station;
	private Currency CAD = Currency.getInstance(Locale.CANADA);

	@Before
	public void setup() {
		SelfCheckoutStation.resetConfigurationToDefaults();
		supervisionStation = new AttendantStation();
	}

	@Test
	public void testCreate() {
		assertEquals(0, supervisionStation.supervisedStationCount());
		assertEquals(0, supervisionStation.supervisedStations().size());
	}

	@Test
	public void testAddAndRemove() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(10.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		supervisionStation.add(station);
		assertEquals(1, supervisionStation.supervisedStationCount());
		assertEquals(1, supervisionStation.supervisedStations().size());

		supervisionStation.remove(station);
		assertEquals(0, supervisionStation.supervisedStationCount());
		assertEquals(0, supervisionStation.supervisedStations().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadAdd() {
		supervisionStation.add(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testBadAdd2() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(10.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		supervisionStation.add(station);
		supervisionStation.add(station);
	}

	@Test
	public void testRemoveNull() {
		supervisionStation.remove(null);
	}

	@Test
	public void testPlugIn() {
		supervisionStation.unplug();
		supervisionStation.plugIn(PowerGrid.instance());

		assertEquals(true, supervisionStation.keyboard.isPluggedIn());
		assertEquals(false, supervisionStation.keyboard.isPoweredUp());
	}

	@Test
	public void testUnplug() {
		supervisionStation.unplug();

		assertEquals(false, supervisionStation.keyboard.isPluggedIn());
		assertEquals(false, supervisionStation.keyboard.isPoweredUp());
	}

	@Test
	public void testTurnOn() {
		supervisionStation.plugIn(PowerGrid.instance());
		supervisionStation.turnOn();

		assertEquals(true, supervisionStation.keyboard.isPluggedIn());
		assertEquals(true, supervisionStation.keyboard.isPoweredUp());
	}

	@Test
	public void testTurnOff() {
		supervisionStation.plugIn(PowerGrid.instance());
		supervisionStation.turnOn();
		supervisionStation.turnOff();

		assertEquals(true, supervisionStation.keyboard.isPluggedIn());
		assertEquals(false, supervisionStation.keyboard.isPoweredUp());
	}
}
