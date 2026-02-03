package org.gnucash.apispec.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.apispec.ConstTest;
import org.gnucash.apispec.read.impl.GnuCashFileExtImpl;
import org.gnucash.apispec.read.impl.TestGnuCashSecurityImpl;
import org.gnucash.apispec.write.GnuCashWritableSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.complex.GCshSecID_Exchange;
import org.gnucash.base.basetypes.complex.GCshSecID_MIC;
import org.gnucash.base.basetypes.complex.GCshSecID_SecIdType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableSecurityImpl {
	public static final GCshCmdtyNameSpace.Exchange SEC_1_EXCH = TestGnuCashSecurityImpl.SEC_1_EXCH;
	public static final String SEC_1_ID   = TestGnuCashSecurityImpl.SEC_1_ID;
	public static final String SEC_1_ISIN = TestGnuCashSecurityImpl.SEC_1_ISIN;

	public static final GCshCmdtyNameSpace.Exchange SEC_2_EXCH = TestGnuCashSecurityImpl.SEC_2_EXCH;
	public static final String SEC_2_ID   = TestGnuCashSecurityImpl.SEC_2_ID;
	public static final String SEC_2_ISIN = TestGnuCashSecurityImpl.SEC_2_ISIN;

	public static final GCshCmdtyNameSpace.SecIdType SEC_3_SECIDTYPE = TestGnuCashSecurityImpl.SEC_3_SECIDTYPE;
	public static final String SEC_3_ID   = TestGnuCashSecurityImpl.SEC_3_ID;
	public static final String SEC_3_ISIN = TestGnuCashSecurityImpl.SEC_3_ISIN;

	public static final GCshCmdtyNameSpace.SecIdType SEC_4_SECIDTYPE = TestGnuCashSecurityImpl.SEC_4_SECIDTYPE;
	public static final String SEC_4_ID   = TestGnuCashSecurityImpl.SEC_4_ID;
	public static final String SEC_4_ISIN = TestGnuCashSecurityImpl.SEC_4_ISIN;

	// -----------------------------------------------------------------

	private GnuCashWritableFileExtImpl gcshInFile = null;
	private GnuCashFileExtImpl gcshOutFile = null;

	private GCshSecID newID = new GCshSecID("POOPOO", "BEST");

	private GCshSecID secID1 = null;
	private GCshSecID secID2 = null;
	private GCshSecID secID3 = null;
	private GCshSecID secID4 = null;

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
			gcshInFile = new GnuCashWritableFileExtImpl(gcshInFileRaw);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}

		// ---

		secID1 = new GCshSecID_Exchange(SEC_1_EXCH, SEC_1_ID);
		secID2 = new GCshSecID_Exchange(SEC_2_EXCH, SEC_2_ID);
		secID3 = new GCshSecID_SecIdType(SEC_3_SECIDTYPE, SEC_3_ID);
		secID4 = new GCshSecID_SecIdType(SEC_4_SECIDTYPE, SEC_4_ID);
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
	public void test01_1() throws Exception {
		GnuCashWritableSecurity sec = gcshInFile.getWritableSecurityByQualifID(SEC_1_EXCH, SEC_1_ID);
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
	public void test01_2() throws Exception {
		List<GnuCashWritableSecurity> secList = gcshInFile.getWritableSecuritiesByName("mercedes");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());

		assertEquals(secID1.toString(), secList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	        secList.get(0).getQualifID()); // not trivial!
		assertEquals(SEC_1_ISIN, secList.get(0).getXCode());
		assertEquals("Mercedes-Benz Group AG", secList.get(0).getName());

		secList = gcshInFile.getWritableSecuritiesByName("BENZ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         secList.get(0).getQualifID());

		secList = gcshInFile.getWritableSecuritiesByName(" MeRceDeS-bEnZ  ");
		assertNotEquals(null, secList);
		assertEquals(1, secList.size());
		assertEquals(secID1.toString(), secList.get(0).getQualifID().toString());
		// *Not* equal because of class
		assertNotEquals(secID1, secList.get(0).getQualifID());
		// ::TODO: Convert to GCshSecID_Exchange, then it should be equal
		//    assertEquals(secID1, 
		//	         secList.get(0).getQualifID()); // not trivial!
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableSimpleTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	// ::TODO

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------
	
	@Test
	public void test03_1_1() throws Exception {
		assertEquals(ConstTest.Stats.NOF_SEC, gcshInFile.getSecurities().size());

		GnuCashWritableSecurity sec = gcshInFile.createWritableSecurity(newID, "US0123456001", "Best Corp Ever");

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_1_check_memory(sec);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_1_check_persisted(outFile);
	}

	private void test03_1_1_check_memory(GnuCashWritableSecurity sec) throws Exception {
		assertEquals(ConstTest.Stats.NOF_SEC + 1, gcshInFile.getSecurities().size());

		assertEquals(newID.toString(), sec.getQualifID().toString());
		assertEquals("Best Corp Ever", sec.getName());
	}

	private void test03_1_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileExtImpl(outFile);

		assertEquals(ConstTest.Stats.NOF_SEC + 1, gcshInFile.getSecurities().size());

		GnuCashCommodity cmdty = gcshOutFile.getCommodityByQualifID(newID);
		assertNotEquals(null, cmdty);

		assertEquals(newID.toString(), cmdty.getQualifID().toString());
		assertEquals("Best Corp Ever", cmdty.getName());
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	@Test
	public void test03_2_1() throws Exception {
		GnuCashWritableSecurity sec = 
				gcshInFile.createWritableSecurity(
						new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.NASDAQ, "SCAM"),
						"US0123456789",
						"Scam and Screw Corp.");

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		//      System.err.println("Outfile for TestGnuCashWritableCommodityImpl.test01_1: '" + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_2_1_check_1_valid(outFile);
		test03_2_1_check(outFile);
	}

	// -----------------------------------------------------------------

	//  @Test
	//  public void test03_2_2() throws Exception
	//  {
	//      assertNotEquals(null, outFileGlob);
	//      assertEquals(true, outFileGlob.exists());
	//
	//      // Check if generated document is valid
	//      // ::TODO: in fact, not even the input document is.
	//      // Build document
	//      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//      DocumentBuilder builder = factory.newDocumentBuilder(); 
	//      Document document = builder.parse(outFileGlob);
	//      System.err.println("xxxx XML parsed");
	//
	//      // https://howtodoinjava.com/java/xml/read-xml-dom-parser-example/
	//      Schema schema = null;
	//      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	//      SchemaFactory factory1 = SchemaFactory.newInstance(language);
	//      schema = factory1.newSchema(outFileGlob);
	//
	//      Validator validator = schema.newValidator();
	//      DOMResult validResult = null; 
	//      validator.validate(new DOMSource(document), validResult);
	//      System.out.println("yyy: " + validResult);
	//      // assertEquals(validResult);
	//  }

	// Sort of "soft" variant of above function
	// CAUTION: Not platform-independent!
	// Tool "xmllint" must be installed and in path
	private void test03_2_1_check_1_valid(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Check if generated document is valid
		// ProcessBuilder bld = new ProcessBuilder("xmllint", outFile.getAbsolutePath());
		ProcessBuilder bld = new ProcessBuilder("xmlstarlet", "val", outFile.getAbsolutePath() );
		Process prc = bld.start();

		if ( prc.waitFor() == 0 ) {
			assertEquals(0, 0);
		} else {
			assertEquals(0, 1);
		}
	}

	private void test03_2_1_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
		//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
		//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("gnc:commodity");
		assertEquals(ConstTest.Stats.NOF_SEC + 1, nList.getLength()); // <-- CAUTION: includes
		// "template:template"

		// Last (new) node
		Node lastNode = nList.item(nList.getLength() - 1);
		assertEquals(Node.ELEMENT_NODE, lastNode.getNodeType());
		Element elt = (Element) lastNode;
		assertEquals("Scam and Screw Corp.", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
		assertEquals(GCshCmdtyNameSpace.Exchange.NASDAQ.toString(),
				elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
		assertEquals("SCAM", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
	}

	// -----------------------------------------------------------------

	@Test
	public void test03_2_2() throws Exception {
		GnuCashWritableSecurity sec1 = 
				gcshInFile.createWritableSecurity(
						new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.NASDAQ, "SCAM"),
						"US0123456789",
						"Scam and Screw Corp.");

		GnuCashWritableSecurity sec2 = 
				gcshInFile.createWritableSecurity(
						new GCshSecID_MIC(GCshCmdtyNameSpace.MIC.XBRU, "CHOC"),
						"BE0123456789",
						"Chocolaterie de la Grande Place");

		GnuCashWritableSecurity sec3 = 
				gcshInFile.createWritableSecurity(
						new GCshSecID_Exchange(GCshCmdtyNameSpace.Exchange.EURONEXT, "FOUS"),
						"FR0123456789",
						"Ils sont fous ces dingos!");

		GnuCashWritableSecurity sec4 = 
				gcshInFile.createWritableSecurity(
						new GCshSecID_SecIdType(GCshCmdtyNameSpace.SecIdType.ISIN, "GB10000A2222"),
						"GB10000A2222",
						"Ye Ole National British Trade Company Ltd.");

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCommodityImpl.test02_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_2_2_check(outFile);
	}

	private void test03_2_2_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
		//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
		//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("gnc:commodity");
		assertEquals(ConstTest.Stats.NOF_SEC + 4, nList.getLength()); // <-- CAUTION: includes
		// "template:template"

		// Last three nodes (the new ones)
		Node node = nList.item(nList.getLength() - 4);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Element elt = (Element) node;
		assertEquals("Scam and Screw Corp.", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
		assertEquals(GCshCmdtyNameSpace.Exchange.NASDAQ.toString(),
				elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
		assertEquals("SCAM", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
		assertEquals("US0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

		node = nList.item(nList.getLength() - 3);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		elt = (Element) node;
		assertEquals("Chocolaterie de la Grande Place",
				elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
		assertEquals(GCshCmdtyNameSpace.MIC.XBRU.toString(),
				elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
		assertEquals("CHOC", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
		assertEquals("BE0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

		node = nList.item(nList.getLength() - 2);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		elt = (Element) node;
		assertEquals("Ils sont fous ces dingos!", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
		assertEquals(GCshCmdtyNameSpace.Exchange.EURONEXT.toString(),
				elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
		assertEquals("FOUS", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
		assertEquals("FR0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

		node = nList.item(nList.getLength() - 1);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		elt = (Element) node;
		assertEquals("Ye Ole National British Trade Company Ltd.",
				elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
		assertEquals(GCshCmdtyNameSpace.SecIdType.ISIN.toString(),
				elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
		assertEquals("GB10000A2222", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
		assertEquals("GB10000A2222", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());
	}
	
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
