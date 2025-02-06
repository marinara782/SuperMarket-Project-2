package com.jjjwelectronics.printer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterListener;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class ReceiptPrinterBronzeTest {
	private ReceiptPrinterBronze printer;
	private int found;

	@Before
	public void setup() throws OverloadedDevice {
		printer = new ReceiptPrinterBronze();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addPaper(2);
		printer.addInk(ReceiptPrinterBronze.CHARACTERS_PER_LINE);
		found = 0;
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		PowerGrid.instance().forcePowerRestore();
	}	

	@Test(expected = UnsupportedOperationException.class)
	public void testPaperRemaining() {
		printer.paperRemaining();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInkRemaining() {
		printer.inkRemaining();
	}
	
	@Test
	public void testPrintFullLine() throws EmptyDevice, OverloadedDevice {
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				found++;
			}
			
			@Override
			public void thePrinterHasLowInk() {
				fail();
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}
		});

		for(int i = 0; i < ReceiptPrinterBronze.CHARACTERS_PER_LINE; i++)
			printer.print('a');

		assertEquals(1, found);
	}

	@Test
	public void testPrintWhitespace() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addPaper(1);
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				found++;
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});

		printer.print(' ');
		assertEquals(1, found);
	}

	@Test(expected = EmptyDevice.class)
	public void testNoPaper() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addInk(1);
		printer.print(' ');
	}

	@Test(expected = EmptyDevice.class)
	public void testNoPaper2() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addInk(1);
		printer.print(' ');
	}

	@Test(expected = OverloadedDevice.class)
	public void testLineTooLong() throws OverloadedDevice, EmptyDevice {
		for(int i = 0; i < ReceiptPrinterBronze.CHARACTERS_PER_LINE; i++)
			printer.print(' ');

		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				fail();
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});
		printer.print(' ');
	}

	@Test
	public void testTwoLines() throws OverloadedDevice, EmptyDevice {
		for(int i = 0; i < ReceiptPrinterBronze.CHARACTERS_PER_LINE; i++)
			printer.print('a');

		printer.print('\n');
		printer.addInk(1);

		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				found++;
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});
		printer.print(' ');
		printer.print('a');
		assertEquals(1, found);
	}

	@Test
	public void testTab() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				fail();
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}
			
			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});

		printer.print('\t');
	}

	@Test
	public void testPrintAndRunOutOfPaper() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addPaper(1);
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				found++;
			}

			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}
			
			@Override
			public void thePrinterIsOutOfInk() {
				found++;
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});

		printer.print('\n');
		assertEquals(2, found);
	}

	@Test(expected = EmptyDevice.class)
	public void testPrintWithoutInk() throws OverloadedDevice, EmptyDevice {
		printer = new ReceiptPrinterBronze();
		printer.plugIn(PowerGrid.instance());
		printer.turnOn();
		printer.addPaper(1);
		printer.print('a');
	}

	// @Test
	@Test(expected = SimulationException.class)
	public void testRemoveWithoutCutting() {
		assertEquals(null, printer.removeReceipt());
	}

	@Test
	public void testRemove() {
		printer.cutPaper();
		assertEquals("", printer.removeReceipt());
		try {
			assertEquals(null, printer.removeReceipt());
		}
		catch(SimulationException e) {
			// good
			return;
		}
		fail();
	}

	@Test(expected = SimulationException.class)
	public void testRemove3() {
		printer = new ReceiptPrinterBronze();
		printer.removeReceipt();
	}

	@Test(expected = SimulationException.class)
	public void testBadAddInk() throws OverloadedDevice, EmptyDevice {
		printer.addInk(-1);
	}

	@Test
	public void testAddNoInk() throws OverloadedDevice, EmptyDevice {
		printer.addInk(0);
	}

	@Test(expected = OverloadedDevice.class)
	public void testAddTooMuchInk() throws OverloadedDevice, EmptyDevice {
		printer.addInk(ReceiptPrinterBronze.MAXIMUM_INK + 1);
	}

	@Test(expected = SimulationException.class)
	public void testRemovePaper() throws OverloadedDevice, EmptyDevice {
		printer.addPaper(-1);
	}

	@Test(expected = OverloadedDevice.class)
	public void testAddTooMuchPaper() throws OverloadedDevice, EmptyDevice {
		printer.addPaper(ReceiptPrinterBronze.MAXIMUM_PAPER + 1);
	}

	@Test
	public void testAddNoPaper() throws OverloadedDevice, EmptyDevice {
		printer.addPaper(0);
	}

	@Test
	public void testAddInk() throws OverloadedDevice, EmptyDevice {
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}

			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				fail();
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				found++;
			}

			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});
		printer.addInk(1);
		assertEquals(1, found);
	}

	@Test
	public void testAddPaper() throws OverloadedDevice, EmptyDevice {
		printer.register(new ReceiptPrinterListener() {
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
			public void paperHasBeenAddedToThePrinter() {
				found++;
			}

			@Override
			public void thePrinterIsOutOfPaper() {
				fail();
			}

			@Override
			public void thePrinterHasLowPaper() {
				fail();
			}

			@Override
			public void thePrinterIsOutOfInk() {
				fail();
			}

			@Override
			public void inkHasBeenAddedToThePrinter() {
				fail();
			}

			@Override
			public void thePrinterHasLowInk() {
				fail();
			}
		});
		printer.addPaper(1);
		assertEquals(1, found);
	}

	@Test(expected = NoPowerException.class)
	public void testPrintWhileTurnedOff() throws EmptyDevice, OverloadedDevice {
		printer = new ReceiptPrinterBronze();
		printer.print(' ');
	}

	@Test(expected = NoPowerException.class)
	public void testCutPaperWhileTurnedOff() throws EmptyDevice, OverloadedDevice {
		printer = new ReceiptPrinterBronze();
		printer.cutPaper();
	}

	@Test(expected = NoPowerException.class)
	public void testAddInkWhileTurnedOff() throws EmptyDevice, OverloadedDevice {
		printer = new ReceiptPrinterBronze();
		printer.addInk(0);
	}

	@Test(expected = NoPowerException.class)
	public void testAddPaperWhileTurnedOff() throws EmptyDevice, OverloadedDevice {
		printer = new ReceiptPrinterBronze();
		printer.addPaper(0);
	}
}
