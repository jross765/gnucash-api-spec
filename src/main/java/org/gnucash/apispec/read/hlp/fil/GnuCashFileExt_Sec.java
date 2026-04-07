package org.gnucash.apispec.read.hlp.fil;

import java.util.List;

import org.gnucash.apispec.read.GnuCashSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashFileExt_Sec {

	GnuCashSecurity getSecurityByID(GCshSecID secID);

	GnuCashSecurity getSecurityByNamSpcCode(String nameSpace, String code);
	
	GnuCashSecurity getSecurityByNamSpcCode(GCshCmdtyNameSpace.Exchange exchange, String tkr);

	GnuCashSecurity getSecurityByNamSpcCode(GCshCmdtyNameSpace.MIC mic, String micID);

	GnuCashSecurity getSecurityByNamSpcCode(GCshCmdtyNameSpace.SecIdType secIdType, String secID);

	GnuCashSecurity getSecurityByXCode(String xCode);

	List<GnuCashSecurity> getSecurities();
	
    List<GnuCashSecurity> getSecuritiesByName(String expr);

    List<GnuCashSecurity> getSecuritiesByName(String expr, boolean relaxed);

    GnuCashSecurity getSecurityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

}
