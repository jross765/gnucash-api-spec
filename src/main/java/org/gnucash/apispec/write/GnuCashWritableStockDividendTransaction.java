package org.gnucash.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.apispec.read.GnuCashStockDividendTransaction;
import org.gnucash.base.basetypes.simple.GCshAcctID;

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
    
    GnuCashWritableTransactionSplit       getWritableExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<GnuCashWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setStockAcctID(GCshAcctID stockAcctID) throws TransactionSplitNotFoundException;
    
    void setStockAcct(GnuCashAccount stockAcct) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcctID(GCshAcctID offsettingAcctID) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcct(GnuCashAccount pffsettingAcct) throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setGrossDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void addFeeTax(GCshAcctID expAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void addFeeTax(GCshAcctID expAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void clearFeesTaxes() throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setNetDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void refreshNetDividend() throws TransactionSplitNotFoundException;
    
}
