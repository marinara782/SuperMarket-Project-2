package com.jjjwelectronics.bag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class ReusableBagDispenserBronzeTest {
	ReusableBagDispenser dispenser;
	ReusableBag bag;
	int found;
	int count;

	@Before
	public void setup() {
		found = 0;
		count = 0;
		bag = new ReusableBag();
		dispenser = new ReusableBagDispenser(2);
		assertEquals(2, dispenser.getCapacity());
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		dispenser.plugIn(PowerGrid.instance());
		dispenser.turnOn();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		PowerGrid.instance().forcePowerRestore();
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadCreate() {
		new ReusableBagDispenser(0);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testBadLoad() throws OverloadedDevice {
		dispenser.load((ReusableBag[])null);
	}

	@Test(expected = NoPowerException.class)
	public void testLoadNoPower() throws OverloadedDevice {
		dispenser.turnOff();
		dispenser.load(bag);
	}

	@Test(expected = NoPowerException.class)
	public void testUnloadNoPower() throws OverloadedDevice {
		dispenser.turnOff();
		dispenser.unload();
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testQuantitytRemaining() {
		dispenser.getQuantityRemaining();	
	}

	@Test
	public void testLoadOneBag() throws OverloadedDevice {
		dispenser.load(bag);
	}

	@Test
	public void testLoadOneBagWithListener() throws OverloadedDevice {
		dispenser.register(new ReusableBagDispenserListener() {
			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void theDispenserIsOutOfBags() {
				fail();
			}

			@Override
			public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
				found++;
			}

			@Override
			public void aBagHasBeenDispensedByTheDispenser() {
				fail();
			}
		});
		dispenser.load(bag);
		assertEquals(1, found);
	}

	@Test
	public void testLoadTwoBagsWithListener() throws OverloadedDevice {
		dispenser.register(new ReusableBagDispenserListener() {
			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void theDispenserIsOutOfBags() {
				fail();
			}

			@Override
			public void bagsHaveBeenLoadedIntoTheDispenser(int localCount) {
				found++;
				count = localCount;
			}

			@Override
			public void aBagHasBeenDispensedByTheDispenser() {
				fail();
			}
		});
		dispenser.load(bag, bag);
		assertEquals(1, found);
		assertEquals(2, count);
	}

	@Test(expected = OverloadedDevice.class)
	public void testLoadThreeBags() throws OverloadedDevice {
		dispenser.load(bag, bag, bag);
	}

	@Test
	public void testUnload() throws OverloadedDevice {
		dispenser.load(bag);
		ReusableBag[] bags = dispenser.unload();
		assertEquals(1, bags.length);
	}

	@Test
	public void testUnloadWithListener() throws OverloadedDevice {
		dispenser.load(bag);
		dispenser.register(new ReusableBagDispenserListener() {
			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void theDispenserIsOutOfBags() {
				found++;
			}

			@Override
			public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
				fail();
			}

			@Override
			public void aBagHasBeenDispensedByTheDispenser() {
				fail();
			}
		});
		ReusableBag[] bags = dispenser.unload();
		assertEquals(1, bags.length);
		assertEquals(1, found);
	}

	@Test(expected = NoPowerException.class)
	public void testDispenseNoPower() throws EmptyDevice {
		dispenser.turnOff();
		dispenser.dispense();
	}

	@Test(expected = EmptyDevice.class)
	public void testDispenseWhenEmpty() throws EmptyDevice {
		dispenser.dispense();
	}

	@Test
	public void testDispenseWithOneLeft() throws EmptyDevice, OverloadedDevice {
		dispenser.load(bag, bag);
		dispenser.register(new ReusableBagDispenserListener() {
			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void theDispenserIsOutOfBags() {
				fail();
			}

			@Override
			public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
				fail();
			}

			@Override
			public void aBagHasBeenDispensedByTheDispenser() {
				found++;
			}
		});
		dispenser.dispense();
		assertEquals(1, found);
	}

	@Test
	public void testDispenseWithNoneLeft() throws EmptyDevice, OverloadedDevice {
		dispenser.load(bag);
		dispenser.register(new ReusableBagDispenserListener() {
			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				fail();
			}

			@Override
			public void theDispenserIsOutOfBags() {
				found++;
			}

			@Override
			public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
				fail();
			}

			@Override
			public void aBagHasBeenDispensedByTheDispenser() {
				found++;
			}
		});
		dispenser.dispense();
		assertEquals(2, found);
	}
}
