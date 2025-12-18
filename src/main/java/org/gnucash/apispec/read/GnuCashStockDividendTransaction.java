package org.gnucash.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashStockDividendTransaction extends GnuCashTransaction,
												         GnuCashSpecialTransaction
{

    GnuCashTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<GnuCashTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    GnuCashTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    FixedPointNumber getGrossDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossDividendRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNetDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetDividendRat()  throws TransactionSplitNotFoundException;
    
}
