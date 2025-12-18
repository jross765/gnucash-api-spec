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
import org.gnucash.apispec.read.GnuCashStockDividendTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashStockDividendTransactionImpl {
	
	public static final GCshTrxID TRX_1_ID = new GCshTrxID("32b216aa73a44137aa5b041ab8739058");
	public static final GCshTrxID TRX_4_ID = new GCshTrxID("71979c2d99104919899fa249e616bcaa");
	
	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashStockDividendTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCsh_FILENAME);
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
		GnuCashTransaction genTrx = gcshFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		assertEquals(2, genTrx.getSplitsCount());
		
		try {
			GnuCashStockDividendTransaction specTrx = new GnuCashStockDividendTransactionImpl((GnuCashTransactionImpl) genTrx);
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		GnuCashTransaction genTrx = gcshFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockDividendTransaction specTrx = new GnuCashStockDividendTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		// ---
		
		assertEquals(5, specTrx.getSplitsCount());
		
		assertEquals("ea08a144322146cea38b39d134ca6fc1", specTrx.getStockAccountSplit().getID().toString());
		assertEquals("5c5fa881869843d090a932f8e6b15af2", specTrx.getIncomeAccountSplit().getID().toString());
		assertEquals(2, specTrx.getExpensesSplits().size());
		assertEquals("1fd25550fb63498999ed85a7e935a0e3", specTrx.getExpensesSplits().get(0).getID().toString());
		assertEquals("c99e8b5e6b5f410a8ff4fc188480e548", specTrx.getExpensesSplits().get(1).getID().toString());
		assertEquals("7abf90fe15124254ac3eb7ec33f798e7", specTrx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getOffsettingAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(1).toString(), specTrx.getStockAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(2).toString(), specTrx.getExpensesSplits().get(0).toString());
		assertEquals(specTrx.getSplits().get(3).toString(), specTrx.getExpensesSplits().get(1).toString());
		assertEquals(specTrx.getSplits().get(4).toString(), specTrx.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(11.0, specTrx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2.93, specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(8.07, specTrx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(11, 1),    specTrx.getGrossDividendRat());
		assertEquals(BigFraction.of(293, 100), specTrx.getFeesTaxesRat());
		assertEquals(BigFraction.of(807, 100), specTrx.getNetDividendRat());
		
		assertEquals(specTrx.getGrossDividendRat().doubleValue(), specTrx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getFeesTaxesRat().doubleValue(),     specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNetDividendRat().doubleValue(),   specTrx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		// ---
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
