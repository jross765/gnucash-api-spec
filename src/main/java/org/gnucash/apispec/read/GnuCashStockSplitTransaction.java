package org.gnucash.apispec.read;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashStockSplitTransaction extends GnuCashTransaction,
													  GnuCashSpecialTransaction
{

	GnuCashTransactionSplit getSplit() throws TransactionSplitNotFoundException;
	
    // ---------------------------------------------------------------
    
    FixedPointNumber getSplitFactor()  throws TransactionSplitNotFoundException;
    
    BigFraction      getSplitFactorRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofAddShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofAddSharesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofSharesBeforeSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesBeforeSplitRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofSharesAfterSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesAfterSplitRat()  throws TransactionSplitNotFoundException;
    
}
