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
    
//    void setGrossDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
//    
//    void setGrossDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
//    
//    void setFeesTaxes(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
//    
//    void setFeesTaxes(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
}
