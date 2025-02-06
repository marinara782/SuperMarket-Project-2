package com.jjjwelectronics.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodeScannerSilver;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BarcodeScannerSilverTest {
	private IBarcodeScanner scanner;
	private int found;

	@Before
	public void setup() {
		scanner = new BarcodeScannerSilver();
		found = 0;
		scanner.plugIn(PowerGrid.instance());
		scanner.turnOn();
		PowerGrid.instance().forcePowerRestore();
		PowerGrid.engageUninterruptiblePowerSource();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = SimulationException.class)
	public void testBadScan() throws OverloadedDevice, DisabledDevice {
		scanner.scan(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadScan2() throws OverloadedDevice, DisabledDevice {
		scanner = new BarcodeScannerSilver();
		scanner.plugIn(PowerGrid.instance());
		scanner.turnOn();
		scanner.scan(null);
	}

	@Test(expected = NoPowerException.class)
	public void testScanWithoutTurningOn() throws OverloadedDevice, DisabledDevice {
		scanner = new BarcodeScannerSilver();
		scanner.scan(null);
	}

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

	@Test(expected = SimulationException.class)
	public void testBadBarcode() {
		new Barcode(new Numeral[] { Numeral.eight, null, Numeral.one });
	}

	@Test
	public void testScan() {
		PowerGrid.instance().forcePowerRestore();
		BarcodedItem item = new BarcodedItem(new Barcode(convert("00000")), new Mass(BigInteger.valueOf(10)));
		scanner.register(new BarcodeScannerListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
				found++;
				assertEquals("00000", barcode.toString());
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}
		});
		for(int i = 0; i < 100000; i++)
			scanner.scan(item);

		assertTrue(found < 100000);
	}

	@Test
	public void testScan1() {
		BarcodedItem item = new BarcodedItem(new Barcode(convert("00000")), new Mass(BigInteger.valueOf(10)));
		scanner.disable();
		scanner.register(new BarcodeScannerListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}
		});
		scanner.scan(item);
	}
}
