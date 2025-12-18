package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.GnuCashStockSplitTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashStockSplitTransactionImpl {
	
	public static final GCshTrxID TRX_1_ID = new GCshTrxID("32b216aa73a44137aa5b041ab8739058");
	public static final GCshTrxID TRX_4_ID = new GCshTrxID("c589727108b8491d828a5c1a9bdc11d1");
	
	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashStockSplitTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		GnuCashTransaction genTrx = gcshFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		assertEquals(5, genTrx.getSplitsCount());
		
		try {
			GnuCashStockSplitTransaction specTrx = new GnuCashStockSplitTransactionImpl((GnuCashTransactionImpl) genTrx);
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		GnuCashTransaction genTrx = gcshFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashStockSplitTransaction specTrx = new GnuCashStockSplitTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		// ---
		
		assertEquals(1, specTrx.getSplitsCount());
		
		assertEquals("e9900dc4e5124b2b8ad42347a6ee6fc0", specTrx.getSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getSplit().toString());
		
		// ---
		
		assertEquals(10.0, specTrx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3.0,  specTrx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(5.0,  specTrx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(15.0, specTrx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(10, 1), specTrx.getNofAddSharesRat());
		assertEquals(BigFraction.of(3, 1),  specTrx.getSplitFactorRat());
		assertEquals(BigFraction.of(5, 1),  specTrx.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(15, 1), specTrx.getNofSharesAfterSplitRat());
		
		assertEquals(specTrx.getNofAddSharesRat().doubleValue(),         specTrx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getSplitFactorRat().doubleValue(),          specTrx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNofSharesBeforeSplitRat().doubleValue(), specTrx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNofSharesAfterSplitRat().doubleValue(),  specTrx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		// ---
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
