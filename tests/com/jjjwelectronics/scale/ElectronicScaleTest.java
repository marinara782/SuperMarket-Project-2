package com.jjjwelectronics.scale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class ElectronicScaleTest {
	private AbstractElectronicScale scale;
	private Item heavyItem;
	private Item normalItem;
	private Item lightItem;
	private int found;

	static class MyScale extends AbstractElectronicScale {
		public MyScale(Mass limit, Mass sensitivity) {
			super(limit, sensitivity);
		}
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

	@Before
	public void setup() {
		scale = new MyScale(new Mass(BigInteger.valueOf(50)), new Mass(BigInteger.valueOf(10))) {};
		heavyItem = new BarcodedItem(new Barcode(convert("0")), new Mass(BigInteger.valueOf(100)));
		normalItem = new BarcodedItem(new Barcode(convert("0")), new Mass(BigInteger.valueOf(20)));
		lightItem = new BarcodedItem(new Barcode(convert("0")), new Mass(BigInteger.valueOf(5)));
		found = 0;
		scale.plugIn(PowerGrid.instance());
		scale.turnOn();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = SimulationException.class)
	public void testCreateNullMass() {
		new MyScale(null, Mass.ONE_GRAM);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new MyScale(Mass.ZERO, Mass.ONE_GRAM);
	}

	@Test
	public void testMass() throws OverloadedDevice {
		assertEquals(new Mass(BigInteger.valueOf(50)), scale.getMassLimit());
		assertEquals(Mass.ZERO.inMicrograms().doubleValue(), scale.getCurrentMassOnTheScale().inMicrograms().doubleValue(),
			scale.getSensitivityLimit().inMicrograms().doubleValue());
		assertEquals(new Mass(BigInteger.valueOf(10)), scale.getSensitivityLimit());
	}

	@Test(expected = SimulationException.class)
	public void testAddNull() throws OverloadedDevice {
		scale.addAnItem(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadRemove1() throws OverloadedDevice {
		scale = new MyScale(new Mass(BigInteger.valueOf(5)), new Mass(BigInteger.valueOf(2)));
		scale.plugIn(PowerGrid.instance());
		scale.turnOn();
		scale.removeAnItem(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		scale = new MyScale(new Mass(BigInteger.valueOf(1)), Mass.ZERO);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate3() {
		scale = new MyScale(new Mass(BigInteger.valueOf(1)), null);
	}

	@Test
	public void testOverload() {
		scale.register(new ElectronicScaleListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
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

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				found++;
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				found++;
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				fail();
			}
		});
		scale.addAnItem(heavyItem);
		assertEquals(2, found);
	}

	@Test(expected = OverloadedDevice.class)
	public void testOverloadException() throws OverloadedDevice {
		scale.addAnItem(heavyItem);
		scale.getCurrentMassOnTheScale();
	}

	@Test(expected = SimulationException.class)
	public void testRepeatedAdd() {
		scale.addAnItem(normalItem);
		scale.addAnItem(normalItem);
	}

	@Test
	public void testAdding() {
		scale.register(new ElectronicScaleListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
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

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				found++;
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				fail();
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				fail();
			}
		});
		scale.addAnItem(normalItem);
		scale.addAnItem(lightItem);
		assertEquals(1, found);
	}

	@Test
	public void testOutOfOverload() {
		scale.register(new ElectronicScaleListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
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

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				found++;
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				found++;
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				found++;
			}
		});
		scale.addAnItem(heavyItem);
		scale.removeAnItem(heavyItem);
		assertEquals(3, found);
	}

	@Test(expected = SimulationException.class)
	public void testBadRemove() {
		scale.removeAnItem(heavyItem);
	}

	@Test
	public void testAddThenRemove() throws OverloadedDevice {
		scale.addAnItem(normalItem);
		scale.addAnItem(lightItem);
		scale.removeAnItem(normalItem);
		assertEquals(lightItem.getMass().inMicrograms().doubleValue(),
			scale.getCurrentMassOnTheScale().inMicrograms().doubleValue(), scale.getSensitivityLimit().inMicrograms().doubleValue());
	}

	@Test
	public void testAddThenRemove2() throws OverloadedDevice {
		scale = new MyScale(new Mass(BigInteger.valueOf(50)), new Mass(BigInteger.valueOf(10)));
		scale.plugIn(PowerGrid.instance());
		scale.turnOn();

		scale.addAnItem(heavyItem);
		scale.register(new ElectronicScaleListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
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

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				found++;
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				fail();
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				found++;
			}
		});
		scale.removeAnItem(heavyItem);
		scale.addAnItem(normalItem);
		scale.addAnItem(lightItem);
		scale.removeAnItem(normalItem);
		assertEquals(lightItem.getMass().inMicrograms().doubleValue(),
			scale.getCurrentMassOnTheScale().inMicrograms().doubleValue(), scale.getSensitivityLimit().inMicrograms().doubleValue());
		assertEquals(3, found);
	}

	@Test
	public void testAddThenRemove3() {
		scale.addAnItem(lightItem);
		scale.addAnItem(heavyItem);
		scale.register(new ElectronicScaleListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
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

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				found++;
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				fail();
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				found++;
			}
		});
		scale.removeAnItem(lightItem);
		assertEquals(0, found);
	}

	@Test(expected = NoPowerException.class)
	public void testGetWeightWhileTurnedOff() throws OverloadedDevice {
		scale.turnOff();
		scale.getCurrentMassOnTheScale();
	}

	@Test(expected = NoPowerException.class)
	public void testAddItemWhileTurnedOff() throws OverloadedDevice {
		scale.turnOff();
		scale.addAnItem(null);
	}

	@Test(expected = NoPowerException.class)
	public void testRemoveWhileTurnedOff() throws OverloadedDevice {
		scale.turnOff();
		scale.removeAnItem(null);
	}
}
