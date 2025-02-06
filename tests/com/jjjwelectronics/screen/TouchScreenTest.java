package com.jjjwelectronics.screen;

import static org.junit.Assert.assertEquals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.screen.TouchScreenBronze;
import com.jjjwelectronics.screen.TouchScreenListener;

import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class TouchScreenTest {
	private TouchScreenBronze screen;
	private volatile int found;

	@Before
	public void setup() {
		screen = new TouchScreenBronze();
		screen.register(new TouchScreenListener() {
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
		});
		screen.deregister(null);
		screen.deregisterAll();
		found = 0;

		screen.plugIn(PowerGrid.instance());
		screen.turnOn();

		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		screen.disable();
		screen.enable();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testFrame() {
		JFrame f = screen.getFrame();
		JButton foo = new JButton("foo");
		foo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				found++;
			}
		});

		f.add(foo);

		screen.setVisible(false);
		foo.doClick();

		assertEquals(1, found);
	}

	@Test(expected = NoPowerException.class)
	public void testSetVisibleWithoutTurningOn() {
		screen = new TouchScreenBronze();

		screen.plugIn(PowerGrid.instance());
		screen.turnOff();

		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		screen.setVisible(true);
	}

	@Test
	public void testSetInvisibleWithoutTurningOn() {
		screen = new TouchScreenBronze();
		screen.plugIn(PowerGrid.instance());
		screen.turnOn();

		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		screen.setVisible(false);
	}

	@Test
	public void testSetInvisibleWithTurningOn() {
		screen = new TouchScreenBronze();

		screen.plugIn(PowerGrid.instance());
		screen.turnOn();

		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		screen.setVisible(false);
	}

	@Test
	public void testSetVisibleWithTurningOn() {
		screen = new TouchScreenBronze();

		screen.plugIn(PowerGrid.instance());
		screen.turnOn();

		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		screen.setVisible(true);
	}

	// Note that this is not a proper automated test. An automated test does not
	// force user interaction. Trust me: clicking repeatedly on buttons is tedious
	// and error-prone. When you suddenly discover a bug on your hundredth attempt,
	// what did you do? Who knows, since you stopped paying attention.
	// <p>
	// Note: This is only a demo.
	// </p>
	// <p>
	// Look at FrameDemo2 for a more detailed example of a standalone version.
	// @Test
	// public void testFrameManual() {
	// PowerGrid.engageUninterruptiblePowerSource();
	// final JFrame frame = screen.getFrame();
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.setPreferredSize(new Dimension(500, 500));
	// screen.setVisible(true);
	// JButton foo = new JButton("Press to Close");
	// foo.setPreferredSize(new Dimension(200, 100));
	// foo.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// found++;
	// frame.dispose();
	// }
	// });
	//
	// frame.getContentPane().add(foo);
	// frame.pack();
	// }
	// });
	//
	// // This loop is only needed to prevent the JUnit runner from closing the
	// window
	// // before you have a chance to interact with it. If you look at FrameDemo2,
	// // which gets run as a standalone application, you will see that this is not
	// // necessary.
	// while(found < 1)
	// ;
	//
	// assertEquals(1, found);
	// }
}
