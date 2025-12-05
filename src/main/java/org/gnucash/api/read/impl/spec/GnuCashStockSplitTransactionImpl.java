package org.gnucash.api.read.impl.spec;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.read.spec.GnuCashStockSplitTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashStockSplitTransactionImpl extends GnuCashTransactionImpl
											  implements GnuCashStockSplitTransaction
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------

	public GnuCashStockSplitTransactionImpl(GncTransaction peer, GnuCashFile gcshFile, boolean addTrxToInvc) {
		super( peer, gcshFile, addTrxToInvc );
		// TODO Auto-generated constructor stub
	}
	
	// ---------------------------------------------------------------

	@Override
	public GnuCashTransactionSplit getSplit() {
		return getSplits().get(0);
	}

	@Override
	protected void addSplit(GnuCashTransactionSplitImpl splt) {
		if ( getSplitsCount() == 1 ) {
			throw new IllegalStateException("This transaction already has a split");
		}
		
		// splt.getActionStr() == null is *not* valid here
		// (as opposed to GnuCashSimpleTransactionImpl),
		// but implicitly checked with the following:
		if ( splt.getAction() != GnuCashTransactionSplit.Action.SPLIT ) {
			throw new IllegalArgumentException("the split's action is not " + GnuCashTransactionSplit.Action.SPLIT);
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.STOCK ) {
			throw new IllegalArgumentException("the split's account's type is not " + GnuCashAccount.Type.STOCK);
		}
		
		if ( splt.getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			throw new IllegalArgumentException("the split's account's commodity/currency is of type " + GCshCmdtyCurrID.Type.CURRENCY);
		}
		
		super.addSplit( splt );
	}

	// ---------------------------------------------------------------
	
	public String toString() {
		// ::TODO
		return "NOT IMPLEMENTED YET";
	}
	
}
