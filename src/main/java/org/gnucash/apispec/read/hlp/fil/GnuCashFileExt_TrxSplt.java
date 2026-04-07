package org.gnucash.apispec.read.hlp.fil;

import java.util.Currency;
import java.util.List;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;

public interface GnuCashFileExt_TrxSplt {

    /**
     * @param secID 
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given security. 
     */
    List<GnuCashTransactionSplit> getTransactionSplitsBySecID(GCshSecID secID);

    /**
     * @param currID 
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given currency. 
     */
    List<GnuCashTransactionSplit> getTransactionSplitsByCurrID(GCshCurrID currID);

    /**
     * @param curr
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given curreny. 
     */
    List<GnuCashTransactionSplit> getTransactionSplitsByCurr(Currency curr);

}
