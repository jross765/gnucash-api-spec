package org.gnucash.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashStockBuyTransaction extends GnuCashTransaction,
												    GnuCashSpecialTransaction
{

    GnuCashTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<GnuCashTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    FixedPointNumber getNofShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getPricePerShare()  throws TransactionSplitNotFoundException;
    
    BigFraction      getPricePerShareRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNetPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetPriceRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getGrossPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossPriceRat()  throws TransactionSplitNotFoundException;
    
}
