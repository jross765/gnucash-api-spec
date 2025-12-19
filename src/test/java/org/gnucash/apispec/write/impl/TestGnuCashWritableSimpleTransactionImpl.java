package org.gnucash.apispec.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.TestGnuCashSimpleTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableSimpleTransaction;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableSimpleTransactionImpl {
	private static final GCshTrxID TRX_1_ID = TestGnuCashSimpleTransactionImpl.TRX_1_ID;
	private static final GCshTrxID TRX_4_ID = TestGnuCashSimpleTransactionImpl.TRX_4_ID;
//	private static final GCshTrxID TRX_10_ID = new GCshTrxID("c97032ba41684b2bb5d1391c9d7547e9");
//
//	private static final GCshAcctID ACCT_1_ID  = new GCshAcctID("bbf77a599bd24a3dbfec3dd1d0bb9f5c"); // Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa
//	private static final GCshAcctID ACCT_20_ID = new GCshAcctID("b88e9eca9c73411b947b882d0bf8ec6f"); // Root Account::Aktiva::Sichteinlagen::nicht-KK::Sparkonto

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
		return new JUnit4TestAdapter(TestGnuCashWritableSimpleTransactionImpl.class);
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
	// Cf. TestGnuCashSimpleTransactionImpl.test02
	//
	// Check whether the GnuCashWritableTransaction objects returned by
	// GnuCashWritableFileImpl.getWritableSimpleTransactionByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getTransactionByID().
	
	@Test
	public void test01_2() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashSimpleTransactionImpl specTrxRO = new GnuCashSimpleTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		GnuCashWritableSimpleTransaction specTrxRW = new GnuCashWritableSimpleTransactionImpl((GnuCashSimpleTransactionImpl) specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ---
		
		assertEquals(2, specTrxRW.getSplitsCount());
		
		assertEquals("b65f76a37e5643b1ac2ea2ad9cdf381d", specTrxRW.getFirstSplit().getID().toString());
		assertEquals(150.0, specTrxRW.getFirstSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(150.0, specTrxRW.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals("48657aca121b4500baef4078a3982c03", specTrxRW.getSecondSplit().getID().toString());
		assertEquals(-150.0, specTrxRW.getSecondSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-150.0, specTrxRW.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(specTrxRW.getSplits().get(0).getID().toString(), specTrxRW.getFirstSplit().getID().toString());
		assertEquals(specTrxRW.getSplits().get(1).getID().toString(), specTrxRW.getSecondSplit().getID().toString());
		
		// ---
		
		assertEquals(-150.0, specTrxRW.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		// ---
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableSimpleTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashSimpleTransactionImpl specTrxRO = new GnuCashSimpleTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		GnuCashWritableSimpleTransaction specTrxRW = new GnuCashWritableSimpleTransactionImpl((GnuCashSimpleTransactionImpl) specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		GnuCashWritableTransactionSplit splt1 = specTrxRW.getWritableFirstSplit();
		splt1.setQuantity(new FixedPointNumber("200"));
		splt1.setValue(new FixedPointNumber("200"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- split 1 not symmetric to split 2
		}
		
		GnuCashWritableTransactionSplit splt2 = specTrxRW.getWritableSecondSplit();
		splt2.setQuantity(new FixedPointNumber("-200"));
		splt2.setValue(new FixedPointNumber("-300")); // <-- sic
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splits still not symmetric
		}
		
		splt2.setValue(new FixedPointNumber("-200")); // <-- now correct
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now finally
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

	@Test
	public void test02_2() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		GnuCashSimpleTransactionImpl specTrxRO = new GnuCashSimpleTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		GnuCashWritableSimpleTransaction specTrxRW = new GnuCashWritableSimpleTransactionImpl((GnuCashSimpleTransactionImpl) specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setAmount(new FixedPointNumber("300"));

		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_2_check_memory(specTrxRW);

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

		// Not necessary:
		// test02_2_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(GnuCashWritableSimpleTransaction trx) throws Exception {
		assertEquals(2, trx.getSplitsCount()); // unchanged
		
		assertEquals("b65f76a37e5643b1ac2ea2ad9cdf381d", trx.getFirstSplit().getID().toString()); // unchanged
		assertEquals(200.0, trx.getFirstSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(200.0, trx.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("48657aca121b4500baef4078a3982c03", trx.getSecondSplit().getID().toString()); // unchanged
		assertEquals(-200.0, trx.getSecondSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-200.0, trx.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(-200.0, trx.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashTransaction genTrx = gcshOutFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());

		GnuCashSimpleTransactionImpl specTrxRO = new GnuCashSimpleTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		// ---
		
		assertEquals(2, specTrxRO.getSplitsCount()); // unchanged
		
		assertEquals("b65f76a37e5643b1ac2ea2ad9cdf381d", specTrxRO.getFirstSplit().getID().toString()); // unchanged
		assertEquals(200.0, specTrxRO.getFirstSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(200.0, specTrxRO.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("48657aca121b4500baef4078a3982c03", specTrxRO.getSecondSplit().getID().toString()); // unchanged
		assertEquals(-200.0, specTrxRO.getSecondSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-200.0, specTrxRO.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(-200.0, specTrxRO.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	// ---------------------------------------------------------------

	private void test02_2_check_memory(GnuCashWritableSimpleTransaction trx) throws Exception {
		assertEquals(2, trx.getSplitsCount()); // unchanged
		
		assertEquals("b65f76a37e5643b1ac2ea2ad9cdf381d", trx.getFirstSplit().getID().toString()); // unchanged
		assertEquals(-300.0, trx.getFirstSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-300.0, trx.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("48657aca121b4500baef4078a3982c03", trx.getSecondSplit().getID().toString()); // unchanged
		assertEquals(300.0, trx.getSecondSplit().getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(300.0, trx.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(300.0, trx.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
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
