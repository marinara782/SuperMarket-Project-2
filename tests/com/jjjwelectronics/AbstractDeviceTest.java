package com.jjjwelectronics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;
import powerutility.PowerSurge;

@SuppressWarnings("javadoc")
public class AbstractDeviceTest {
	AbstractDevice<IDeviceListener> device;

	@Before
	public void setup() {
		device = new AbstractDevice<IDeviceListener>() {};
		found = 0;
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testPluginInNull() {
		device.plugIn(null);
	}

	@Test
	public void testPlugInNoPower() {

	}

	@Test(expected = NoPowerException.class)
	public void testPluginInFailingGrid() {
		PowerGrid.instance().forcePowerOutage();
		device.plugIn(PowerGrid.instance());
		device.disable();
	}

	@Test(expected = NoPowerException.class)
	public void testEnableNoPower() {
		device.turnOff();
		device.enable();
	}

	@Test(expected = NoPowerException.class)
	public void testDisableNoPower() {
		device.turnOff();
		device.disable();
	}

	@Test(expected = InvalidStateSimulationException.class)
	public void testTurnOnUnplugged() {
		device.unplug();
		device.turnOn();
	}

	@Test(expected = NoPowerException.class)
	public void testIsDisabledNoPower() {
		device.turnOff();
		device.isDisabled();
	}

	@Test
	public void testListeners() {
		assertEquals(0, device.listeners().size());
		device.register(new IDeviceListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
		});
		assertEquals(1, device.listeners().size());
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testRegisterNull() {
		device.register(null);
	}

	private volatile int found;

	@Test
	public void testDisableWithListener() {
		device.register(new IDeviceListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				found++;
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
		});
		device.disable();
		assertEquals(1, found);
	}

	@Test
	public void testEnableWithListener() {
		device.register(new IDeviceListener() {
			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				found++;
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
		});
		device.enable();
		assertEquals(1, found);
	}

	@Test
	public void testPoweredUp1() {
		PowerGrid.instance().forcePowerRestore();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		assertTrue(device.isPoweredUp());
	}

	@Test
	public void testPoweredUp2() {
		PowerGrid.instance().forcePowerRestore();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		device.turnOff();
		assertFalse(device.isPoweredUp());
	}

	@Test(expected = NoPowerException.class)
	public void testPoweredUp3() {
		PowerGrid.instance().forcePowerOutage();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		device.isPoweredUp();
	}

	@Test(expected = PowerSurge.class)
	public void testPoweredUp4() {
		PowerGrid.engageFaultyPowerSource();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		device.isPoweredUp();
	}

	@Test
	public void testPoweredUp5() {
		PowerGrid.engageUninterruptiblePowerSource();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		device.isPoweredUp();
	}

	@Test
	public void testPoweredUp6() {
		device.unplug();
		assertFalse(device.isPoweredUp());
	}

	@Test(expected = NoPowerException.class)
	public void testPoweredUp6A() {
		PowerGrid.disconnect();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		assertFalse(device.isPoweredUp());
	}

	@Test(expected = PowerSurge.class)
	public void testPoweredUp7() {
		PowerGrid.instance().forcePowerSurge();
		device.plugIn(PowerGrid.instance());
		device.turnOn();
		device.isPoweredUp();
	}

	@Test
	public void testReTurnOff() {
		device.plugIn(PowerGrid.instance());
		device.turnOff();
		device.turnOff();
		assertFalse(device.isPoweredUp());
	}
}
