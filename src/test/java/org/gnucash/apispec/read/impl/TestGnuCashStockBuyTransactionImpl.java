package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.GnuCashStockBuyTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashStockBuyTransactionImpl {
	
	public static final GCshTrxID TRX_1_ID = new GCshTrxID("4307689faade47d8aab4db87c8ce3aaf");
	public static final GCshTrxID TRX_4_ID = new GCshTrxID("71979c2d99104919899fa249e616bcaa");
	
	// -----------------------------------------------------------------

	private GnuCashFile kmmFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashStockBuyTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.GCsh_FILENAME);
		// System.err.println("GnuCash test file resource: '" + kmmFileURL + "'");
		InputStream kmmFileStream = null;
		try {
			kmmFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmFile = new GnuCashFileImpl(kmmFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		GnuCashTransaction genTrx = kmmFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		assertEquals(2, genTrx.getSplitsCount());
		
		try {
			GnuCashStockBuyTransaction specTrx = new GnuCashStockBuyTransactionImpl((GnuCashTransactionImpl) genTrx);
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		GnuCashTransaction genTrx = kmmFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockBuyTransaction specTrx = new GnuCashStockBuyTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		// ---
		
		assertEquals(4, specTrx.getSplitsCount());
		
		assertEquals("c3ae14400ec843f9bf63f5ef69a31528", specTrx.getStockAccountSplit().getID().toString());
		assertEquals(2, specTrx.getExpensesSplits().size());
		assertEquals("65539ddefc34439d80925275226e7849", specTrx.getExpensesSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", specTrx.getExpensesSplits().get(1).getID().toString());
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", specTrx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getStockAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(1).toString(), specTrx.getExpensesSplits().get(0).toString());
		assertEquals(specTrx.getSplits().get(2).toString(), specTrx.getExpensesSplits().get(1).toString());
		assertEquals(specTrx.getSplits().get(3).toString(), specTrx.getOffsettingAccountSplit().toString());
		
		// ---
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
