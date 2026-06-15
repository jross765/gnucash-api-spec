package org.gnucash.apispec.read.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.apispec.read.GnuCashStockBuyTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xyz
 * 
 * @see GnuCashTransaction
 */
public class GnuCashStockBuyTransactionImpl extends GnuCashStockBuySellTransactionImpl
                                             implements GnuCashStockBuyTransaction
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashStockBuyTransactionImpl.class);

	// ---------------------------------------------------------------
    
	public GnuCashStockBuyTransactionImpl(final GnuCashTransactionImpl trx) {
		super( trx );
	}

	public GnuCashStockBuyTransactionImpl(final GnuCashStockBuyTransactionImpl trx) {
		super(trx);
		
//		init();
//		
//		try {
//			validate();
//		} catch ( TransactionValidationException exc ) {
//			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
//		} catch ( Exception exc ) {
//			throw new IllegalArgumentException("argument <trx>: something went wrong");
//		}
	}

	// ---------------------------------------------------------------
    
	@Override
	protected void validateStockAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		super.validateStockAcctSplit(splt);

		if ( splt.getQuantityRat().compareTo(BigFraction.ZERO) <= 0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) <= 0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}

	@Override
	protected void validateOffsettingAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		super.validateOffsettingAcctSplit(splt);
		
		if ( splt.getQuantityRat().compareTo(BigFraction.ZERO) >= 0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) >= 0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public String toString() {
		String result = super.toString();
		result = result.replaceAll( "GnuCashStockBuySellTransactionImpl", "GnuCashStockBuyTransactionImpl" );
		return result;
	}

	public String toStringHuman() {
		String result = super.toStringHuman();
		result = result.replaceAll( "Stock-buy-sell", "Stock-buy" );
		return result;
	}

}
