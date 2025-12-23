package org.gnucash.apispec.write;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.apispec.read.GnuCashStockSplitTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransaction
 */
public interface GnuCashWritableStockSplitTransaction extends GnuCashWritableTransaction,
                                                              GnuCashStockSplitTransaction
{

	GnuCashWritableTransactionSplit getWritableSplit() throws TransactionSplitNotFoundException;
	
    // ---------------------------------------------------------------
    
    void setSplitFactor(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setSplitFactor(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofAddShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofAddShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesBeforeSplit(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesBeforeSplit(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesAfterSplit(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesAfterSplit(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
