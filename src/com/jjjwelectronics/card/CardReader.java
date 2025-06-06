package com.jjjwelectronics.card;

/**
 * Represents the card reader, capable of tap, chip insert, and swipe. Either
 * the reader or the card may fail, or the data read in can be corrupted, with
 * varying probabilities.
 * <p>
 * As our most economical model, it is the least reliable of our line of card
 * readers, but it still outshines the competition!
 * 
 * @author JJJW Electronics LLP
 */
public class CardReader extends AbstractCardReader implements ICardReader {
	/**
	 * Basic constructor.
	 */
	public CardReader() {
		probabilityOfTapFailure = 0.05;
		probabilityOfInsertFailure = 0.05;
		probabilityOfSwipeFailure = 0.5;
	}
}
