package org.gnucash.apispec.write.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashStockBuyTransactionImpl;
import org.gnucash.apispec.write.GnuCashWritableStockBuyTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableStockBuyTransactionImpl extends GnuCashWritableTransactionImpl 
                                                    implements GnuCashWritableStockBuyTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableStockBuyTransaction.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS_STOCK = 1;
    
    private static final int NOF_SPLITS_FEES_TAXES_MIN = 0;
    private static final int NOF_SPLITS_FEES_TAXES_MAX = 4; // more is implausible

    private static final int NOF_SPLITS_OFFSETTING = 1;
    
    // ---

    private static final int NOF_SPLITS_MIN = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MIN + NOF_SPLITS_OFFSETTING;
    private static final int NOF_SPLITS_MAX = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MAX + NOF_SPLITS_OFFSETTING;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockBuyTransactionImpl(final GnuCashStockBuyTransactionImpl trx) {
    	super(trx);
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockBuyTransactionImpl(final GnuCashWritableStockBuyTransaction trx) {
    	super(trx);
    }

    // ---------------------------------------------------------------
    
	@Override
	public GnuCashWritableTransactionSplit getWritableStockAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getStockAccountSplit();
	}

	@Override
	public List<GnuCashWritableTransactionSplit> getWritableExpensesSplits() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	List<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();
    	
    	for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
    		result.add( (GnuCashWritableTransactionSplit) splt );
    	}
    	
    	return result;
	}

	@Override
	public GnuCashWritableTransactionSplit getWritableOffsettingAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getOffsettingAccountSplit();
	}

    // ---------------------------------------------------------------
    
	@Override
	public GnuCashTransactionSplit getStockAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		for ( GnuCashTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == GnuCashAccount.Type.STOCK ) {
				return splt;
			}
		}
		
		return null;
	}

	@Override
	public List<GnuCashTransactionSplit> getExpensesSplits() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		List<GnuCashTransactionSplit> result = new ArrayList<GnuCashTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == GnuCashAccount.Type.EXPENSE ) {
				result.add(splt);
			}
		}
		
		return result;
	}

	@Override
	public GnuCashTransactionSplit getOffsettingAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		for ( GnuCashTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == GnuCashAccount.Type.BANK ) {
				return splt;
			}
		}
		
		return null;
	}

    // ----------------------------
    
	@Override
	public FixedPointNumber getNofShares() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getQuantity();
	}

	@Override
	public BigFraction getNofSharesRat() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getQuantityRat();
	}

	@Override
	public FixedPointNumber getPricePerShare() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNetPrice();
		
		result.divide( getNofShares() ); // mutable
		
		return result;
	}

	@Override
	public BigFraction getPricePerShareRat() throws TransactionSplitNotFoundException {
    	BigFraction result = getNetPriceRat();
		
		result = result.divide( getNofSharesRat() ); // immutable
		
		return result;
	}

	@Override
	public FixedPointNumber getNetPrice() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getGrossPrice();
		
		result.subtract( getFeesTaxes() ); // mutable
		
		return result;
	}

	@Override
	public BigFraction getNetPriceRat() throws TransactionSplitNotFoundException {
		BigFraction result = getGrossPriceRat();
		
		result = result.subtract( getFeesTaxesRat() ); // immutable
		
		return result;
	}

	@Override
	public FixedPointNumber getFeesTaxes() throws TransactionSplitNotFoundException {
		FixedPointNumber result = FixedPointNumber.ZERO.copy(); // Caution: FPN is mutable!
		
		for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
			result.add( splt.getValue() ); // mutable
		}
		
		return result;
	}

	@Override
	public BigFraction getFeesTaxesRat() throws TransactionSplitNotFoundException {
		BigFraction result = BigFraction.ZERO; // Caution: BF is immutable
		
		for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
			result = result.add( splt.getValueRat() ); // immutable
		}
		
		return result;
	}

	@Override
	public FixedPointNumber getGrossPrice() throws TransactionSplitNotFoundException {
		return getOffsettingAccountSplit().getValue().negate(); // Notice: negate
	}

	@Override
	public BigFraction getGrossPriceRat() throws TransactionSplitNotFoundException {
		return getOffsettingAccountSplit().getValueRat().negate(); // Notice: negate
	}

    // ---------------------------------------------------------------
    
	@Override
	public void setNofShares(final FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableStockAccountSplit().setQuantity(val);
	}

	@Override
	public void setNofShares(final BigFraction val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableStockAccountSplit().setQuantity(val);
	}

	@Override
	public void setPricePerShare(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		FixedPointNumber netPrc = getNofShares().multiply(amt);
		FixedPointNumber feeTax = getFeesTaxes();
		FixedPointNumber grossPrc = netPrc.copy().add(feeTax); // mutable
		setGrossPrice(grossPrc);
	}

	@Override
	public void setPricePerShare(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		BigFraction netPrc = getNofSharesRat().multiply(amt);
		BigFraction feeTax = getFeesTaxesRat();
		BigFraction grossPrc = netPrc.add(feeTax); // immutable
		setGrossPrice(grossPrc);
	}

	@Override
	public void setGrossPrice(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		FixedPointNumber amtNeg = amt.copy().negate(); // mutable
		
		getWritableOffsettingAccountSplit().setQuantity(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	@Override
	public void setGrossPrice(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		BigFraction amtNeg = amt.negate(); // immutable
		
		getWritableOffsettingAccountSplit().setQuantity(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

    // ---------------------------------------------------------------
    
	@Override
	public void validate() throws Exception {
		// ::TODO
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableStockBuyTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", descr='");
		buffer.append(getDescription() + "'");

		buffer.append(", stock-acct=");
		try {
			buffer.append(getStockAccountSplit().getAccount().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", #expenses-splits=");
		try {
			buffer.append(getExpensesSplits().size());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", offset-acct=");
		try {
			buffer.append(getOffsettingAccountSplit().getAccount().getID());
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
