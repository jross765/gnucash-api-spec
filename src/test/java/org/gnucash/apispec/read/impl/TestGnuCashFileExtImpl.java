package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.gnucash.apispec.ConstTest;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashFileExtImpl {
	private GnuCashFileExtImpl gcshFile  = null;
	private GnuCashFileExtImpl gcshFile2 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashFileExtImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		URL gcshFileURL = null;
		File gcshFileRaw = null;
		File gcshFileRaw2 = null;
		try {
			gcshFileURL = classLoader.getResource(ConstTest.GCSH_FILENAME);
			gcshFileRaw  = new File(gcshFileURL.getFile());
			gcshFileRaw2 = new File(gcshFileURL.getFile());
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile  = new GnuCashFileExtImpl(gcshFileRaw);
			gcshFile2 = new GnuCashFileExtImpl(gcshFileRaw2);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test10() throws Exception {
		assertEquals(ConstTest.Stats.NOF_CURR, gcshFile.getCurrencies().size());
		assertEquals(ConstTest.Stats.NOF_SEC, gcshFile.getSecurities().size());
	}

	// ---------------------------------------------------------------
	// The following test cases seem trivial, obvious, superfluous. 
	// I am not so sure about that. I cannot exactly provide a reason
	// right now, but my gut and my experience tell me that these tests
	// are not that trivial and redundant as they seem to be.

	@Test
	public void test24() throws Exception {
		assertEquals(gcshFile.getCurrencies().toString(), gcshFile2.getCurrencies().toString());
		assertEquals(gcshFile.getSecurities().toString(), gcshFile2.getSecurities().toString());
	}

	// ---------------------------------------------------------------

	/*
	@Test
	public void test30() throws Exception {
		PrintStream dumpOutStream = new PrintStream(DUMP_OUT_FILE_NAME);
		gcshFile.dump(dumpOutStream);
		dumpOutStream.close();
		
		File dumpOutFile = new File(DUMP_OUT_FILE_NAME);
		File dumpRefFile = new File(DUMP_REF_FILE_NAME);
		assertTrue(FileUtils.contentEquals(dumpOutFile, dumpRefFile));
	}
	*/

}
