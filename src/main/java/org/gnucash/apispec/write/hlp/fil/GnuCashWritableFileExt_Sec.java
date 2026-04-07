package org.gnucash.apispec.write.hlp.fil;

import java.util.Collection;
import java.util.List;

import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.apispec.write.GnuCashWritableSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshSecID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public interface GnuCashWritableFileExt_Sec
{

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
