package org.gnucash.apispec.read;

import java.util.List;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface GnuCashStockBuyTransaction extends GnuCashTransaction,
												    GnuCashSpecialTransaction
{

    public GnuCashTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    public List<GnuCashTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    public GnuCashTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
}
