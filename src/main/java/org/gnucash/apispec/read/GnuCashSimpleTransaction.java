package org.gnucash.apispec.read;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashSimpleTransaction extends GnuCashTransaction,
												  GnuCashSpecialTransaction
{

    /**
     *
     * @return the first split of this transaction or null.
     * <br>
     * <em>Caution</em>: This only makes sense for simple transactions
     * that consist of only two splits. 
     * By no means is that guaranteed or even "normal"!
     *  
     * @throws TransactionSplitNotFoundException
     * 
     * @see #getSecondSplit()
     * @see #getSplits()
     * @see #getSplitsCount()
    */
    GnuCashTransactionSplit getFirstSplit()  throws TransactionSplitNotFoundException;
    
    /**
     * @return the second split of this transaction or null.
     * <br>
     * <em>Caution</em>: This only makes sense for simple transactions
     * that consist of only two splits. 
     * By no means is that guaranteed or even "normal"!
     * 
     * @throws TransactionSplitNotFoundException 
     *
     * @see #getFirstSplit()
     * @see #getSplits()
     * @see #getSplitsCount()
     */
	GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException;

    // ---------------------------------------------------------------
    
    FixedPointNumber getAmount() throws TransactionSplitNotFoundException;
    
    BigFraction      getAmountRat() throws TransactionSplitNotFoundException;
}
