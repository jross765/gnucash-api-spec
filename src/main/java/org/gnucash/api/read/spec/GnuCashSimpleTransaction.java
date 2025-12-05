package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface GnuCashSimpleTransaction extends GnuCashTransaction
{

    public GnuCashTransactionSplit getFirstSplit()  throws TransactionSplitNotFoundException;
    
	public GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException;

}
