package com.tdc.coin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.FailOnReceiveSinkStub;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.Sink;
import com.tdc.StandardSinkStub;
import com.tdc.ThrowOnReceiveSinkStub;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class CoinValidatorTest {
	private Currency currency = Currency.getInstance(Locale.CANADA);
	private CoinValidator validator;

	class FoundOnReceiveSinkStub extends StandardSinkStub<Coin> {
		public FoundOnReceiveSinkStub(boolean hasSpace) {
			super(hasSpace);
		}

		@Override
		public void receive(Coin thing) {
			found++;
		}
	}

	@Before
	public void setup() {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE));
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

	@Test(expected = NullPointerSimulationException.class)
	public void testBadCreateWithBuriedNullDenomination() {
		new CoinValidator(currency, Arrays.asList((BigDecimal)null));
	}

	@Test(expected = SimulationException.class)
	public void testBadAccept() throws CashOverloadException, DisabledException {
		validator.receive(null);
	}

	@Test(expected = SimulationException.class)
	public void testIllegalAccept2() throws CashOverloadException, DisabledException {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE));
		validator.connect(PowerGrid.instance());
		validator.activate();
		validator.receive(null);
	}

	@Test
	public void testCreate() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FoundOnReceiveSinkStub(true));
		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));

		do {
			validator.receive(coin);
		}
		while(found == 0);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testSetupWithNullOverflow() {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FoundOnReceiveSinkStub(true));
		validator.setup(new StandardSinkStub<>(true), map, null);
	}

	// @Test(expected = SimulationException.class)
	// public void testIllegalConnect() {
	// CoinTray stub = new CoinTray(1) {
	// @Override
	// public void receive(Coin coin) throws TooMuchCashException, DisabledException
	// {}
	//
	// @Override
	// public boolean hasSpace() {
	// return true;
	// }
	// };
	//
	// Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
	// map.put(BigDecimal.ONE, new UnidirectionalChannel<Coin>(null) {
	// public void deliver(Coin thing) throws TooMuchCashException,
	// DisabledException {
	// found++;
	// }
	//
	// public boolean hasSpace() {
	// return true;
	// }
	// });
	// validator.setup(new UnidirectionalChannel<>(stub), map, new
	// UnidirectionalChannel<>(null));
	// }

	@Test(expected = SimulationException.class)
	public void testBadCurrency() {
		new CoinValidator(null, Arrays.asList(BigDecimal.ONE));
	}

	@Test(expected = SimulationException.class)
	public void testBadDenomination() {
		new CoinValidator(currency, null);
	}

	@Test(expected = SimulationException.class)
	public void testBadDenomination2() {
		new CoinValidator(currency, Arrays.asList());
	}

	@Test(expected = SimulationException.class)
	public void testBadDenomination3() {
		new CoinValidator(currency, Arrays.asList(BigDecimal.valueOf(-1)));
	}

	@Test(expected = SimulationException.class)
	public void testBadDenomination4() {
		new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.ONE));
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect() {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<Coin>(true) {
			public void deliver(Coin thing) throws CashOverloadException, DisabledException {
				found++;
			}
		});
		validator.setup(null, map, null);
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect2() {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<Coin>(true) {
			public void deliver(Coin thing) throws CashOverloadException, DisabledException {
				found++;
			}
		});
		validator.setup(null, map, null);
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect3() {
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		validator.setup(sink, null, sink);
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect4() {
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, sink);
		map.put(BigDecimal.valueOf(2), sink);
		validator.setup(sink, map, sink);
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect5() {
		validator.setup(new StandardSinkStub<>(true), new HashMap<BigDecimal, Sink<Coin>>(),
			new StandardSinkStub<>(true));
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect6() {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.TEN));
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		map.put(BigDecimal.ONE, sink);
		map.put(BigDecimal.TEN, sink);
		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect6A() {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, null);
		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect7() {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.TEN));
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, sink);
		map.put(BigDecimal.valueOf(2), sink);
		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect8() {
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));
		validator.setup(sink, map, sink);
	}

	@Test(expected = SimulationException.class)
	public void testBadConnect9() {
		StandardSinkStub<Coin> sink = new StandardSinkStub<>(true);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, sink);
		validator.setup(sink, map, new StandardSinkStub<>(true));
	}

	private int found;

	@Test
	public void testBadCoin() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(Currency.getInstance(Locale.US), BigDecimal.ONE);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));
		validator.setup(new FoundOnReceiveSinkStub(true), map, new StandardSinkStub<>(true));

		validator.receive(coin);
		assertEquals(1, found);
	}

	@Test
	public void testRandomlyBadCoin() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<Coin>(true));
		validator.setup(new FoundOnReceiveSinkStub(true), map, new StandardSinkStub<>(true));

		for(int i = 0; i < 10000; i++)
			validator.receive(coin);

		assertTrue(found > 0);
	}

	@Test
	public void testRandomlyBadCoin2() throws DisabledException, CashOverloadException {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.TEN));
		validator.connect(PowerGrid.instance());
		validator.activate();

		Coin coin = new Coin(currency, BigDecimal.TEN);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FailOnReceiveSinkStub<>(true));
		map.put(BigDecimal.TEN, new StandardSinkStub<>(true));
		validator.setup(new FoundOnReceiveSinkStub(true), map, new StandardSinkStub<>(true));

		for(int i = 0; i < 10000; i++)
			validator.receive(coin);

		assertTrue(found > 0);
	}

	@Test
	public void testRandomlyBadCoin3() throws DisabledException, CashOverloadException {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.TEN));
		validator.connect(PowerGrid.instance());
		validator.activate();

		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));
		map.put(BigDecimal.TEN, new FailOnReceiveSinkStub<>(true));
		validator.setup(new FoundOnReceiveSinkStub(true), map, new StandardSinkStub<>(true));

		for(int i = 0; i < 10000; i++)
			validator.receive(coin);

		assertTrue(found > 0);
	}

	@Test
	public void testBadCoin2() throws DisabledException, CashOverloadException {
		validator = new CoinValidator(currency, Arrays.asList(BigDecimal.ONE, BigDecimal.TEN));
		validator.connect(PowerGrid.instance());
		validator.activate();

		Coin coin = new Coin(currency, BigDecimal.TEN);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));
		map.put(BigDecimal.TEN, new FailOnReceiveSinkStub<>(true));
		validator.setup(new FoundOnReceiveSinkStub(true), map, new StandardSinkStub<>(true));

		for(int i = 0; i < 10000; i++)
			try {
				validator.receive(coin);
				break;
			}
			catch(AssertionError e) {}
		
		assertTrue(found > 0);
	}

	@Test(expected = DisabledException.class)
	public void testAcceptWhenDisabled() throws DisabledException, CashOverloadException {
		validator.disable();
		validator.receive(null);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptWithBadSink() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new ThrowOnReceiveSinkStub<>(true));

		validator.setup(new ThrowOnReceiveSinkStub<>(true), map, new StandardSinkStub<>(true));
		validator.receive(coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptWithBadSink2() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FailOnReceiveSinkStub<>(false));

		validator.setup(new ThrowOnReceiveSinkStub<>(true), map, new ThrowOnReceiveSinkStub<>(true));
		validator.receive(coin);
	}

	@Test
	public void testAcceptWithGoodRejectSink() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FailOnReceiveSinkStub<>(false));

		validator.setup(new FoundOnReceiveSinkStub(true), map, new FoundOnReceiveSinkStub(true));

		validator.receive(coin);
		assertEquals(1, found);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptWithBadRejectSink() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FailOnReceiveSinkStub<>(false));

		validator.setup(new ThrowOnReceiveSinkStub<>(true), map, new ThrowOnReceiveSinkStub<>(true));
		validator.receive(coin);
	}

	@Test(expected = CashOverloadException.class)
	public void testAcceptWithBadRejectSink2() throws DisabledException, CashOverloadException {
		Coin coin = new Coin(currency, BigDecimal.TEN);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FailOnReceiveSinkStub<>(false));

		validator.setup(new ThrowOnReceiveSinkStub<>(true), map, new StandardSinkStub<>(true));
		validator.receive(coin);
	}

	@Test
	public void testHasSpace() throws CashOverloadException, DisabledException {
		StandardSinkStub<Coin> sink = new StandardSinkStub<Coin>(true) {
			@Override
			public void receive(Coin thing) throws CashOverloadException, DisabledException {
				retry = true;
			}
		};
		Coin coin = new Coin(currency, BigDecimal.ONE);

		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new FoundOnReceiveSinkStub(true));

		validator.setup(sink, map, new StandardSinkStub<>(true));

		assertTrue(validator.hasSpace());

		do {
			retry = false;
			validator.receive(coin);
		}
		while(retry);

		assertEquals(1, found);
	}

	private boolean retry = false;

	@Test
	public void testGoodAcceptWithListener() throws DisabledException, CashOverloadException {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));

		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));
		validator.attach(new CoinValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void validCoinDetected(CoinValidator validator, BigDecimal value) {
				found++;
			}

			@Override
			public void invalidCoinDetected(CoinValidator validator) {
				retry = true;
			}
		});
		Coin coin = new Coin(currency, BigDecimal.ONE);

		do {
			retry = false;
			validator.receive(coin);
		}
		while(retry);

		assertEquals(1, found);
	}

	@Test
	public void testBadAcceptWithListener() throws DisabledException, CashOverloadException {
		Map<BigDecimal, Sink<Coin>> map = new HashMap<>();
		map.put(BigDecimal.ONE, new StandardSinkStub<>(true));

		validator.setup(new StandardSinkStub<>(true), map, new StandardSinkStub<>(true));
		validator.attach(new CoinValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				// ignore
			}

			@Override
			public void validCoinDetected(CoinValidator validator, BigDecimal value) {
				fail();
			}

			@Override
			public void invalidCoinDetected(CoinValidator validator) {
				found++;
			}
		});
		Coin coin = new Coin(currency, BigDecimal.TEN);

		validator.receive(coin);
		assertEquals(1, found);
	}

	@Test(expected = ComponentFailure.class)
	public void testReject() {
		validator.reject(null);
	}

	@Test(expected = NoPowerException.class)
	public void testReceiveWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		validator.disactivate();
		validator.receive(null);
	}

	@Test(expected = NoPowerException.class)
	public void testHasSpaceWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		validator.disactivate();
		validator.hasSpace();
	}

	@Test(expected = NoPowerException.class)
	public void testRejectWithoutPower() throws CashOverloadException, DisabledException, NoCashAvailableException {
		validator.disactivate();
		validator.reject(null);
	}
}
