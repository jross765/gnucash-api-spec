package org.gnucash.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.GnuCashFileExt;
import org.gnucash.apispec.read.GnuCashSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshSecID_SecIdType;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashSecurityImpl {
	// Mercedes-Benz Group AG
	public static final GCshCmdtyNameSpace.Exchange SEC_1_EXCH = GCshCmdtyNameSpace.Exchange.EURONEXT;
	public static final String SEC_1_ID   = "MBG";
	public static final String SEC_1_ISIN = "DE0007100000";

	// SAP SE
	public static final GCshCmdtyNameSpace.Exchange SEC_2_EXCH = GCshCmdtyNameSpace.Exchange.EURONEXT;
	public static final String SEC_2_ID   = "SAP";
	public static final String SEC_2_ISIN = "DE0007164600";

	// AstraZeneca Plc
	// Note that in the SecIDType variants, the ISIN/CUSIP/SEDOL/WKN/whatever
	// is stored twice in the object, redundantly
	public static final GCshCmdtyNameSpace.SecIdType SEC_3_SECIDTYPE = GCshCmdtyNameSpace.SecIdType.ISIN;
	public static final String SEC_3_ID   = "GB0009895292";
	public static final String SEC_3_ISIN = SEC_3_ID;

	// Coca Cola
	public static final GCshCmdtyNameSpace.SecIdType SEC_4_SECIDTYPE = GCshCmdtyNameSpace.SecIdType.ISIN;
	public static final String SEC_4_ID   = "US1912161007";
	public static final String SEC_4_ISIN = SEC_4_ID;

	// -----------------------------------------------------------------

	private GnuCashFileExt  gcshFile = null;
	private GnuCashSecurity sec = null;

	private GCshSecID secID1 = null;
	private GCshSecID secID2 = null;
	private GCshSecID secID3 = null;
	private GCshSecID secID4 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashSecurityImpl.class);
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

		// ---

		secID1 = new GCshSecID_Exchange(SEC_1_EXCH, SEC_1_ID);
		secID2 = new GCshSecID_Exchange(SEC_2_EXCH, SEC_2_ID);
		secID3 = new GCshSecID_SecIdType(SEC_3_SECIDTYPE, SEC_3_ID);
		secID4 = new GCshSecID_SecIdType(SEC_4_SECIDTYPE, SEC_4_ID);
	}

	// -----------------------------------------------------------------

	@Test
	public void test00() throws Exception {
		// Cf. TestsecID -- let's just double-check
		assertEquals(SEC_1_EXCH.toString() + GCshCmdtyID.SEPARATOR + SEC_1_ID, secID1.toString());
		assertEquals(SEC_2_EXCH.toString() + GCshCmdtyID.SEPARATOR + SEC_2_ID, secID2.toString());
		assertEquals(SEC_3_SECIDTYPE.toString() + GCshCmdtyID.SEPARATOR + SEC_3_ID, secID3.toString());
	}

	// ------------------------------

	@Test
	public void test01_1() throws Exception {
		sec = gcshFile.getSecurityByQualifID(SEC_1_EXCH, SEC_1_ID);
		assertNotEquals(null, sec);

		assertEquals(secID1.toString(), sec.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, sec.getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
		assertEquals(SEC_1_ISIN, sec.getXCode());
		assertEquals("Mercedes-Benz Group AG", sec.getName());
	}

//	@Test
//	public void test01_2() throws Exception {
//		sec = gcshFile.getSecurityByQualifID(secID1.toString());
//		assertNotEquals(null, sec);
//
//		assertEquals(secID1.toString(), sec.getQualifID().toString());
//		// *Not* equal because of class
//		assertNotEquals(secID1, sec.getQualifID());
//		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
//		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
//		assertEquals(SEC_1_ISIN, sec.getXCode());
//		assertEquals("Mercedes-Benz Group AG", sec.getName());
//	}

	@Test
	public void test01_3() throws Exception {
		sec = gcshFile.getSecurityByXCode(SEC_1_ISIN);
		assertNotEquals(null, sec);

		assertEquals(secID1.toString(), sec.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, sec.getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
		assertEquals(SEC_1_ISIN, sec.getXCode());
		assertEquals("Mercedes-Benz Group AG", sec.getName());
	}

	@Test
	public void test01_4() throws Exception {
		List<GnuCashSecurity> secList = gcshFile.getSecuritiesByName("mercedes");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());

		assertEquals(secID1.toString(), secList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	        ((GnuCashSecurity) secList.toArray()[0]).getQualifID()); // not trivial!
		assertEquals(SEC_1_ISIN, secList.get(0).getXCode());
		assertEquals("Mercedes-Benz Group AG", ((GnuCashSecurity) secList.toArray()[0]).getName());

		secList = gcshFile.getSecuritiesByName("BENZ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         secList.get(0).getQualifID());

		secList = gcshFile.getSecuritiesByName(" MeRceDeS-bEnZ  ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		assertEquals(secID1.toString(), secList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         secList.get(0).getQualifID()); // not trivial!
	}

	// ------------------------------

	@Test
	public void test02_1() throws Exception {
		sec = gcshFile.getSecurityByQualifID(SEC_3_SECIDTYPE.toString(), SEC_3_ID);
		assertNotEquals(null, sec);

		assertEquals(secID3.toString(), sec.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID3, sec.getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
		assertEquals(SEC_3_ISIN, sec.getXCode());
		assertEquals("AstraZeneca Plc", sec.getName());
	}

//	@Test
//	public void test02_2() throws Exception {
//		sec = gcshFile.getSecurityByQualifID(secID3.toString());
//		assertNotEquals(null, sec);
//
//		assertEquals(secID3.toString(), sec.getQualifID().toString());
//		// *Not* equal because of class
//		assertNotEquals(secID3, sec.getQualifID());
//		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
//		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
//		assertEquals(SEC_3_ISIN, sec.getXCode());
//		assertEquals("AstraZeneca Plc", sec.getName());
//	}

	@Test
	public void test02_3() throws Exception {
		sec = gcshFile.getSecurityByXCode(SEC_3_ISIN);
		assertNotEquals(null, sec);

		assertEquals(secID3.toString(), sec.getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID3, sec.getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, sec.getQualifID()); // not trivial!
		assertEquals(SEC_3_ISIN, sec.getXCode());
		assertEquals("AstraZeneca Plc", sec.getName());
	}

	@Test
	public void test02_4() throws Exception {
		List<GnuCashSecurity> secList = gcshFile.getSecuritiesByName("astra");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());

		assertEquals(secID3.toString(), ((GnuCashSecurity) secList.toArray()[0]).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID3, ((GnuCashSecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	        ((GnuCashSecurity) secList.toArray()[0]).getQualifID()); // not trivial!
		assertEquals(SEC_3_ISIN, ((GnuCashSecurity) secList.toArray()[0]).getXCode());
		assertEquals("AstraZeneca Plc", ((GnuCashSecurity) secList.toArray()[0]).getName());

		secList = gcshFile.getSecuritiesByName("BENZ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		// *Not* equal because of class
		assertNotEquals(secID3, ((GnuCashSecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         ((GnuCashSecurity) secList.toArray()[0]).getQualifID());

		secList = gcshFile.getSecuritiesByName(" aStrAzENeCA  ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		assertEquals(secID3.toString(), ((GnuCashSecurity) secList.toArray()[0]).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID3, ((GnuCashSecurity) secList.toArray()[0]).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         ((GnuCashSecurity) secList.toArray()[0]).getQualifID()); // not trivial!
	}
	
	// ---------------------------------------------------------------
	
	@Test
	public void test03() throws Exception {
		List<GnuCashSecurity> secList = gcshFile.getSecurities();

		assertEquals(6, secList.size());
		assertEquals(secID3.toString(), secList.get(0).getQualifID().toString());
		// ...
		assertEquals(secID4.toString(), secList.get(secList.size() - 1).getQualifID().toString());
	}
}
