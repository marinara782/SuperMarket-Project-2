package com.jjjwelectronics.keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.keyboard.AbstractKeyboard;
import com.jjjwelectronics.keyboard.Key;
import com.jjjwelectronics.keyboard.KeyListener;
import com.jjjwelectronics.keyboard.KeyboardListener;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class KeyboardTest {
	private AbstractKeyboard keyboard;
	private int found;

	@Before
	public void setup() {
		keyboard = new USKeyboardQWERTY();
		found = 0;
		keyboard.plugIn(PowerGrid.instance());
		keyboard.turnOn();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.engageUninterruptiblePowerSource();
	}
	
	@Test(expected = NullPointerSimulationException.class)
	public void testBadCreate() {
		new AbstractKeyboard(null) {};
	}

	@Test
	public void testPressKey() throws DisabledDevice {
		keyboard.getKey("A").press();
	}

	@Test
	public void testPressKeyWithListener() throws DisabledDevice {
		keyboard.register(new KeyboardListener() {
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
			public void aKeyHasBeenPressed(String label) {
				found++;
			}

			@Override
			public void aKeyHasBeenReleased(String label) {
				fail();
			}
		});
		keyboard.getKey("A").press();
		assertEquals(1, found);
	}

	@Test(expected = NullPointerException.class)
	public void testBadPressKey() throws DisabledDevice {
		keyboard.getKey("a").press();
	}

	@Test
	public void testType2() throws DisabledDevice {
		keyboard.register(new KeyboardListener() {
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
			public void aKeyHasBeenPressed(String label) {
				found++;
			}

			@Override
			public void aKeyHasBeenReleased(String label) {
				found++;
			}
		});
		keyboard.getKey("A").press();
		keyboard.getKey("B").press();
		keyboard.getKey("A").release();
		keyboard.getKey("B").release();

		assertEquals(4, found);
	}

	@Test
	public void testDisableSynched() {
		Key key = keyboard.keys().values().iterator().next();
		keyboard.disable();
		assertEquals(true, key.isDisabled());
		keyboard.enable();
		assertEquals(false, key.isDisabled());
	}

	@Test
	public void testDeregisterFromKey() {
		Key key = keyboard.keys().values().iterator().next();
		key.register(new KeyListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aKeyHasBeenReleased(Key k) {}

			@Override
			public void aKeyHasBeenPressed(Key k) {}
		});
		int size = key.listeners().size();
		assertEquals(false, key.listeners().isEmpty());
		key.deregisterAll();
		assertEquals(size - 1, key.listeners().size());
	}

	@Test
	public void testDeregisterFromKey2() {
		Key key = keyboard.keys().values().iterator().next();
		KeyListener listener = new KeyListener() {
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// ignore
			}

			@Override
			public void aKeyHasBeenReleased(Key k) {}

			@Override
			public void aKeyHasBeenPressed(Key k) {}
		};
		key.register(listener);
		int size = key.listeners().size();
		assertEquals(false, key.listeners().isEmpty());
		key.deregister(listener);
		assertEquals(size - 1, key.listeners().size());
	}

	@Test
	public void testDeregisterFromKey3() {
		Key key = keyboard.keys().values().iterator().next();
		KeyListener listener = key.listeners().get(0);
		int size = key.listeners().size();
		key.deregister(listener);
		assertEquals(size, key.listeners().size());
	}

	@Test(expected = DisabledDevice.class)
	public void testPressKeyWhenDisabled() throws DisabledDevice {
		keyboard.disable();
		Key key = keyboard.keys().values().iterator().next();
		key.press();
	}

	@Test(expected = DisabledDevice.class)
	public void testReleaseKeyWhenDisabled() throws DisabledDevice {
		keyboard.disable();
		Key key = keyboard.keys().values().iterator().next();
		key.release();
	}

	@Test(expected = DisabledDevice.class)
	public void testReleaseKeyWhenDisabled2() throws DisabledDevice {
		Key key = keyboard.keys().values().iterator().next();
		try {
			key.press();
		}
		catch(DisabledDevice e) {
			fail();
		}
		keyboard.disable();
		key.release();
	}

	@Test
	public void testPressWhenPressed() throws DisabledDevice {
		Key key = keyboard.keys().values().iterator().next();
		key.press();
		assertEquals(true, key.isPressed());
		key.press();
	}

	@Test
	public void testReleaseWhenReleased() throws DisabledDevice {
		Key key = keyboard.keys().values().iterator().next();
		key.release();
		assertEquals(false, key.isPressed());
		key.release();
	}

	@Test
	public void testDisableOverridden() {
		Key key = keyboard.keys().values().iterator().next();
		key.disable();
		assertEquals(false, key.isDisabled());
	}

	@Test
	public void testEnableOverridden() {
		keyboard.disable();
		Key key = keyboard.keys().values().iterator().next();
		key.enable();
		assertEquals(true, key.isDisabled());
	}

	@Test
	public void testUnplug() {
		keyboard.unplug();

		for(Key key : keyboard.keys().values())
			assertEquals(false, key.isPluggedIn());

		assertEquals(false, keyboard.isPluggedIn());
	}

	@Test
	public void testPlugIn() {
		keyboard.plugIn(PowerGrid.instance());

		for(Key key : keyboard.keys().values())
			assertEquals(true, key.isPluggedIn());

		assertEquals(true, keyboard.isPluggedIn());
	}

	@Test
	public void testTurnOff() {
		keyboard.turnOff();

		for(Key key : keyboard.keys().values())
			assertEquals(false, key.isPoweredUp());

		assertEquals(false, keyboard.isPoweredUp());
	}

	@Test
	public void testTurnOffOverridden() {
		Key key = keyboard.keys().values().iterator().next();
		key.turnOff();
		assertEquals(true, key.isPoweredUp());
	}

	@Test
	public void testTurnOnOverridden() {
		keyboard.turnOff();
		Key key = keyboard.keys().values().iterator().next();
		key.turnOn();
		assertEquals(false, key.isPoweredUp());
	}

	@Test
	public void testReTurnOnOverridden() {
		Key key = keyboard.keys().values().iterator().next();
		key.turnOn();

		for(Key key2 : keyboard.keys().values())
			assertEquals(true, key2.isPoweredUp());

		assertEquals(true, keyboard.isPoweredUp());
	}

	@Test(expected = NoPowerException.class)
	public void testKeyPressWithoutPower() throws DisabledDevice {
		keyboard.turnOff();
		Key key = keyboard.keys().values().iterator().next();
		key.press();
	}

	@Test(expected = NoPowerException.class)
	public void testKeyReleaseWithoutPower() throws DisabledDevice {
		keyboard.turnOff();
		Key key = keyboard.keys().values().iterator().next();
		key.release();
	}

	@Test
	public void testKeyPlugInOverridden() throws DisabledDevice {
		keyboard.unplug();
		Key key = keyboard.keys().values().iterator().next();
		key.plugIn(PowerGrid.instance());

		assertEquals(false, key.isPluggedIn());
		assertEquals(false, key.isPoweredUp());
	}

	@Test
	public void testKeyUnplugOverridden() throws DisabledDevice {
		Key key = keyboard.keys().values().iterator().next();
		key.unplug();

		assertEquals(true, key.isPluggedIn());
		assertEquals(true, key.isPoweredUp());
	}
}
