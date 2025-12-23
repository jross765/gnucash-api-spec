package org.gnucash.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.apispec.read.GnuCashStockDividendTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransaction
 */
public interface GnuCashWritableStockDividendTransaction extends GnuCashWritableTransaction,
                                                                 GnuCashStockDividendTransaction
{

    GnuCashWritableTransactionSplit       getWritableStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    GnuCashWritableTransactionSplit       getWritableIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<GnuCashWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setGrossDividend(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setGrossDividend(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
