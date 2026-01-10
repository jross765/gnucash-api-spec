package org.gnucash.apispec.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableSimpleTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableSimpleTransactionImpl extends GnuCashWritableTransactionImpl 
                                                  implements GnuCashWritableSimpleTransaction 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableSimpleTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 2;

    // -----------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableSimpleTransactionImpl(final GnuCashSimpleTransactionImpl trx) {
    	super(trx);
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableSimpleTransactionImpl(final GnuCashWritableSimpleTransactionImpl trx) {
    	super(trx);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public GnuCashWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() <= 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getFirstSplit();
    }
    
    /**
     * {@inheritDoc}
     */
    public GnuCashWritableTransactionSplit getWritableSecondSplit()  throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() <= 1 )
			throw new TransactionSplitNotFoundException();

    	return (GnuCashWritableTransactionSplit) getSplits().get(1);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
	public GnuCashTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() < 1 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() < 2 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
	}

    // ----------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
	public FixedPointNumber getAmount() throws TransactionSplitNotFoundException {
    	return getSecondSplit().getValue();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public BigFraction getAmountRat() throws TransactionSplitNotFoundException {
    	return getSecondSplit().getValueRat();
	}

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
    public void setAmount(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		FixedPointNumber amtNeg = amt.copy().negate(); // Caution: FixedPointNumber is mutable!
		
    	getWritableFirstSplit().setQuantity(amtNeg);
    	getWritableFirstSplit().setValue(amtNeg);
		
    	getWritableSecondSplit().setQuantity(amt);
    	getWritableSecondSplit().setValue(amt);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public void setAmount(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		BigFraction amtNeg = amt.negate();
		
    	getWritableFirstSplit().setQuantity(amtNeg);
    	getWritableFirstSplit().setValue(amtNeg);
		
    	getWritableSecondSplit().setQuantity(amt);
    	getWritableSecondSplit().setValue(amt);
    }

    // ---------------------------------------------------------------
    
	@Override
	public void validate() throws Exception {
		if ( getSplitsCount() != NOF_SPLITS ) {
			String msg = "Trx ID " + getID() + ": Number of splits is not " + NOF_SPLITS;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getAccount().getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Commodity/currency of first split's account is not of type '" + GCshCmdtyCurrID.Type.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSecondSplit().getAccount().getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Commodity/currency of second split's account is not of type '" + GCshCmdtyCurrID.Type.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( ! getFirstSplit().getAccount().getCmdtyCurrID().getCode().equals( getSecondSplit().getAccount().getCmdtyCurrID().getCode() ) ) {
			String msg = "Trx ID " + getID() + ": Commodity/currency code of the two splits are not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getQuantityRat().doubleValue() != getSecondSplit().getQuantityRat().negate().doubleValue() ) {
			String msg = "Trx ID " + getID() + ": Shares of first split is not equal to negative quantity of second split";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getFirstSplit().getValueRat().doubleValue() != getSecondSplit().getValueRat().negate().doubleValue() ) {
			String msg = "Trx ID " + getID() + ": Value of first split is not equal to negative value of second split";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getQuantityRat().signum() != getFirstSplit().getValueRat().signum() ) {
			String msg = "Trx ID " + getID() + ": Signum of first split's shares and value are not is not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSecondSplit().getQuantityRat().signum() != getSecondSplit().getValueRat().signum() ) {
			String msg = "Trx ID " + getID() + ": Signum of second split's shares and value are not is not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		// redundant:
		
		if ( getBalance().doubleValue() != 0.0 ) {
			String msg = "Trx ID :" + getID() + ": Transaction is not balanced: " + getBalance();
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableSimpleTransactionImpl [");

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

		buffer.append(", amount=");
		try {
			buffer.append(getAmount());
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
