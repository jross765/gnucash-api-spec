package org.gnucash.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashStockBuyTransaction extends GnuCashTransaction,
												    GnuCashSpecialTransaction
{

    GnuCashTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<GnuCashTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    @Deprecated
    FixedPointNumber getNofShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getPricePerShare()  throws TransactionSplitNotFoundException;
    
    BigFraction      getPricePerShareRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getNetPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetPriceRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getFeeTax(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeeTaxRat(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getGrossPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossPriceRat()  throws TransactionSplitNotFoundException;
    
}
