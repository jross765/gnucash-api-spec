package org.gnucash.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashStockDividendTransaction extends GnuCashTransaction,
												         GnuCashSpecialTransaction
{

    GnuCashTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<GnuCashTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    @Deprecated
    FixedPointNumber getGrossDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossDividendRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getFeeTax(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeeTaxRat(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    @Deprecated
    FixedPointNumber getNetDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetDividendRat()  throws TransactionSplitNotFoundException;
    
}
