package org.gnucash.apispec.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashStockSplitTransactionImpl;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableStockSplitTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableStockSplitTransactionImpl extends GnuCashWritableTransactionImpl 
                                                      implements GnuCashWritableStockSplitTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 1;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockSplitTransactionImpl(final GnuCashStockSplitTransactionImpl trx) {
    	super(trx);
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-split transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockSplitTransactionImpl(final GnuCashWritableStockSplitTransaction trx) {
    	super(trx);
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-split transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    // ---------------------------------------------------------------
    
	@Override
	public GnuCashTransactionSplit getSplit() throws TransactionSplitNotFoundException
	{
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		return getSplits().get(0);
	}

	@Override
	public GnuCashWritableTransactionSplit getWritableSplit() throws TransactionSplitNotFoundException
	{
    	return (GnuCashWritableTransactionSplit) getSplit();
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
	public void setSplitFactor(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		FixedPointNumber nofShrAfter = getNofSharesBeforeSplit().multiply(val);
		FixedPointNumber nofAddShr   = nofShrAfter.subtract( getNofSharesBeforeSplit() );
		setNofAddShares(nofAddShr);
	}

	@Override
	public void setSplitFactor(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		BigFraction nofShrAfter = getNofSharesBeforeSplitRat().multiply(val);
		BigFraction nofAddShr   = nofShrAfter.subtract( getNofSharesBeforeSplitRat() );
		setNofAddShares(nofAddShr);
	}

	@Override
	public void setNofAddShares(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableSplit().setQuantity(val);
	}

	@Override
	public void setNofAddShares(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableSplit().setQuantity(val);
	}

	@Override
	public void setNofSharesAfterSplit(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		FixedPointNumber nofAddShr = val.subtract( getNofSharesBeforeSplit() );
		setNofAddShares(nofAddShr);
	}

	@Override
	public void setNofSharesAfterSplit(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		BigFraction nofAddShr = val.subtract( getNofSharesBeforeSplitRat() );
		setNofAddShares(nofAddShr);
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
		// (as opposed to GnuCashStockSplitTransactionImpl),
		// but implicitly checked with the following:
		if ( getSplit().getAction() != GnuCashTransactionSplit.Action.SPLIT ) {
			throw new IllegalArgumentException("the split's action is not " + GnuCashTransactionSplit.Action.SPLIT);
		}
		
		if ( getSplit().getAccount().getType() != GnuCashAccount.Type.STOCK ) {
			throw new IllegalArgumentException("the split's account's type is not " + GnuCashAccount.Type.STOCK);
		}
		
		if ( getSplit().getAccount().getCmdtyID().getType() == GCshCmdtyID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Security/currency of first split's account is of type '" + GCshCmdtyNameSpace.CURRENCY + "'";
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
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableStockSplitTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

//		buffer.append(", split=");
//		try {
//			buffer.append(getSplit().getID());
//		} catch (Exception e) {
//			buffer.append("ERROR");
//		}

		buffer.append(", stock-acct=");
		try {
			buffer.append(getSplit().getAccount().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", factor=");
		try {
			buffer.append(getSplitFactor());
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
