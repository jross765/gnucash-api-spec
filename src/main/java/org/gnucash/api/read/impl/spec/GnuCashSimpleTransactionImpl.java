package org.gnucash.api.read.impl.spec;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.read.spec.GnuCashSimpleTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashSimpleTransactionImpl extends GnuCashTransactionImpl
										  implements GnuCashSimpleTransaction
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashSimpleTransactionImpl.class);

	// ---------------------------------------------------------------

	public GnuCashSimpleTransactionImpl(GncTransaction peer, GnuCashFile gcshFile, boolean addTrxToInvc) {
		super( peer, gcshFile, addTrxToInvc );
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------------------------------------
	
	@Override
	protected void addSplit(GnuCashTransactionSplitImpl splt) {
		if ( getSplitsCount() == 2 ) {
			throw new IllegalStateException("This transaction already has two splits");
		}

		if ( splt.getActionStr() != null ) { // null is valid!
			if ( splt.getAction() != GnuCashTransactionSplit.Action.INCREASE ||
				 splt.getAction() != GnuCashTransactionSplit.Action.DECREASE ||
				 
				 splt.getAction() != GnuCashTransactionSplit.Action.INVOICE ||
				 splt.getAction() != GnuCashTransactionSplit.Action.BILL ||
				 splt.getAction() != GnuCashTransactionSplit.Action.VOUCHER ||
					      
				 splt.getAction() != GnuCashTransactionSplit.Action.BUY ||
				 splt.getAction() != GnuCashTransactionSplit.Action.SELL || 
				 splt.getAction() != GnuCashTransactionSplit.Action.EQUITY || 
					 
				 splt.getAction() != GnuCashTransactionSplit.Action.PRICE ||
				 splt.getAction() != GnuCashTransactionSplit.Action.FEE ||
				 splt.getAction() != GnuCashTransactionSplit.Action.DIVIDEND ||
				 splt.getAction() != GnuCashTransactionSplit.Action.LTCG ||
				 splt.getAction() != GnuCashTransactionSplit.Action.STCG ||
				 splt.getAction() != GnuCashTransactionSplit.Action.INCOME ||
				 splt.getAction() != GnuCashTransactionSplit.Action.DIST ||
				 splt.getAction() != GnuCashTransactionSplit.Action.SPLIT ) {
					throw new IllegalArgumentException("the split's action is not valid");
				}
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.BANK &&
			 splt.getAccount().getType() != GnuCashAccount.Type.CASH &&
			 splt.getAccount().getType() != GnuCashAccount.Type.CREDIT ) {
			throw new IllegalArgumentException("the split's account's type is not valid");
		}
		
		if ( splt.getAccount().getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			throw new IllegalArgumentException("the split's account's commodity/currency is not of type " + GCshCmdtyCurrID.Type.CURRENCY);
		}
		
		super.addSplit( splt );
	}

	// ---------------------------------------------------------------
	
	public String toString() {
		// ::TODO
		return "NOT IMPLEMENTED YET";
	}
	
}
