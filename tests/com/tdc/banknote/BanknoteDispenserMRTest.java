package com.tdc.banknote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.StandardSinkStub;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserObserver;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.IBanknoteDispenser;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BanknoteDispenserMRTest {
	private BanknoteDispenserBronze dispenser;
	private Currency currency = Currency.getInstance(Locale.CANADA);

	@Before
	public void setup() {
		dispenser = new BanknoteDispenserBronze();
		good = 0;
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testCreate() {
		assertEquals(1000, dispenser.getCapacity());
		assertEquals(0, dispenser.size());
	}

	@Test(expected = SimulationException.class)
	public void testBadLoad2() throws CashOverloadException, DisabledException {
		dispenser.load((Banknote)null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new AbstractBanknoteDispenser(0) {};
	}

	@Test(expected = NoCashAvailableException.class)
	public void testBadEmit() throws NoCashAvailableException, DisabledException, CashOverloadException {
		dispenser.emit();
	}

	void found() {
		good++;
	}

	int good;

	@Test
	public void testLoad() throws SimulationException, CashOverloadException {
		dispenser.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				found();
			}

			@Override
			public void moneyFull(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknotesEmpty(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		dispenser.load(banknote);
		assertEquals(1, good);
	}

	@Test(expected = CashOverloadException.class)
	public void testBadLoad() throws SimulationException, CashOverloadException {
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		Banknote[] notes = new Banknote[1001];
		
		for(int i = 0; i < 1001; i++)
			notes[i] = banknote;

		dispenser.load(notes);
	}

	@Test
	public void testUnload() throws SimulationException, CashOverloadException {
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		dispenser.load(banknote);
		dispenser.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				found();
			}

			@Override
			public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void moneyFull(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknotesEmpty(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});
		good = 0;
		List<Banknote> banknotes = dispenser.unload();
		assertEquals(1, good);
		assertEquals(1, banknotes.size());
		assertEquals(banknote, banknotes.get(0));
	}

	@Test
	public void testEject() throws DisabledException, NoCashAvailableException, SimulationException, CashOverloadException {
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		dispenser.load(banknote);
		dispenser.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void moneyFull(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknotesEmpty(IBanknoteDispenser dispenser) {
				found();
			}

			@Override
			public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
				found();
			}

			@Override
			public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});

		dispenser.sink = new StandardSinkStub<>(true);

		dispenser.emit();
		assertEquals(2, good);
	}

	@Test(expected = DisabledException.class)
	public void testEjectWhileDisabled() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser.disable();
		dispenser.emit();
	}

	@Test
	public void testEjectWithoutEmptying() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser = new BanknoteDispenserBronze();
		dispenser.connect(PowerGrid.instance());
		dispenser.activate();
		Banknote banknote = new Banknote(currency, BigDecimal.ONE);
		dispenser.load(banknote, banknote);
		BanknoteInsertionSlot bs = new BanknoteInsertionSlot();
		bs.connect(PowerGrid.instance());
		bs.activate();
		dispenser.sink = new StandardSinkStub<>(true);
		dispenser.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void moneyFull(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknotesEmpty(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
				found();
			}

			@Override
			public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}
		});

		dispenser.emit();
		assertEquals(1, dispenser.size());
	}

	@Test(expected = CashOverloadException.class)
	public void testBadChannel()
		throws CashOverloadException, SimulationException, DisabledException, NoCashAvailableException {
		dispenser.load(new Banknote(currency, BigDecimal.ONE));
		dispenser.sink = new StandardSinkStub<>(false);

		dispenser.emit();
	}

	@Test(expected = CashOverloadException.class)
	public void testBadChannel2()
		throws CashOverloadException, SimulationException, DisabledException, NoCashAvailableException {
		dispenser.load(new Banknote(currency, BigDecimal.ONE));
		dispenser.sink = new StandardSinkStub<Banknote>(true) {
			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				throw new CashOverloadException();
			}
		};

		dispenser.emit();
	}

	@Test(expected = NoPowerException.class)
	public void testSizeWithoutPower() {
		dispenser.disconnect();
		dispenser.size();
	}

	@Test(expected = NoPowerException.class)
	public void testLoadWithoutPower() throws CashOverloadException {
		dispenser.disconnect();
		dispenser.load((Banknote[])null);
	}

	@Test(expected = NoPowerException.class)
	public void testUnloadWithoutPower() throws CashOverloadException {
		dispenser.disconnect();
		dispenser.unload();
	}

	@Test(expected = NoPowerException.class)
	public void testEmitWithoutPower() throws CashOverloadException, NoCashAvailableException, DisabledException {
		dispenser.disconnect();
		dispenser.emit();
	}
}
