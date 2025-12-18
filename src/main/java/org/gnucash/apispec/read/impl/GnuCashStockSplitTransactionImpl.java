package org.gnucash.apispec.read.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.apispec.read.GnuCashStockSplitTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz
 * 
 * @see GnuCashTransaction
 */
public class GnuCashStockSplitTransactionImpl extends GnuCashTransactionImpl
											  implements GnuCashStockSplitTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 1;

	// ---------------------------------------------------------------

	public GnuCashStockSplitTransactionImpl(GnuCashTransactionImpl trx) {
		super( trx );
		
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
	}
	
	// ---------------------------------------------------------------

	@Override
	protected void addSplit(GnuCashTransactionSplitImpl splt) {
		if ( getSplitsCount() == NOF_SPLITS ) {
			throw new IllegalStateException("This transaction already has a split");
		}
		
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
		
		super.addSplit( splt );
	}

	// ---------------------------------------------------------------
	
	@Override
	public void validate() throws Exception
	{
		if ( getSplitsCount() != NOF_SPLITS ) {
			String msg = "Trx ID " + getID() + ": Number of splits is not " + NOF_SPLITS;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// splt.getActionStr() == null is *not* valid here
		// (as opposed to GnuCashSimpleTransactionImpl),
		// but implicitly checked with the following:
		if ( getSplit().getAction() != GnuCashTransactionSplit.Action.SPLIT ) {
			throw new IllegalArgumentException("the split's action is not " + GnuCashTransactionSplit.Action.SPLIT);
		}
		
		if ( getSplit().getAccount().getType() != GnuCashAccount.Type.STOCK ) {
			throw new IllegalArgumentException("the split's account's type is not " + GnuCashAccount.Type.STOCK);
		}
		
		if ( getSplit().getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Commodity/currency of first split's account is of type '" + GCshCmdtyCurrNameSpace.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplit().getQuantityRat().doubleValue() == 0.0 ) {
			String msg = "Trx ID " + getID() + ": Quantity of the split is = 0";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplit().getValueRat().doubleValue() != 0.0 ) {
			String msg = "Trx ID " + getID() + ": Value of the split is != 0";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
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
	
	@Override
	public FixedPointNumber getSplitFactor() throws TransactionSplitNotFoundException {
		return getNofSharesAfterSplit().divide( getNofSharesBeforeSplit() );
	}

	@Override
	public BigFraction getSplitFactorRat() throws TransactionSplitNotFoundException {
		return getNofSharesAfterSplitRat().divide( getNofSharesBeforeSplitRat() );
	}

	@Override
	public FixedPointNumber getNofAddShares() throws TransactionSplitNotFoundException {
		return getSplit().getQuantity();
	}

	@Override
	public BigFraction getNofAddSharesRat() throws TransactionSplitNotFoundException {
		return getSplit().getQuantityRat();
	}
	
	@Override
	public FixedPointNumber getNofSharesBeforeSplit() throws TransactionSplitNotFoundException {
		GnuCashAccount acct = getSplit().getAccount();
		return acct.getBalance(getPreviousSplit());
	}

	@Override
	public BigFraction getNofSharesBeforeSplitRat() throws TransactionSplitNotFoundException {
		GnuCashAccount acct = getSplit().getAccount();
		return acct.getBalanceRat(getPreviousSplit());
	}

	@Override
	public FixedPointNumber getNofSharesAfterSplit() throws TransactionSplitNotFoundException {
		GnuCashAccount acct = getSplit().getAccount();
		return acct.getBalance(getSplit());
	}
	
	@Override
	public BigFraction getNofSharesAfterSplitRat() throws TransactionSplitNotFoundException {
		GnuCashAccount acct = getSplit().getAccount();
		return acct.getBalanceRat(getSplit());
	}
	
	// ----------------------------
	
	public GnuCashTransactionSplit getPreviousSplit() throws TransactionSplitNotFoundException {
		GnuCashAccount acct = getSplit().getAccount();
		
		GnuCashTransactionSplit prevSplt = null;
		for ( GnuCashTransactionSplit splt : acct.getTransactionSplits() ) {
			if ( splt.getID().equals( getSplit().getID() )) {
				return prevSplt;
			}
			
			prevSplt = splt;
		}
		
		return null;
	}

	// ---------------------------------------------------------------
	
	@Override
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
