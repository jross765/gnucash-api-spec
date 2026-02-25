package org.gnucash.apispec.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.impl.GnuCashStockSplitTransactionImpl;
import org.gnucash.apispec.read.impl.TestGnuCashStockSplitTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableStockSplitTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableStockSplitTransactionImpl {
	private static final GCshTrxID TRX_1_ID = TestGnuCashStockSplitTransactionImpl.TRX_1_ID;
	private static final GCshTrxID TRX_4_ID = TestGnuCashStockSplitTransactionImpl.TRX_4_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshTrxID newTrxID = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashWritableStockSplitTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		URL gcshInFileURL = null;
		File gcshInFileRaw = null;
		try {
			gcshInFileURL = classLoader.getResource(ConstTest.GCSH_FILENAME);
			gcshInFileRaw = new File(gcshInFileURL.getFile());
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshInFile = new GnuCashWritableFileImpl(gcshInFileRaw);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGnuCashStockBuyTransactionImpl.test02
	//
	// Check whether the GnuCashWritableTransaction objects returned by
	// GnuCashWritableFileImpl.getWritableStockBuyTransactionByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getTransactionByID().
	
	@Test
	public void test01_2() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashStockSplitTransactionImpl roSpecTrx = new GnuCashStockSplitTransactionImpl((GnuCashTransactionImpl) genTrx);
		GnuCashWritableStockSplitTransaction specTrx = new GnuCashWritableStockSplitTransactionImpl(roSpecTrx);
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

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableStockBuyTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashStockSplitTransactionImpl specTrxRO = new GnuCashStockSplitTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		GnuCashWritableStockSplitTransaction specTrxRW = new GnuCashWritableStockSplitTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Variant 1:
		// Modify the object

		specTrxRW.setNofAddShares(new FixedPointNumber("13"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Variant 2:
		
		specTrxRW.setSplitFactor(BigFraction.of(36, 10)); // redundant / idempotent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Variant 3:
		
		specTrxRW.setNofSharesAfterSplit(new FixedPointNumber("18")); // redundant / idempotent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(GnuCashWritableStockSplitTransaction trx) throws Exception {
		assertEquals(1, trx.getSplitsCount());
		
		// ---
		
		assertEquals(13.0, trx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(3.6,  trx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(5.0,  trx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(18.0, trx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(13, 1),  trx.getNofAddSharesRat()); // changed
		assertEquals(BigFraction.of(36, 10), trx.getSplitFactorRat()); // changed
		assertEquals(BigFraction.of(5, 1),   trx.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(18, 1),  trx.getNofSharesAfterSplitRat()); // changed
		
		assertEquals(trx.getNofAddSharesRat().doubleValue(),         trx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getSplitFactorRat().doubleValue(),          trx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNofSharesBeforeSplitRat().doubleValue(), trx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNofSharesAfterSplitRat().doubleValue(),  trx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashTransaction genTrx = gcshOutFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());

		GnuCashStockSplitTransactionImpl specTrxRO = new GnuCashStockSplitTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(1, specTrxRO.getSplitsCount());
		
		// ---
		
		assertEquals(13.0, specTrxRO.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(3.6,  specTrxRO.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(5.0,  specTrxRO.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(18.0, specTrxRO.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(13, 1),  specTrxRO.getNofAddSharesRat()); // changed
		assertEquals(BigFraction.of(36, 10), specTrxRO.getSplitFactorRat()); // changed
		assertEquals(BigFraction.of(5, 1),   specTrxRO.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(18, 1),  specTrxRO.getNofSharesAfterSplitRat()); // changed
		
		assertEquals(specTrxRO.getNofAddSharesRat().doubleValue(),         specTrxRO.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getSplitFactorRat().doubleValue(),          specTrxRO.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNofSharesBeforeSplitRat().doubleValue(), specTrxRO.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNofSharesAfterSplitRat().doubleValue(),  specTrxRO.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------
	
	// NOT POSSIBLE (THE WAY WE NORMALLY USE THE API).
	// A special transaction must correctly validate the moment we instantiate it.
	// It is therefore not possible to create an empty instance of GnuCashWritableTransaction,
	// because 'empty' means 'no splits', which in turn means 'is not valid'.
	
	// You must therefore always generate a generic transaction with two splits etc.
	// But this already is implicitly tested elsewhere.

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------
	
	// ::EMPTY
	
	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------
	
	// ::TODO

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
