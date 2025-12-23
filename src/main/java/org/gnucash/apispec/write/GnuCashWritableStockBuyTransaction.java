package org.gnucash.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.apispec.read.GnuCashStockBuyTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransaction
 */
public interface GnuCashWritableStockBuyTransaction extends GnuCashWritableTransaction,
                                                            GnuCashStockBuyTransaction
{

    GnuCashWritableTransactionSplit       getWritableStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<GnuCashWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setNofShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNetPrice(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNetPrice(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
