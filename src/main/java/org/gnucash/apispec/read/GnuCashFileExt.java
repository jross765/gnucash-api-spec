package org.gnucash.apispec.read;

import java.util.List;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFileExt extends GnuCashFile
{

	GnuCashCurrency getCurrencyByID(GCshCurrID currID);
	
	GnuCashCurrency getCurrencyByISOCode(String expr);

	List<GnuCashCurrency> getCurrencies();
	
	// ---------------------------------------------------------------
	
	GnuCashSecurity getSecurityByID(GCshSecID secID);

	GnuCashSecurity getSecurityByQualifID(String nameSpace, String code);
	
	GnuCashSecurity getSecurityByQualifID(GCshCmdtyNameSpace.Exchange exchange, String code);

	GnuCashSecurity getSecurityByQualifID(GCshCmdtyNameSpace.MIC mic, String code);

	GnuCashSecurity getSecurityByQualifID(GCshCmdtyNameSpace.SecIdType secIdType, String code);

	GnuCashSecurity getSecurityByXCode(String xCode);

	List<GnuCashSecurity> getSecurities();
	
    List<GnuCashSecurity> getSecuritiesByName(String expr);

    List<GnuCashSecurity> getSecuritiesByName(String expr, boolean relaxed);

    GnuCashSecurity getSecurityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

}
