package org.gnucash.apispec.read;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface GnuCashStockSplitTransaction extends GnuCashTransaction
{

	public GnuCashTransactionSplit getSplit() throws TransactionSplitNotFoundException;
	
}
