package org.gnucash.apispec.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashStockBuyTransactionImpl;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableStockSellTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableStockSellTransactionImpl extends GnuCashWritableStockBuySellTransactionImpl 
                                                     implements GnuCashWritableStockSellTransaction
{

	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableStockSellTransactionImpl.class);

	// ---------------------------------------------------------------
    
    public GnuCashWritableStockSellTransactionImpl(final GnuCashStockBuyTransactionImpl trx) {
    	super(trx);
    	
//    	init();
//    	
//		try {
//			validate();
//		} catch ( TransactionValidationException exc ) {
//			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-sell transaction");
//		} catch ( Exception exc ) {
//			throw new IllegalArgumentException("argument <trx>: something went wrong");
//		}
    }

	public GnuCashWritableStockSellTransactionImpl(final GnuCashWritableStockBuySellTransactionImpl trx) {
		super(trx);
		
//		init();
//		
//		try {
//			validate();
//		} catch ( TransactionValidationException exc ) {
//			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-sell transaction");
//		} catch ( Exception exc ) {
//			throw new IllegalArgumentException("argument <trx>: something went wrong");
//		}
	}

	// ---------------------------------------------------------------
    
	@Override
	protected void validateStockAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		super.validateStockAcctSplit(splt);
		
		if ( splt.getQuantityRat().compareTo(BigFraction.ZERO) >= 0 ) { // sic
			String msg = "the split's shares is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) >= 0 ) { // sic
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	@Override
	protected void validateOffsettingAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		super.validateOffsettingAcctSplit(splt);

		if ( splt.getQuantityRat().compareTo(BigFraction.ZERO) <= 0 ) { // sic
			String msg = "the split's shares is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) <= 0 ) { // sic
			String msg = "the split's value is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public String toString() {
		String result = super.toString();
		result = result.replaceAll( "GnuCashWritableStockBuySellTransactionImpl", "GnuCashWritableStockSellTransactionImpl" );
		return result;
	}

}
