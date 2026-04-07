package org.gnucash.apispec.write.hlp.fil;

import java.util.Collection;

import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.apispec.write.GnuCashWritableCurrency;
import org.gnucash.base.basetypes.complex.GCshCurrID;

public interface GnuCashWritableFileExt_Curr {

    GnuCashWritableCurrency getWritableCurrencyByID(GCshCurrID currID);

    GnuCashWritableCurrency getWritableCurrencyByISOCode(String isoCode);

    Collection<GnuCashWritableCurrency> getWritableCurrencies();

    // ----------------------------
    
    GnuCashWritableCurrency createWritableCurrency(GCshCurrID currID, String code, String name);

    void removeCurrency(GnuCashWritableCurrency curr) throws ObjectCascadeException;
    
}
