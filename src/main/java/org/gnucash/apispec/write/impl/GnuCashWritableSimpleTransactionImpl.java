package org.gnucash.apispec.write.impl;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableSimpleTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableSimpleTransactionImpl extends GnuCashWritableTransactionImpl 
                                                  implements GnuCashWritableSimpleTransaction 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableSimpleTransactionImpl.class);

    // -----------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableSimpleTransactionImpl(final GnuCashTransactionImpl trx) {
    	super(trx);
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableSimpleTransactionImpl(final GnuCashWritableTransactionImpl trx) {
    	super(trx);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public GnuCashWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getFirstSplit();
    }
    
    /**
     * {@inheritDoc}
     */
    public GnuCashWritableTransactionSplit getWritableSecondSplit()  throws TransactionSplitNotFoundException {
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

    	return (GnuCashWritableTransactionSplit) getSplits().get(1);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
	public GnuCashTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException
	{
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public GnuCashTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException
	{
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
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
