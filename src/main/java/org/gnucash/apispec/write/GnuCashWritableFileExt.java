package org.gnucash.apispec.write;

import java.util.Collection;
import java.util.List;

import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.apispec.read.GnuCashFileExt;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashWritableFileExt extends GnuCashWritableFile,
                                                GnuCashFileExt
{

    GnuCashWritableCurrency getWritableCurrencyByID(GCshCurrID currID);

    GnuCashWritableCurrency getWritableCurrencyByISOCode(String isoCode);

    Collection<GnuCashWritableCurrency> getWritableCurrencies();

    // ----------------------------
    
    GnuCashWritableCurrency createWritableCurrency(GCshCurrID currID, String code, String name);

    void removeCurrency(GnuCashWritableCurrency curr) throws ObjectCascadeException;
    
    // ---------------------------------------------------------------
    
    GnuCashWritableSecurity getWritableSecurityByID(GCshSecID secID);

    GnuCashWritableSecurity getWritableSecurityByQualifID(String nameSpace, String code);

    GnuCashWritableSecurity getWritableSecurityByQualifID(GCshCmdtyNameSpace.Exchange exchange, String code);

    GnuCashWritableSecurity getWritableSecurityByQualifID(GCshCmdtyNameSpace.MIC mic, String code);

    GnuCashWritableSecurity getWritableSecurityByQualifID(GCshCmdtyNameSpace.SecIdType secIdType, String code);

    GnuCashWritableSecurity getWritableSecurityByXCode(String xCode);

    List<GnuCashWritableSecurity> getWritableSecuritiesByName(String expr);

    List<GnuCashWritableSecurity> getWritableSecuritiesByName(String expr, boolean relaxed);
    
    GnuCashWritableSecurity getWritableSecurityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
    Collection<GnuCashWritableSecurity> getWritableSecurities();

    // ----------------------------
    
    GnuCashWritableSecurity createWritableSecurity(GCshSecID secID, String code, String name);

    void removeSecurity(GnuCashWritableSecurity sec) throws ObjectCascadeException;
	
}
