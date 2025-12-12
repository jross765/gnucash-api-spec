package org.gnucash.apispec.read.impl;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.apispec.read.GnuCashSimpleTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz
 * 
 * @see GnuCashTransaction
 */
public class GnuCashSimpleTransactionImpl extends GnuCashTransactionImpl
										  implements GnuCashSimpleTransaction
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashSimpleTransactionImpl.class);

	// ---------------------------------------------------------------

	public GnuCashSimpleTransactionImpl(GnuCashTransactionImpl trx) {
		super(trx);
		
		// ::TODO ::CHECK
		if ( trx.getSplitsCount() != 2 ) {
			throw new IllegalStateException("argument <trx> must have two splits -- not more, not less");
		}
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
	
	@Override
	public void validate() throws Exception
	{
		if ( getSplitsCount() != 2 ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Number of splits is not 2");
			throw new TransactionValidationException();
		}
		
		// ---
		
		if ( getFirstSplit().getAccount().getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Commodity/currency of first split's account is not of type '" + GCshCmdtyCurrID.Type.CURRENCY + "'");
			throw new TransactionValidationException();
		}
		
		if ( getSecondSplit().getAccount().getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Commodity/currency of second split's account is not of type '" + GCshCmdtyCurrID.Type.CURRENCY + "'");
			throw new TransactionValidationException();
		}
		
		if ( ! getFirstSplit().getAccount().getCmdtyCurrID().getCode().equals( getSecondSplit().getAccount().getCmdtyCurrID().getCode() ) ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Commodity/currency code of the two splits are not equal");
			throw new TransactionValidationException();
		}
		
		// ---
		
		if ( getFirstSplit().getQuantityRat().doubleValue() != getSecondSplit().getQuantityRat().negate().doubleValue() ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Quantity of first split is not equal to negative quantity of second split");
			throw new TransactionValidationException();
		}
		
		if ( getFirstSplit().getValueRat().doubleValue() != getSecondSplit().getValueRat().negate().doubleValue() ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Value of first split is not equal to negative value of second split");
			throw new TransactionValidationException();
		}
		
		// ---
		
		if ( getFirstSplit().getQuantityRat().signum() != getFirstSplit().getValueRat().signum() ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Signum of first split's quantity and value are not is not equal");
			throw new TransactionValidationException();
		}
		
		if ( getSecondSplit().getQuantityRat().signum() != getSecondSplit().getValueRat().signum() ) {
			LOGGER.error("validate: Trx ID :" + getID() + " Signum of second split's quantity and value are not is not equal");
			throw new TransactionValidationException();
		}
	}
	
	// ---------------------------------------------------------------
	
    /**
     * {@inheritDoc}
     */
	@Override
    public GnuCashTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
    }

	// ---------------------------------------------------------------
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashSimpleTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", split1=");
		try {
			buffer.append(getFirstSplit().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", split2=");
		try {
			buffer.append(getSecondSplit().getID());
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
