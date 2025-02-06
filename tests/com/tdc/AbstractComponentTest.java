package com.tdc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class AbstractComponentTest {
	AbstractComponent<IComponentObserver> device;

	@Before
	public void setup() {
		device = new AbstractComponent<>() {};
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testConnectToNull() {
		device.connect(null);
	}

	@Test
	public void testHasPower() {
		assertFalse(device.hasPower());

		device.connect(PowerGrid.instance());
		assertTrue(device.hasPower());

		PowerGrid.instance().forcePowerOutage();
		try {
			assertFalse(device.hasPower());
		}
		catch(NoPowerException e) {}
	}

	@Test
	public void testPlugIn() {
		assertEquals(false, device.isConnected());
		assertEquals(false, device.isActivated());

		device.connect(PowerGrid.instance());
		assertEquals(true, device.isConnected());
		assertEquals(false, device.isActivated());

		device.disconnect();
		assertEquals(false, device.isConnected());
		assertEquals(false, device.isActivated());

		device.disactivate();
		assertEquals(false, device.isConnected());
		assertEquals(false, device.isActivated());

		device.activate();
		assertEquals(false, device.isConnected());
		assertEquals(true, device.isActivated());

		device.connect(PowerGrid.instance());
		assertEquals(true, device.isConnected());
		assertEquals(true, device.isActivated());

		device.activate();
		assertEquals(true, device.isConnected());
		assertEquals(true, device.isActivated());

		PowerGrid.instance().forcePowerOutage();

		assertEquals(true, device.isConnected());
		assertEquals(true, device.isActivated());
	}

	@Test
	public void testDetach() {
		device.detach(null);
		device.detach(new IComponentObserver() {
			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {}

			@Override
			public void enabled(IComponent<? extends IComponentObserver> component) {}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> component) {}
		});
	}

	@Test
	public void testRePlugIn() {
		device.connect(PowerGrid.instance());
		device.connect(PowerGrid.instance());

		assertTrue(device.isConnected());
	}

	@Test(expected = NoPowerException.class)
	public void testEnableNoPower() {
		device.disconnect();
		device.enable();
	}

	@Test(expected = NoPowerException.class)
	public void testDisableNoPower() {
		device.disconnect();
		device.disable();
	}

	@Test(expected = NoPowerException.class)
	public void testIsDisabledNoPower() {
		device.disconnect();
		device.isDisabled();
	}
}
