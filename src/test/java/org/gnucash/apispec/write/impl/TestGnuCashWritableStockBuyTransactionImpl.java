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
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.impl.GnuCashStockBuyTransactionImpl;
import org.gnucash.apispec.read.impl.TestGnuCashStockBuyTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableStockBuyTransaction;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableStockBuyTransactionImpl {
	private static final GCshTrxID TRX_1_ID = TestGnuCashStockBuyTransactionImpl.TRX_1_ID;
	private static final GCshTrxID TRX_4_ID = TestGnuCashStockBuyTransactionImpl.TRX_4_ID;

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
		return new JUnit4TestAdapter(TestGnuCashWritableStockBuyTransactionImpl.class);
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
		
		GnuCashStockBuyTransactionImpl roSpecTrx = new GnuCashStockBuyTransactionImpl((GnuCashTransactionImpl) genTrx);
		GnuCashWritableStockBuyTransaction specTrx = new GnuCashWritableStockBuyTransactionImpl(roSpecTrx);
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
		
		assertEquals(15.0,    specTrx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(125.0,   specTrx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1875.0,  specTrx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(4.15,    specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1879.15, specTrx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(15, 1),     specTrx.getNofSharesRat());
		assertEquals(BigFraction.of(125, 1),    specTrx.getPricePerShareRat());
		assertEquals(BigFraction.of(1875, 1),   specTrx.getNetPriceRat());
		assertEquals(BigFraction.of(83, 20),    specTrx.getFeesTaxesRat());
		assertEquals(BigFraction.of(37583, 20), specTrx.getGrossPriceRat());
		
		assertEquals(specTrx.getNofSharesRat().doubleValue(),     specTrx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getPricePerShareRat().doubleValue(), specTrx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNetPriceRat().doubleValue(),      specTrx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getFeesTaxesRat().doubleValue(),     specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getGrossPriceRat().doubleValue(),    specTrx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
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
	// High-level
	public void test02_1() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockBuyTransactionImpl specTrxRO = new GnuCashStockBuyTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		GnuCashWritableStockBuyTransaction specTrxRW = new GnuCashWritableStockBuyTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setNofShares( new FixedPointNumber("20") );
		specTrxRW.setPricePerShare( new FixedPointNumber("125.00") );
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}

		// Either of both:
		// specTrxRW.setGrossPrice( new FixedPointNumber("2510.00") );
		specTrxRW.refreshGrossPrice();
		
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

	@Test
	// Mid-level: Just like test01_1, but "manually"
	public void test02_2() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockBuyTransactionImpl specTrxRO = new GnuCashStockBuyTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		GnuCashWritableStockBuyTransaction specTrxRW = new GnuCashWritableStockBuyTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		GnuCashWritableTransactionSplit splt1 = specTrxRW.getWritableStockAccountSplit();
		splt1.setQuantity(new FixedPointNumber("20"));
		splt1.setValue(new FixedPointNumber("2500.00"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}
		
		GnuCashWritableTransactionSplit splt2 = specTrxRW.getWritableOffsettingAccountSplit();
		splt2.setQuantity(new FixedPointNumber("-2500.00")); // <-- net (!) amount
		splt2.setValue(new FixedPointNumber("-2600.00")); // <-- sic, not equal to qty
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splt2: value <> qty
		}
		
		splt2.setValue(new FixedPointNumber("-2500.00")); // <-- splt2 now consistent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splt2 consistent, but not correct, so trx still not balanced
		}
		
		// Add fees and taxes to gross amount:
		splt2.setValue(new FixedPointNumber("-2504.15"));
		splt2.setQuantity(new FixedPointNumber("-2504.15"));
		
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

	@Test
	// High-level: like test02_1, but one more twist
	public void test02_3() throws Exception {
		GnuCashWritableTransaction genTrx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());
		
		GnuCashStockBuyTransactionImpl specTrxRO = new GnuCashStockBuyTransactionImpl((GnuCashWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		GnuCashWritableStockBuyTransaction specTrxRW = new GnuCashWritableStockBuyTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setNofShares( new FixedPointNumber("20") );
		specTrxRW.setPricePerShare( new FixedPointNumber("125.00") );
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}

		// Either of both:
		// specTrxRW.setGrossPrice( new FixedPointNumber("2510.00") );
		specTrxRW.refreshGrossPrice();
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now everything's OK
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}

		// The twist: Add third fee/tax entry
		FixedPointNumber newFeeVal = new FixedPointNumber("1.25");
		specTrxRW.addFeeTax( new GCshAcctID("4681684348c64a10bac7f6e82a07f7d6"), newFeeVal ); // Root Account:Aufwendungen:Steuern
		
		// Diminish the first fee/tax entry, so the the trx's balance does not change
		GnuCashWritableTransactionSplit expSpltRW = specTrxRW.getWritableExpensesSplit( new GCshAcctID("7d4b851a3f704c4695d5d466b28cdc55") ); // Root Account:Aufwendungen:Bank:Provision
		expSpltRW.setValue( expSpltRW.getValue().subtract(newFeeVal) );
		expSpltRW.setQuantity( expSpltRW.getQuantity().subtract(newFeeVal) );
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now everything's OK
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_3_check_memory(specTrxRW);

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

		test02_3_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(GnuCashWritableStockBuyTransaction trx) throws Exception {
		assertEquals(4, trx.getSplitsCount()); // unchanged
		
		assertEquals("c3ae14400ec843f9bf63f5ef69a31528", trx.getStockAccountSplit().getID().toString());
		assertEquals(2, trx.getExpensesSplits().size());
		assertEquals("65539ddefc34439d80925275226e7849", trx.getExpensesSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", trx.getExpensesSplits().get(1).getID().toString());
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", trx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(trx.getSplits().get(0).toString(), trx.getStockAccountSplit().toString());
		assertEquals(trx.getSplits().get(1).toString(), trx.getExpensesSplits().get(0).toString());
		assertEquals(trx.getSplits().get(2).toString(), trx.getExpensesSplits().get(1).toString());
		assertEquals(trx.getSplits().get(3).toString(), trx.getOffsettingAccountSplit().toString());
		
		// ---
		
		assertEquals(20.0,    trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.15,    trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2504.15, trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),     trx.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),    trx.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1),   trx.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(83, 20),    trx.getFeesTaxesRat());
		assertEquals(BigFraction.of(250415, 100), trx.getGrossPriceRat()); // changed
		
		assertEquals(trx.getNofSharesRat().doubleValue(),     trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getPricePerShareRat().doubleValue(), trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNetPriceRat().doubleValue(),      trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getFeesTaxesRat().doubleValue(),     trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getGrossPriceRat().doubleValue(),    trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashTransaction genTrx = gcshOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());

		GnuCashStockBuyTransactionImpl specTrxRO = new GnuCashStockBuyTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(4, specTrxRO.getSplitsCount()); // unchanged
		
		assertEquals("c3ae14400ec843f9bf63f5ef69a31528", specTrxRO.getStockAccountSplit().getID().toString());
		assertEquals(2, specTrxRO.getExpensesSplits().size());
		assertEquals("65539ddefc34439d80925275226e7849", specTrxRO.getExpensesSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", specTrxRO.getExpensesSplits().get(1).getID().toString());
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", specTrxRO.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrxRO.getSplits().get(0).toString(), specTrxRO.getStockAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(1).toString(), specTrxRO.getExpensesSplits().get(0).toString());
		assertEquals(specTrxRO.getSplits().get(2).toString(), specTrxRO.getExpensesSplits().get(1).toString());
		assertEquals(specTrxRO.getSplits().get(3).toString(), specTrxRO.getOffsettingAccountSplit().toString());
		
		// ---
		
		assertEquals(20.0,    specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.15,    specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2504.15, specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),       specTrxRO.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),      specTrxRO.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1),     specTrxRO.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(83, 20),      specTrxRO.getFeesTaxesRat());
		assertEquals(BigFraction.of(250415, 100), specTrxRO.getGrossPriceRat()); // changed
		
		assertEquals(specTrxRO.getNofSharesRat().doubleValue(),     specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getPricePerShareRat().doubleValue(), specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNetPriceRat().doubleValue(),      specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getFeesTaxesRat().doubleValue(),     specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getGrossPriceRat().doubleValue(),    specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	// ---------------------------------------------------------------

	private void test02_3_check_memory(GnuCashWritableStockBuyTransaction trx) throws Exception {
		assertEquals(5, trx.getSplitsCount()); // changed
		
		assertEquals("c3ae14400ec843f9bf63f5ef69a31528", trx.getStockAccountSplit().getID().toString());
		assertEquals(3, trx.getExpensesSplits().size()); // changed
		assertEquals("65539ddefc34439d80925275226e7849", trx.getExpensesSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", trx.getExpensesSplits().get(1).getID().toString());
		assertEquals("4681684348c64a10bac7f6e82a07f7d6", trx.getExpensesSplits().get(2).getAccountID().toString()); // new; CAUTION: not ID (unpredictable), but ACCOUNT'S ID
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", trx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(trx.getSplits().get(0).toString(), trx.getStockAccountSplit().toString());
		assertEquals(trx.getSplits().get(1).toString(), trx.getExpensesSplits().get(0).toString());
		assertEquals(trx.getSplits().get(2).toString(), trx.getExpensesSplits().get(1).toString());
		assertEquals(trx.getSplits().get(3).toString(), trx.getOffsettingAccountSplit().toString());
		assertEquals(trx.getSplits().get(4).toString(), trx.getExpensesSplits().get(2).toString()); // new
		
		// ---
		
		assertEquals(20.0,    trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.15,    trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2504.15, trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),     trx.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),    trx.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1),   trx.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(83, 20),    trx.getFeesTaxesRat());
		assertEquals(BigFraction.of(250415, 100), trx.getGrossPriceRat()); // changed
		
		assertEquals(trx.getNofSharesRat().doubleValue(),     trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getPricePerShareRat().doubleValue(), trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNetPriceRat().doubleValue(),      trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getFeesTaxesRat().doubleValue(),     trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getGrossPriceRat().doubleValue(),    trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_3_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashTransaction genTrx = gcshOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());

		GnuCashStockBuyTransactionImpl specTrxRO = new GnuCashStockBuyTransactionImpl((GnuCashTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(5, specTrxRO.getSplitsCount()); // changed
		
		assertEquals("c3ae14400ec843f9bf63f5ef69a31528", specTrxRO.getStockAccountSplit().getID().toString());
		assertEquals(3, specTrxRO.getExpensesSplits().size()); // changed
		assertEquals("65539ddefc34439d80925275226e7849", specTrxRO.getExpensesSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", specTrxRO.getExpensesSplits().get(1).getID().toString());
		assertEquals("4681684348c64a10bac7f6e82a07f7d6", specTrxRO.getExpensesSplits().get(2).getAccountID().toString()); // new; CAUTION: not ID (unpredictable), but ACCOUNT'S ID
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", specTrxRO.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrxRO.getSplits().get(0).toString(), specTrxRO.getStockAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(1).toString(), specTrxRO.getExpensesSplits().get(0).toString());
		assertEquals(specTrxRO.getSplits().get(2).toString(), specTrxRO.getExpensesSplits().get(1).toString());
		assertEquals(specTrxRO.getSplits().get(3).toString(), specTrxRO.getOffsettingAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(4).toString(), specTrxRO.getExpensesSplits().get(2).toString()); // new
		
		// ---
		
		assertEquals(20.0,    specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.15,    specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2504.15, specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),       specTrxRO.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),      specTrxRO.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1),     specTrxRO.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(83, 20),      specTrxRO.getFeesTaxesRat());
		assertEquals(BigFraction.of(250415, 100), specTrxRO.getGrossPriceRat()); // changed
		
		assertEquals(specTrxRO.getNofSharesRat().doubleValue(),     specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getPricePerShareRat().doubleValue(), specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNetPriceRat().doubleValue(),      specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getFeesTaxesRat().doubleValue(),     specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getGrossPriceRat().doubleValue(),    specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
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
