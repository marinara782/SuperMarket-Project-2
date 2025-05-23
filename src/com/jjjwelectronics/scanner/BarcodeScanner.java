package com.jjjwelectronics.scanner;

/**
 * A complex device hidden behind a simple simulation. They can scan and that is
 * about all.
 * <p>
 * A more modest model than Gold, our Silver model can occasionally fail to scan
 * barcodes.
 * 
 * @author JJJW Electronics LLP
 */
public class BarcodeScanner extends AbstractBarcodeScanner {
	/**
	 * Create a barcode scanner.
	 */
	public BarcodeScanner() {
		probabilityOfFailedScan = 10;
	}
}
