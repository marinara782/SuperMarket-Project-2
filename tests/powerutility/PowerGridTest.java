package powerutility;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class PowerGridTest {
	// For use as a Java application: needed as JUnit captures the exceptions
	// otherwise
	public static void main(String[] args) {
		PowerGridTest t = new PowerGridTest();

		t.setup();
		try {
			t.testForPowerFailure();
			fail();
		}
		catch(NoPowerException e) {}
		t.teardown();

		t.setup();
		try {
			t.testForPowerSurge();
			fail();
		}
		catch(PowerSurge e) {}
		t.teardown();

		t.setup();
		try {
			t.testForPowerSurge2();
			fail();
		}
		catch(PowerSurge e) {}
		t.teardown();

		t.setup();
		try {
			t.testForPowerOutage();
			fail();
		}
		catch(NoPowerException e) {}
		t.teardown();

		t.setup();
		t.testForPowerRestore();
		t.teardown();
	}

	@Before
	public void setup() {
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
		PowerGrid.instance().forcePowerRestore();
	}

	@Test(expected = NoPowerException.class)
	public void testForPowerFailure() {
		PowerGrid.disconnect();

		PowerGrid.instance().hasPower();
	}

	@Test(expected = PowerSurge.class)
	public void testForPowerSurge2() {
		PowerGrid.engageFaultyPowerSource();

		PowerGrid.instance().hasPower();
	}

	@Test(expected = PowerSurge.class)
	public void testForPowerSurge() {
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerSurge();
		PowerGrid.instance().hasPower();
	}

	@Test
	public void testForPowerRestore() {
		PowerGrid pg = PowerGrid.instance();
		PowerGrid.engageUninterruptiblePowerSource();
		pg.forcePowerRestore();
		assertTrue(pg.hasPower());
	}

	@Test(expected = NoPowerException.class)
	public void testForPowerOutage() {
		PowerGrid pg = PowerGrid.instance();
		PowerGrid.engageUninterruptiblePowerSource();
		pg.forcePowerOutage();
		pg.hasPower();
	}
}
