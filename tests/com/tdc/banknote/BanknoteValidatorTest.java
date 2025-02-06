package com.tdc.banknote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.StandardSinkStub;
import com.tdc.StandardSourceStub;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class BanknoteValidatorTest {
	private BanknoteValidator validator;
	private Currency currency;
	private Banknote banknote;

	@Before
	public void setup() {
		currency = Currency.getInstance(Locale.CANADA);
		banknote = new Banknote(currency, BigDecimal.ONE);
		validator = new BanknoteValidator(currency, new BigDecimal[] { BigDecimal.ONE });
		found = 0;
		retry = false;
		validator.connect(PowerGrid.instance());
		validator.activate();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();
	}

	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testRejectInvalidBanknote() throws DisabledException, CashOverloadException {
		banknote = new Banknote(currency, BigDecimal.valueOf(2));
		validator.source = new StandardSourceStub<Banknote>(validator) {
			@Override
			public void reject(Banknote banknote) {
				found++;
			}
		};
		validator.receive(banknote);

		assertEquals(1, found);
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		validator.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate() {
		new BanknoteValidator(null, new BigDecimal[] { BigDecimal.ONE });
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate2() {
		new BanknoteValidator(currency, null);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate3() {
		new BanknoteValidator(currency, new BigDecimal[0]);
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate4() {
		new BanknoteValidator(currency, new BigDecimal[] { BigDecimal.valueOf(-1) });
	}

	@Test(expected = SimulationException.class)
	public void testBadCreate5() {
		new BanknoteValidator(currency, new BigDecimal[] { BigDecimal.ONE, BigDecimal.ONE });
	}

	@Test(expected = NullPointerException.class)
	public void testBadAccept2() throws DisabledException, CashOverloadException {
		validator.receive(banknote);
	}

	@Test
	public void testAccept() throws DisabledException, CashOverloadException, NoCashAvailableException {
		validator.attach(new BanknoteValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal value) {
				found++;
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				retry = true;
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
		StandardSourceStub<Banknote> source = new StandardSourceStub<>(validator);
		validator.source = source;
		validator.sink = new StandardSinkStub<>(true);

		do {
			retry = false;
			source.receive(banknote);
			if(source.rejected)
				source.rejected = false;
			;
		}
		while(retry);

		assertEquals(1, found);
	}

	@Test
	public void testAcceptFalseNegative() throws DisabledException, CashOverloadException, NoCashAvailableException {
		validator.attach(new BanknoteValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal value) {
				retry = true;
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				found++;
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
		StandardSourceStub<Banknote> source = new StandardSourceStub<>(validator);
		validator.source = source;
		validator.sink = new StandardSinkStub<Banknote>(true);

		do {
			retry = false;
			source.receive(banknote);
			if(!retry) {
				if(source.rejected)
					source.rejected = false;
			}
		}
		while(retry);

		assertEquals(1, found);
	}

	private int found;

	private boolean retry;

	@Test
	public void testBadCurrency() throws DisabledException, CashOverloadException, NoCashAvailableException {
		StandardSourceStub<Banknote> source = new StandardSourceStub<>(validator);
		validator.source = source;
		validator.sink = new StandardSinkStub<>(true);
		banknote = new Banknote(Currency.getInstance(Locale.US), BigDecimal.ONE);
		validator.attach(new BanknoteValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal value) {
				fail();
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				found++;
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

		source.receive(banknote);

		assertEquals(1, found);
		assertTrue(source.rejected);
	}

	@Test
	public void testBadDenomination() throws DisabledException, CashOverloadException {
		StandardSourceStub<Banknote> source = new StandardSourceStub<>(validator);
		validator.source = source;
		validator.sink = new StandardSinkStub<>(true);
		banknote = new Banknote(currency, BigDecimal.valueOf(2));
		validator.attach(new BanknoteValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal value) {
				fail();
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				found++;
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

		validator.receive(banknote);

		assertEquals(1, found);
		assertTrue(source.rejected);
	}

	@Test(expected = DisabledException.class)
	public void testDisableAccept() throws DisabledException, CashOverloadException {
		validator.disable();
		validator.receive(null);
	}

	@Test
	public void testInvalidAccept() throws DisabledException, CashOverloadException, NoCashAvailableException {
		StandardSourceStub<Banknote> source = new StandardSourceStub<>(validator);
		validator.source = source;
		validator.sink = new StandardSinkStub<>(true);
		banknote = new Banknote(currency, BigDecimal.valueOf(2));
		validator.attach(new BanknoteValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal value) {
				fail();
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				found++;
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

		source.receive(banknote);
		assertEquals(1, found);
		assertTrue(source.rejected);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptBadSink() throws DisabledException, CashOverloadException {
		StandardSourceStub<Banknote> source = new StandardSourceStub<Banknote>(validator) {
			@Override
			public void reject(Banknote thing) throws DisabledException, CashOverloadException {
				throw new CashOverloadException();
			}
		};
		StandardSinkStub<Banknote> sink = new StandardSinkStub<Banknote>(false) {
			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				fail();
			}
		};
		validator.source = source;
		validator.sink = sink;
		validator.receive(banknote);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptBadSink2() throws DisabledException, CashOverloadException {
		StandardSourceStub<Banknote> source = new StandardSourceStub<Banknote>(validator) {
			@Override
			public void reject(Banknote thing) throws DisabledException, CashOverloadException {
				// It will get emitted sometimes, so ignore it.
			}
		};
		StandardSinkStub<Banknote> sink = new StandardSinkStub<Banknote>(true) {
			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				throw new CashOverloadException();
			}
		};

		validator.source = source;
		validator.sink = sink;

		for(int i = 0; i < 10000; i++)
			validator.receive(banknote);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptBadSink3() throws DisabledException, CashOverloadException {
		banknote = new Banknote(currency, BigDecimal.TEN);
		StandardSourceStub<Banknote> source = new StandardSourceStub<Banknote>(validator) {
			@Override
			public void reject(Banknote thing) throws DisabledException, CashOverloadException {
				throw new CashOverloadException();
			}
		};
		StandardSinkStub<Banknote> sink = new StandardSinkStub<Banknote>(false) {
			@Override
			public boolean hasSpace() {
				return false;
			}

			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				fail();
			}
		};
		validator.source = source;
		validator.sink = sink;
		validator.receive(banknote);
	}

	@Test
	public void testAcceptBadSink4() throws DisabledException, CashOverloadException {
		banknote = new Banknote(currency, BigDecimal.ONE);
		StandardSourceStub<Banknote> source = new StandardSourceStub<Banknote>(validator);
		StandardSinkStub<Banknote> sink = new StandardSinkStub<Banknote>(false) {
			@Override
			public void receive(Banknote thing) throws CashOverloadException, DisabledException {
				fail();
			}
		};
		validator.source = source;
		validator.sink = sink;
		validator.receive(banknote);
	}

	@Test(expected = ComponentFailure.class)
	public void testReject() {
		validator.reject(null);
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException {
		validator.disactivate();
		validator.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException {
		validator.disactivate();
		validator.hasSpace();
	}

	@Test
	public void testHasSpaceWithPower() throws CashOverloadException, DisabledException {
		validator.activate();
		validator.hasSpace();
	}

	@Test(expected = NoPowerException.class)
	public void testRejectWithoutPower() throws CashOverloadException, DisabledException {
		validator.disactivate();
		validator.reject(null);
	}
}
