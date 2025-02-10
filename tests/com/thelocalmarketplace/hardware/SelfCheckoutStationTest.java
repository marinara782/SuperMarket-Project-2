package com.thelocalmarketplace.hardware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserObserver;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteInsertionSlotObserver;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteStorageUnitObserver;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.tdc.coin.ICoinDispenser;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

@SuppressWarnings("javadoc")
public class SelfCheckoutStationTest {
	private SelfCheckoutStation station;
	private Currency CAD = Currency.getInstance(Locale.CANADA);

	@Before
	public void setup() {
		SelfCheckoutStation.resetConfigurationToDefaults();
		coinAdded = 0;
		banknoteAdded = 0;
		coinReturned = 0;
		banknoteReturned = 0;
		retryBanknote = false;
		retryCoin = false;
		banknotesEmpty = 0;
	}
	
	@Test
	public void testSupervisor() {
		station = new SelfCheckoutStation();
		assertFalse(station.isSupervised());
		station.setSupervisor(null);
		assertFalse(station.isSupervised());
		station.setSupervisor(new AttendantStation());
		assertTrue(station.isSupervised());
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureBanknoteDenominations() {
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ONE });
	}

	@Test
	public void testConfigureReusableBagsDispenser() {
		SelfCheckoutStation.configureReusableBagDispenserCapacity(1);
		station = new SelfCheckoutStation();
		assertEquals(1, station.reusableBagDispenser.getCapacity());
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureReusableBagsDispenser() {
		SelfCheckoutStation.configureReusableBagDispenserCapacity(0);
	}

	@Test
	public void testConfigureCoinDispenser() {
		SelfCheckoutStation.configureCoinDispenserCapacity(1);
		station = new SelfCheckoutStation();
		assertEquals(1, station.coinDispensers.values().iterator().next().getCapacity());
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureCoinDispenser() {
		SelfCheckoutStation.configureCoinDispenserCapacity(0);
	}

	@Test
	public void testConfigureBanknoteStorageUnit() {
		SelfCheckoutStation.configureBanknoteStorageUnitCapacity(1);
		station = new SelfCheckoutStation();
		assertEquals(1, station.banknoteStorage.getCapacity());
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureBanknoteStorageUnit() {
		SelfCheckoutStation.configureBanknoteStorageUnitCapacity(0);
	}

	@Test
	public void testConfigureCoinStorageUnit() {
		SelfCheckoutStation.configureCoinStorageUnitCapacity(1);
		station = new SelfCheckoutStation();
		assertEquals(1, station.coinStorage.getCapacity());
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureCoinStorageUnit() {
		SelfCheckoutStation.configureCoinStorageUnitCapacity(0);
	}

//	@Test
//	public void testConfigureCoinTray() {
//		SelfCheckoutStation.configureCoinTrayCapacity(1);
//		station = new SelfCheckoutStation();
//		assertEquals(1, station.coinTray.getCapacity());
//	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureCoinTray() {
		SelfCheckoutStation.configureCoinTrayCapacity(0);
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureCoinDenominations() {
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ZERO });
	}

	@Test(expected = InvalidArgumentSimulationException.class)
	public void testBadConfigureCoinDenominations2() {
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ONE });
	}

	@Test
	public void testSimple() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(2.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);
		station = new SelfCheckoutStation();
	}

	@Test(expected = SimulationException.class)
	public void testBad() {
		SelfCheckoutStation.configureCurrency(null);
	}

	@Test(expected = SimulationException.class)
	public void testBad2() {
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[0]);
	}

	@Test(expected = SimulationException.class)
	public void testBad3() {
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ZERO });
	}

	@Test(expected = SimulationException.class)
	public void testBad4() {
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ZERO });
	}

	@Test(expected = SimulationException.class)
	public void testBad5() {
		SelfCheckoutStation.configureCoinDenominations(null);
	}

	@Test(expected = SimulationException.class)
	public void testBad6() {
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] {});
	}

	@Test(expected = SimulationException.class)
	public void testBad8() {
		SelfCheckoutStation.configureScaleMaximumWeight(0);
	}

	@Test(expected = SimulationException.class)
	public void testBad9() {
		SelfCheckoutStation.configureScaleSensitivity(0);
	}

	@Test(expected = SimulationException.class)
	public void testBad10() {
		SelfCheckoutStation.configureBanknoteDenominations(null);
	}

	private boolean retryCoin;
	private boolean retryBanknote;
	private int coinAdded;
	private int banknoteAdded;
	private int coinReturned;
	private int banknoteReturned;
	private int banknotesEmpty;

	@Test
	public void testInsertBadBanknote() throws DisabledException, CashOverloadException {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(10.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());
		station.turnOn();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		Banknote banknote = new Banknote(CAD, BigDecimal.valueOf(2));
		station.banknoteInput.attach(new BanknoteInsertionSlotObserver() {
			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteRemoved(BanknoteInsertionSlot slot) {
				fail();
			}

			@Override
			public void banknoteInserted(BanknoteInsertionSlot slot) {
				banknoteAdded++;
			}

			@Override
			public void banknoteEjected(BanknoteInsertionSlot slot) {
				banknoteReturned++;
			}
		});

		station.banknoteInput.receive(banknote);

		assertEquals(1, banknoteAdded);
		assertEquals(1, banknoteReturned);
		assertTrue(station.banknoteInput.hasDanglingBanknotes());
	}

	@Test
	public void testInsertGoodCoin() throws DisabledException, CashOverloadException, NoCashAvailableException {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation
			.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE, BigDecimal.valueOf(2) });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.TEN, BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(10.0);
		SelfCheckoutStation.configureScaleSensitivity(2.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());
		station.turnOn();
		PowerGrid.engageUninterruptiblePowerSource();
		PowerGrid.instance().forcePowerRestore();

		station.banknoteDispensers.get(BigDecimal.ONE).load(new Banknote(CAD, BigDecimal.ONE));

		station.coinValidator.attach(new CoinValidatorObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void invalidCoinDetected(CoinValidator validator) {
				retryCoin = true;
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void validCoinDetected(CoinValidator validator, BigDecimal value) {}
		});

		station.coinDispensers.get(BigDecimal.ONE).attach(new CoinDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> component) {
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

			@Override
			public void coinsFull(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinsEmpty(ICoinDispenser dispenser) {
				fail();
			}

			@Override
			public void coinAdded(ICoinDispenser dispenser, Coin coin) {
				coinAdded++;
			}

			@Override
			public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
				fail();
			}

			@Override
			public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}

			@Override
			public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
				fail();
			}
		});

		station.banknoteStorage.attach(new BanknoteStorageUnitObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				fail();
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
				banknoteAdded++;
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

		station.banknoteDispensers.get(BigDecimal.ONE).attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> device) {
				fail();
			}

			@Override
			public void banknoteRemoved(IBanknoteDispenser device, Banknote banknote) {
				banknoteReturned++;
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				fail();
			}

			@Override
			public void moneyFull(IBanknoteDispenser dispenser) {
				fail();
			}

			@Override
			public void banknotesEmpty(IBanknoteDispenser dispenser) {
				banknotesEmpty++;
			}

			@Override
			public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}

			@Override
			public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}

			@Override
			public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});

		do {
			retryCoin = false;
			station.coinSlot.receive(new Coin(CAD, BigDecimal.ONE));
		}
		while(retryCoin);

		assertEquals(1, coinAdded);

		do {
			retryBanknote = false;
			station.banknoteInput.receive(new Banknote(CAD, BigDecimal.ONE));
		}
		while(retryBanknote);

		assertEquals(1, banknoteAdded);

		station.banknoteDispensers.get(BigDecimal.ONE).emit();

		assertEquals(1, banknoteReturned);
		assertEquals(1, banknotesEmpty);
	}

	@Test
	public void testPlugIn() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(2.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());

		assertEquals(true, station.getBaggingArea().isPluggedIn());
		assertEquals(false, station.getBaggingArea().isPoweredUp());
	}

	@Test
	public void testUnplug() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(2.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());
		station.unplug();

		assertEquals(false, station.getBaggingArea().isPluggedIn());
		assertEquals(false, station.getBaggingArea().isPoweredUp());
	}

	@Test
	public void testTurnOff() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(2.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());
		station.turnOn();
		station.turnOff();

		assertEquals(true, station.getBaggingArea().isPluggedIn());
		assertEquals(false, station.getBaggingArea().isPoweredUp());
	}

	@Test
	public void testTurnOn() {
		SelfCheckoutStation.configureCurrency(CAD);
		SelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE });
		SelfCheckoutStation.configureScaleMaximumWeight(2.0);
		SelfCheckoutStation.configureScaleSensitivity(1.0);

		station = new SelfCheckoutStation();

		station.plugIn(PowerGrid.instance());
		station.turnOn();

		assertEquals(true, station.getBaggingArea().isPluggedIn());
		assertEquals(true, station.getBaggingArea().isPoweredUp());
	}
}
