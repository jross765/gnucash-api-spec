package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashSimpleTransactionImpl {
	
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
		return new JUnit4TestAdapter(TestGnuCashSimpleTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		URL gcshFileURL = null;
		File gcshFileRaw = null;
		try {
			gcshFileURL = classLoader.getResource(ConstTest.GCSH_FILENAME);
			gcshFileRaw = new File(gcshFileURL.getFile());
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileRaw);
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
			GnuCashSimpleTransaction specTrx = new GnuCashSimpleTransactionImpl((GnuCashTransactionImpl) genTrx);
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
		
		GnuCashSimpleTransaction specTrx = new GnuCashSimpleTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		// ---
		
		assertEquals(2, specTrx.getSplitsCount());
		
		assertEquals("b65f76a37e5643b1ac2ea2ad9cdf381d", specTrx.getFirstSplit().getID().toString());
		assertEquals(150.0, specTrx.getFirstSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(150.0, specTrx.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals("48657aca121b4500baef4078a3982c03", specTrx.getSecondSplit().getID().toString());
		assertEquals(-150.0, specTrx.getSecondSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(-150.0, specTrx.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(specTrx.getSplits().get(0).getID().toString(), specTrx.getFirstSplit().getID().toString());
		assertEquals(specTrx.getSplits().get(1).getID().toString(), specTrx.getSecondSplit().getID().toString());
		
		// ---
		
		assertEquals(-150.0, specTrx.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		// ---
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
