package org.gnucash.apispec.read.impl;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.apispec.read.GnuCashStockSplitTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz
 * 
 * @see GnuCashTransaction
 */
public class GnuCashStockSplitTransactionImpl extends GnuCashTransactionImpl
											  implements GnuCashStockSplitTransaction
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------

	public GnuCashStockSplitTransactionImpl(GnuCashTransactionImpl trx) {
		super( trx );
		
		// ::TODO ::CHECK
		if ( trx.getSplitsCount() != 1 ) {
			throw new IllegalStateException("argument <trx> must have one split -- not more, not less");
		}
	}
	
	// ---------------------------------------------------------------

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
	
	@Override
	public void validate() throws Exception
	{
		if ( getSplitsCount() != 1 ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Number of splits is not 1");
			throw new TransactionValidationException();
		}
		
		if ( getSplit().getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Commodity/currency of first split's account is of type '" + GCshCmdtyCurrID.Type.CURRENCY + "'");
			throw new TransactionValidationException();
		}
		
		if ( getSplit().getQuantityRat().doubleValue() == 0.0 ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Quantity of the split is = 0");
			throw new TransactionValidationException();
		}
		
		if ( getSplit().getValueRat().doubleValue() != 0.0 ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Value of the split is != 0");
			throw new TransactionValidationException();
		}
	}
	
	// ---------------------------------------------------------------
	
    /**
     * {@inheritDoc}
     */
	@Override
	public GnuCashTransactionSplit getSplit() throws TransactionSplitNotFoundException {
		return getSplits().get(0);
	}

	// ---------------------------------------------------------------
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashStockSplitTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", split=");
		try {
			buffer.append(getSplit().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", date-posted=");
		try {
			buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDatePosted().toString());
		}

		buffer.append(", date-entered=");
		try {
			buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDateEntered().toString());
		}

		buffer.append("]");

		return buffer.toString();
	}
	
}
