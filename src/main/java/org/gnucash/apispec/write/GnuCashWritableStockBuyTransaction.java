package org.gnucash.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.apispec.read.GnuCashStockBuyTransaction;
import org.gnucash.base.basetypes.simple.GCshAcctID;

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
    
    GnuCashWritableTransactionSplit       getWritableExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<GnuCashWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setStockAcctID(GCshAcctID stockAcctID) throws TransactionSplitNotFoundException;
    
    void setStockAcct(GnuCashAccount stockAcct) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcctID(GCshAcctID offsettingAcctID) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcct(GnuCashAccount pffsettingAcct) throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setNofShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(GCshAcctID stockAcctID, FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(GCshAcctID stockAcctID, BigFraction val)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void setPricePerShare(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(GCshAcctID stockAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(GCshAcctID stockAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void addFeeTax(GCshAcctID expAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void addFeeTax(GCshAcctID expAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void clearFeesTaxes() throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setGrossPrice(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(GCshAcctID offsettingAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(GCshAcctID offsettingAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void refreshGrossPrice() throws TransactionSplitNotFoundException;
    
}
