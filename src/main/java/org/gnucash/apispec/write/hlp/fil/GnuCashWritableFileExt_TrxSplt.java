package org.gnucash.apispec.write.hlp.fil;

import java.util.Currency;
import java.util.List;

import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;

public interface GnuCashWritableFileExt_TrxSplt
{

    /**
     * @param secID 
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given security. 
     */
    List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsBySecID(GCshSecID secID);

    /**
     * @param currID 
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given currency. 
     */
    List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsByCurrID(GCshCurrID currID);

    /**
     * @param curr
     * @return list of all transaction splits (ro-objects)
     *   denominated in the given curreny. 
     */
    List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsByCurr(Currency curr);

}
