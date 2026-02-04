package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;
import java.util.Currency;
import java.util.List;

import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.GnuCashCurrency;
import org.gnucash.apispec.read.GnuCashFileExt;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashCurrencyImpl {
	// Mercedes-Benz Group AG
	public static final GCshCurrID CURR_1_ID = new GCshCurrID("EUR");
	public static final GCshCurrID CURR_2_ID = new GCshCurrID("USD");

	// -----------------------------------------------------------------

	private GnuCashFileExt  gcshFile = null;
	private GnuCashCurrency curr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashCurrencyImpl.class);
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
			gcshFile = new GnuCashFileExtImpl(gcshFileRaw);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		curr = gcshFile.getCurrencyByID(CURR_1_ID);
		assertNotEquals(null, curr);

		assertEquals("EUR", curr.getQualifID().getCode());
		assertEquals("CURRENCY:EUR", curr.getQualifID().toString());
		assertEquals("CURRENCY:EUR", curr.getQualifID().toStringShort());
		assertEquals("GCshCmdtyID [type='CURRENCY', nameSpace='CURRENCY', code='EUR']", curr.getQualifID().toStringLong());
		assertEquals(Currency.getInstance("EUR"), curr.getCurrency());
		
		curr = gcshFile.getCurrencyByID(CURR_2_ID);
		assertNotEquals(null, curr);

		assertEquals("USD", curr.getQualifID().getCode());
		assertEquals("CURRENCY:USD", curr.getQualifID().toString());
		assertEquals("CURRENCY:USD", curr.getQualifID().toStringShort());
		assertEquals("GCshCmdtyID [type='CURRENCY', nameSpace='CURRENCY', code='USD']", curr.getQualifID().toStringLong());
		assertEquals(Currency.getInstance("USD"), curr.getCurrency());
	}

	@Test
	public void test02() throws Exception {
		curr = gcshFile.getCurrencyByISOCode(CURR_1_ID.getCode());
		assertNotEquals(null, curr);

		assertEquals("EUR", curr.getQualifID().getCode());
		assertEquals("CURRENCY:EUR", curr.getQualifID().toString());
		assertEquals("CURRENCY:EUR", curr.getQualifID().toStringShort());
		assertEquals("GCshCmdtyID [type='CURRENCY', nameSpace='CURRENCY', code='EUR']", curr.getQualifID().toStringLong());
		assertEquals(Currency.getInstance("EUR"), curr.getCurrency());
		
		curr = gcshFile.getCurrencyByISOCode(CURR_2_ID.getCode());
		assertNotEquals(null, curr);

		assertEquals("USD", curr.getQualifID().getCode());
		assertEquals("CURRENCY:USD", curr.getQualifID().toString());
		assertEquals("CURRENCY:USD", curr.getQualifID().toStringShort());
		assertEquals("GCshCmdtyID [type='CURRENCY', nameSpace='CURRENCY', code='USD']", curr.getQualifID().toStringLong());
		assertEquals(Currency.getInstance("USD"), curr.getCurrency());
	}
	
	// ---------------------------------------------------------------
	
	@Test
	public void test03() throws Exception {
		List<GnuCashCurrency> currList = gcshFile.getCurrencies();

		assertEquals(2, currList.size());
		assertEquals(CURR_1_ID.toString(), currList.get(0).getQualifID().toString());
		assertEquals(CURR_2_ID.toString(), currList.get(1).getQualifID().toString());
	}
}
