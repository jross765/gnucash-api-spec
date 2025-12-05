package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;

public interface GnuCashStockSplitTransaction extends GnuCashTransaction
{

	public GnuCashTransactionSplit getSplit();
	
}
