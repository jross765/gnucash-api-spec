package org.gnucash.apispec.write;

import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransaction
 */
public interface GnuCashWritableSimpleTransaction extends GnuCashWritableTransaction,
                                                          GnuCashSimpleTransaction
{

    /**
     * @return the first of the two splits.
     * @throws TransactionSplitNotFoundException 
     *  
     * @see #getWritableSecondSplit()
     */
    GnuCashWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException;
    
    /**
     * @return the second of the two splits.
     * @throws TransactionSplitNotFoundException 
     *  
     * @see #getWritableFirstSplit()
     */
    GnuCashWritableTransactionSplit getWritableSecondSplit() throws TransactionSplitNotFoundException;

}
