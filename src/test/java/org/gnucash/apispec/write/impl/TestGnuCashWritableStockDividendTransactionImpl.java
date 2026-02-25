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
import org.gnucash.apispec.read.impl.GnuCashStockDividendTransactionImpl;
import org.gnucash.apispec.read.impl.TestGnuCashStockDividendTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableStockDividendTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableStockDividendTransactionImpl {
	private static final GCshTrxID TRX_1_ID = TestGnuCashStockDividendTransactionImpl.TRX_1_ID;
	private static final GCshTrxID TRX_4_ID = TestGnuCashStockDividendTransactionImpl.TRX_4_ID;

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
		return new JUnit4TestAdapter(TestGnuCashWritableStockDividendTransactionImpl.class);
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
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockDividendTransactionImpl roSpecTrx = new GnuCashStockDividendTransactionImpl((GnuCashTransactionImpl) genTrx);
		GnuCashWritableStockDividendTransaction specTrx = new GnuCashWritableStockDividendTransactionImpl(roSpecTrx);
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

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableStockBuyTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockDividendTransactionImpl specTrxRO = new GnuCashStockDividendTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		GnuCashWritableStockDividendTransaction specTrxRW = new GnuCashWritableStockDividendTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setNetDividend(new FixedPointNumber("9.00"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}
		
		// Correct gross amount:
		specTrxRW.setGrossDividend(new FixedPointNumber("11.93"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now everything's OK
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

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

	private void test02_1_check_memory(GnuCashWritableStockDividendTransaction trx) throws Exception {
		assertEquals(5, trx.getSplitsCount());
		
		assertEquals("ea08a144322146cea38b39d134ca6fc1", trx.getStockAccountSplit().getID().toString());
		assertEquals("5c5fa881869843d090a932f8e6b15af2", trx.getIncomeAccountSplit().getID().toString());
		assertEquals(2, trx.getExpensesSplits().size());
		assertEquals("1fd25550fb63498999ed85a7e935a0e3", trx.getExpensesSplits().get(0).getID().toString());
		assertEquals("c99e8b5e6b5f410a8ff4fc188480e548", trx.getExpensesSplits().get(1).getID().toString());
		assertEquals("7abf90fe15124254ac3eb7ec33f798e7", trx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(trx.getSplits().get(0).toString(), trx.getOffsettingAccountSplit().toString());
		assertEquals(trx.getSplits().get(1).toString(), trx.getStockAccountSplit().toString());
		assertEquals(trx.getSplits().get(2).toString(), trx.getExpensesSplits().get(0).toString());
		assertEquals(trx.getSplits().get(3).toString(), trx.getExpensesSplits().get(1).toString());
		assertEquals(trx.getSplits().get(4).toString(), trx.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(11.93, trx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(2.93,  trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(9.00,  trx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(1193, 100), trx.getGrossDividendRat()); // changed
		assertEquals(BigFraction.of(293, 100),  trx.getFeesTaxesRat());
		assertEquals(BigFraction.of(9, 1),      trx.getNetDividendRat()); // changed
		
		assertEquals(trx.getGrossDividendRat().doubleValue(), trx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getFeesTaxesRat().doubleValue(),     trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNetDividendRat().doubleValue(),   trx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashTransaction genTrx = gcshOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());

		GnuCashStockDividendTransactionImpl specTrxRO = new GnuCashStockDividendTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(5, specTrxRO.getSplitsCount());
		
		assertEquals("ea08a144322146cea38b39d134ca6fc1", specTrxRO.getStockAccountSplit().getID().toString());
		assertEquals("5c5fa881869843d090a932f8e6b15af2", specTrxRO.getIncomeAccountSplit().getID().toString());
		assertEquals(2, specTrxRO.getExpensesSplits().size());
		assertEquals("1fd25550fb63498999ed85a7e935a0e3", specTrxRO.getExpensesSplits().get(0).getID().toString());
		assertEquals("c99e8b5e6b5f410a8ff4fc188480e548", specTrxRO.getExpensesSplits().get(1).getID().toString());
		assertEquals("7abf90fe15124254ac3eb7ec33f798e7", specTrxRO.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrxRO.getSplits().get(0).toString(), specTrxRO.getOffsettingAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(1).toString(), specTrxRO.getStockAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(2).toString(), specTrxRO.getExpensesSplits().get(0).toString());
		assertEquals(specTrxRO.getSplits().get(3).toString(), specTrxRO.getExpensesSplits().get(1).toString());
		assertEquals(specTrxRO.getSplits().get(4).toString(), specTrxRO.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(11.93, specTrxRO.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(2.93,  specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(9.00,  specTrxRO.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(1193, 100), specTrxRO.getGrossDividendRat()); // changed
		assertEquals(BigFraction.of(293, 100),  specTrxRO.getFeesTaxesRat());
		assertEquals(BigFraction.of(9, 1),      specTrxRO.getNetDividendRat()); // changed
		
		assertEquals(specTrxRO.getGrossDividendRat().doubleValue(), specTrxRO.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getFeesTaxesRat().doubleValue(),     specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNetDividendRat().doubleValue(),   specTrxRO.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
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
