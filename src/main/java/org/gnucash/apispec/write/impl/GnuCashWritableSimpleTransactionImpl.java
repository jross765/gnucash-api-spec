package org.gnucash.apispec.write.impl;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableSimpleTransaction;
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
