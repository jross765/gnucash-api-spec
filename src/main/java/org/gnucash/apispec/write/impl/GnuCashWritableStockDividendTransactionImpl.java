package org.gnucash.apispec.write.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashSimpleTransactionImpl;
import org.gnucash.apispec.read.impl.GnuCashStockDividendTransactionImpl;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableStockDividendTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see GnuCashSimpleTransactionImpl
 */
public class GnuCashWritableStockDividendTransactionImpl extends GnuCashWritableTransactionImpl 
                                                         implements GnuCashWritableStockDividendTransaction
{
	public enum SplitAccountType {
		STOCK,
		INCOME,
		TAXES_FEES,
		OFFSETTING
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableStockDividendTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS_STOCK = 1;
    
    private static final int NOF_SPLITS_INCOME = 1;
    
    private static final int NOF_SPLITS_FEES_TAXES_MIN = 0;
    private static final int NOF_SPLITS_FEES_TAXES_MAX = 4; // more is implausible

    private static final int NOF_SPLITS_OFFSETTING = 1;
    
    // ---

    private static final int NOF_SPLITS_MIN = NOF_SPLITS_STOCK + NOF_SPLITS_INCOME + NOF_SPLITS_FEES_TAXES_MIN + NOF_SPLITS_OFFSETTING;
    private static final int NOF_SPLITS_MAX = NOF_SPLITS_STOCK + NOF_SPLITS_INCOME + NOF_SPLITS_FEES_TAXES_MAX + NOF_SPLITS_OFFSETTING;

	// ---------------------------------------------------------------
    
    private int[] splitCounter;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockDividendTransactionImpl(final GnuCashStockDividendTransactionImpl trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-dividend transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockDividendTransactionImpl(final GnuCashWritableStockDividendTransaction trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-dividend transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    // ---------------------------------------------------------------
    
    // ::TODO: Redundant to GnuCashStockBuyTransactionImpl.init()
    protected void init() {
	    splitCounter = new int[SplitAccountType.values().length];
	    
	    for ( SplitAccountType type : SplitAccountType.values() ) {
	    	splitCounter[type.ordinal()] = 0;
	    }
	    
	    try
		{
			if ( getStockAccountSplit() != null ) {
				splitCounter[SplitAccountType.STOCK.ordinal()] = 1;
			}
			
		    if ( getExpensesSplits().size() != 0 ) {
		    	splitCounter[SplitAccountType.TAXES_FEES.ordinal()] = getExpensesSplits().size();
		    }

		    if ( getOffsettingAccountSplit() != null ) {
		    	splitCounter[SplitAccountType.OFFSETTING.ordinal()] = 1;
		    }
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	public GnuCashWritableTransactionSplit getWritableStockAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getStockAccountSplit();
	}

	@Override
	public GnuCashWritableTransactionSplit getWritableIncomeAccountSplit() throws TransactionSplitNotFoundException
	{
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (GnuCashWritableTransactionSplit) getIncomeAccountSplit();
	}

	@Override
    public GnuCashWritableTransactionSplit getWritableExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException {
    	for ( GnuCashWritableTransactionSplit splt : getWritableExpensesSplits() ) {
    		if ( splt.getAccountID().equals( expAcctID ) ) {
    			return splt;
    		}
    	}
    	
    	throw new TransactionSplitNotFoundException();
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
	public GnuCashTransactionSplit getIncomeAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		for ( GnuCashTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == GnuCashAccount.Type.INCOME ) {
				return splt;
			}
		}
		
		return null;
	}

    @Override
    public GnuCashTransactionSplit getExpensesSplit(GCshAcctID expAcctID)  throws TransactionSplitNotFoundException {
    	for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
    		if ( splt.getAccountID().equals( expAcctID ) ) {
    			return splt;
    		}
    	}
    	
    	throw new TransactionSplitNotFoundException();
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
    @Deprecated
	public FixedPointNumber getGrossDividend() throws TransactionSplitNotFoundException {
		return getGrossDividend_Var1();
	}

    @Deprecated
	private FixedPointNumber getGrossDividend_Var1() throws TransactionSplitNotFoundException {
		return getIncomeAccountSplit().getValue().negate(); // Notice: negate
	}

    @Deprecated
	private FixedPointNumber getGrossDividend_Var2() throws TransactionSplitNotFoundException {
		return getNetDividend().add( getFeesTaxes() );
	}

	@Override
	public BigFraction getGrossDividendRat() throws TransactionSplitNotFoundException {
		return getGrossDividendRat_Var1();
	}
	
	private BigFraction getGrossDividendRat_Var1() throws TransactionSplitNotFoundException {
		return getIncomeAccountSplit().getValueRat().negate(); // Notice: negate
	}

	private BigFraction getGrossDividendRat_Var2() throws TransactionSplitNotFoundException {
		return getNetDividendRat().add( getFeesTaxesRat() );
	}

	// ----------------------------

	@Override
    @Deprecated
	public FixedPointNumber getFeeTax(final GCshAcctID expAcctID) throws TransactionSplitNotFoundException {
		for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
			if ( splt.getAccountID().equals( expAcctID ) ) {
				return splt.getValue();
			}
		}
		
		return FixedPointNumber.ZERO.copy(); // mutable
	}

	@Override
	public BigFraction getFeeTaxRat(final GCshAcctID expAcctID) throws TransactionSplitNotFoundException {
		for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
			if ( splt.getAccountID().equals( expAcctID ) ) {
				return splt.getValueRat();
			}
		}
		
		return BigFraction.ZERO;
	}

	@Override
    @Deprecated
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

	// ----------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Deprecated
	public FixedPointNumber getNetDividend() throws TransactionSplitNotFoundException {
		return getGrossDividend().subtract( getFeesTaxes() ); // mutable
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigFraction getNetDividendRat() throws TransactionSplitNotFoundException	{
		return getGrossDividendRat().subtract( getFeesTaxesRat() ); // immutable
	}

    // ---------------------------------------------------------------
    
	@Override
	public void setStockAcctID(GCshAcctID stockAcctID) throws TransactionSplitNotFoundException	{
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		// ---
		
		GnuCashAccount stockAcct = getGnuCashFile().getAccountByID(stockAcctID);
		if ( stockAcct == null ) {
			LOGGER.error("setStockAcctID: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + stockAcctID);
			throw new IllegalStateException("Could not find account with ID " + stockAcctID);
		}
		
		// ---
		
		setStockAcct(stockAcct);
	}

	@Override
	public void setStockAcct(GnuCashAccount stockAcct) throws TransactionSplitNotFoundException	{
		if ( stockAcct == null ) {
			throw new IllegalArgumentException("argument <stockAcct> is null");
		}
		
		// ---
		
		if ( stockAcct.getType() != GnuCashAccount.Type.STOCK ) {
			LOGGER.error("setStockAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + stockAcct.getID() + " is not of type " + GnuCashAccount.Type.STOCK);
			throw new IllegalArgumentException("Account with ID " + stockAcct.getID() + " is not of type " + GnuCashAccount.Type.STOCK);
		}
		
		boolean acctChange = false;
		GCshAcctID oldAcctID = getStockAccountSplit().getAccountID();
		if ( ! oldAcctID.equals( stockAcct.getID() ) ) {
			acctChange = true;
		}
		if ( acctChange ) {
			LOGGER.debug("setStockAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Changing offsetting account ID from " + oldAcctID + " to " + stockAcct.getID());
		}

		// ---
		
		getWritableStockAccountSplit().setAccountID(stockAcct.getID());
	}

	// ----------------------------

	@Override
	public void setOffsetttingAcctID(GCshAcctID offsettingAcctID) throws TransactionSplitNotFoundException
	{
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		// ---
		
		GnuCashAccount offsettingAcct = getGnuCashFile().getAccountByID(offsettingAcctID);
		if ( offsettingAcct == null ) {
			LOGGER.error("setStockAcctID: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + offsettingAcctID);
			throw new IllegalStateException("Could not find account with ID " + offsettingAcctID);
		}
		
		// ---
		
		setOffsetttingAcct(offsettingAcct);
	}

	@Override
	public void setOffsetttingAcct(GnuCashAccount offsettingAcct) throws TransactionSplitNotFoundException
	{
		if ( offsettingAcct == null ) {
			throw new IllegalArgumentException("argument <offsettingAcct> is null");
		}
		
		// ---
		
		if ( offsettingAcct.getType() != GnuCashAccount.Type.BANK ) {
			LOGGER.error("setOffsetttingAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + offsettingAcct.getID() + " is not of type " + GnuCashAccount.Type.BANK);
			throw new IllegalArgumentException("Account with ID " + offsettingAcct.getID() + " is not of type " + GnuCashAccount.Type.BANK);
		}
		
		boolean acctChange = false;
		GCshAcctID oldAcctID = getStockAccountSplit().getAccountID();
		if ( ! oldAcctID.equals( offsettingAcct.getID() ) ) {
			acctChange = true;
		}
		if ( acctChange ) {
			LOGGER.debug("setOffsetttingAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Changing offsetting account ID from " + oldAcctID + " to " + offsettingAcct.getID());
		}

		// ---
		
		getWritableOffsettingAccountSplit().setAccountID(offsettingAcct.getID());
	}

    // ---------------------------------------------------------------

	@Override
	public void refreshNetDividend() throws TransactionSplitNotFoundException {
		FixedPointNumber netDiv = getGrossDividend_Var2().subtract( getFeesTaxes() ); // <-- important: Var2
		setNetDividend(netDiv);
	}

    // ---------------------------------------------------------------
    
	@Override
    @Deprecated
	public void setGrossDividend(FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		FixedPointNumber amtNeg = amt.copy().negate(); // mutable
		
		getWritableIncomeAccountSplit().setQuantity(amtNeg);
		getWritableIncomeAccountSplit().setValue(amtNeg);
	}

	@Override
	public void setGrossDividend(BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		BigFraction amtNeg = amt.negate(); // mutable
		
		getWritableIncomeAccountSplit().setQuantity(amtNeg);
		getWritableIncomeAccountSplit().setValue(amtNeg);
	}

	// ----------------------------

	@Override
    @Deprecated
	public void addFeeTax(GCshAcctID expAcctID, FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( expAcctID == null ) {
			throw new IllegalArgumentException("argument <expAcctID> is null");
		}
		
		if ( ! expAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <expAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		// ---
		
		GnuCashAccount expAcct = getGnuCashFile().getAccountByID(expAcctID);
		if ( expAcct == null ) {
			LOGGER.error("addFeeTax: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + expAcctID);
			throw new IllegalStateException("Could not find account with ID " + expAcctID);
		}
		
		if ( expAcct.getType() != GnuCashAccount.Type.EXPENSE ) {
			LOGGER.error("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + expAcct.getID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
			throw new IllegalArgumentException("Account with ID " + expAcct.getID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
		}
		
		// ---

		GnuCashWritableTransactionSplit expSplt = null;
		for ( GnuCashWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			if ( splt.getAccountID().equals( expAcctID ) ) {
				expSplt = splt;
				LOGGER.warn("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + splt.getID());
				LOGGER.warn("addFeeTax: Will overwrite data");
				break;
			}
		}
		if ( expSplt == null ) {
			expSplt = createWritableSplit(expAcct);
			LOGGER.debug("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + expSplt.getID());
		}
		
		expSplt.setQuantity(amt);
		expSplt.setValue(amt);
	}

	@Override
	public void addFeeTax(GCshAcctID expAcctID, BigFraction amt) throws TransactionSplitNotFoundException {
		if ( expAcctID == null ) {
			throw new IllegalArgumentException("argument <expAcctID> is null");
		}
		
		if ( ! expAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <expAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		// ---
		
		GnuCashAccount expAcct = getGnuCashFile().getAccountByID(expAcctID);
		if ( expAcct == null ) {
			LOGGER.error("addFeeTax: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + expAcctID);
			throw new IllegalStateException("Could not find account with ID " + expAcctID);
		}
		
		if ( expAcct.getType() != GnuCashAccount.Type.EXPENSE ) {
			LOGGER.error("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + expAcct.getID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
			throw new IllegalArgumentException("Account with ID " + expAcct.getID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
		}
		
		// ---

		GnuCashWritableTransactionSplit expSplt = null;
		for ( GnuCashWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			if ( splt.getAccountID().equals( expAcctID ) ) {
				expSplt = splt;
				LOGGER.warn("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + splt.getID());
				LOGGER.warn("addFeeTax: Will overwrite data");
				break;
			}
		}
		if ( expSplt == null ) {
			expSplt = createWritableSplit(expAcct);
			LOGGER.debug("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + expSplt.getID());
		}
		
		expSplt.setQuantity(amt);
		expSplt.setValue(amt);
	}

	@Override
	public void clearFeesTaxes() throws TransactionSplitNotFoundException {
		for ( GnuCashWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			getGnuCashFile().removeTransactionSplit(splt);
		}
	}

	// ----------------------------

	@Override
    @Deprecated
	public void setNetDividend(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		FixedPointNumber amtNeg = amt.copy(); // mutable
		
		getWritableOffsettingAccountSplit().setQuantity(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	@Override
	public void setNetDividend(final BigFraction amt) throws TransactionSplitNotFoundException {
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
    // ::TODO: Redundant to GnuCashStockBuyTransactionImpl.validate()
	// (as well as the following helper functions)
	public void validate() throws Exception
	{
		if ( getSplitsCount() < NOF_SPLITS_MIN ) {
			String msg = "Trx ID " + getID() + ": Number of splits (altogether) is < " + NOF_SPLITS_MIN;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplitsCount() > NOF_SPLITS_MAX ) {
			String msg = "Trx ID " + getID() + ": Number of splits (altogether) is > " + NOF_SPLITS_MAX;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.STOCK.ordinal()] != NOF_SPLITS_STOCK ) {
			String msg = "Trx ID " + getID() + ": Number of splits to stock account is not " + NOF_SPLITS_STOCK;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.TAXES_FEES.ordinal()] < NOF_SPLITS_FEES_TAXES_MIN || 
			 splitCounter[SplitAccountType.TAXES_FEES.ordinal()] > NOF_SPLITS_FEES_TAXES_MAX ) {
			String msg = "Trx ID " + getID() + ": Number of splits to expenses account is not between " + NOF_SPLITS_FEES_TAXES_MIN + " and " + NOF_SPLITS_FEES_TAXES_MAX;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.OFFSETTING.ordinal()] != NOF_SPLITS_OFFSETTING ) {
			String msg = "Trx ID " + getID() + ": Number of splits to offsetting account is not " + NOF_SPLITS_OFFSETTING;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		validateStockAcctSplit( getStockAccountSplit() );
		
		validateIncomeAcctSplit( getIncomeAccountSplit() );
		
		for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
			validateTaxesFeesAcctSplit( splt );
		}
		
		validateOffsettingAcctSplit( getOffsettingAccountSplit() );

		// ---
		
		if ( getBalance().doubleValue() != 0.0 ) {
			String msg = "Trx ID :" + getID() + ": Transaction is not balanced: " + getBalance();
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ----------------------------
	
	private void validateStockAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getActionStr() != null ) {
			if ( splt.getAction() != GnuCashTransactionSplit.Action.DIVIDEND ) {
				String msg = "the split's action is not valid";
				LOGGER.error("validateStockAcctSplit: " + msg);
				throw new TransactionValidationException("msg");
			}
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.STOCK ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getCmdtyID().getType() == GCshCmdtyID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getQuantity().doubleValue() != 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() != 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	private void validateIncomeAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != null ) {
			String msg = "the split's action is not valid";
			LOGGER.error("validateIncomeAcctSplit: " + msg);
			throw new TransactionValidationException("msg");
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.INCOME ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateIncomeAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getCmdtyID().getType() != GCshCmdtyID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateIncomeAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getQuantity().doubleValue() >= 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateIncomeAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() >= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateIncomeAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	private void validateTaxesFeesAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != null ) { // null is valid!
			String msg = "the split's action is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.EXPENSE ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getCmdtyID().getType() != GCshCmdtyID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getQuantity().doubleValue() <= 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() <= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}

		if ( ! splt.getQuantity().equals( splt.getValue() ) ) {
			String msg = "the split's quantity is not equal to its value";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	private void validateOffsettingAcctSplit(final GnuCashTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != null ) { // null is valid!
			String msg = "the split's action is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getType() != GnuCashAccount.Type.BANK ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getCmdtyID().getType() != GCshCmdtyID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getQuantity().doubleValue() <= 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() <= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( ! splt.getQuantity().equals( splt.getValue() ) ) {
			String msg = "the split's quantity is not equal to its value";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableStockDividendTransactionImpl [");

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

		buffer.append(", income-acct=");
		try {
			buffer.append(getIncomeAccountSplit().getAccount().getID());
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

	public String toStringHuman() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Stock-dividend transaction:\n");

		buffer.append(" - ID: ");
		buffer.append(getID() + "\n");

		buffer.append(" - Splits:\n");
		try
		{
			buffer.append("   o Stock acct split: ");
			buffer.append("ID: " + getStockAccountSplit().getID() + ", ");
			buffer.append("acct: " + getStockAccountSplit().getAccount().getQualifiedName() + ", ");
			GCshSecID secID = (GCshSecID) getStockAccountSplit().getAccount().getCmdtyID();
			GnuCashCommodity sec = getGnuCashFile().getCommodityByID(secID);
			buffer.append("cmdty: '" + sec.getName() + "', ");
			buffer.append("no. of shares: " + getStockAccountSplit().getQuantityFormatted() + "\n");
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			buffer.append("   o Income acct split: ");
			buffer.append("ID: " + getIncomeAccountSplit().getID() + ", ");
			buffer.append("acct: " + getIncomeAccountSplit().getAccount().getQualifiedName() + ", ");
			buffer.append("amt: " + getIncomeAccountSplit().getValueFormatted() + "\n");
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			for ( GnuCashTransactionSplit splt : getExpensesSplits() ) {
				buffer.append("   o Expenses acct split: ");
				buffer.append("ID: " + splt.getID() + ", ");
				buffer.append("acct: " + splt.getAccount().getQualifiedName() + ", ");
				buffer.append("amt: " + splt.getValueFormatted() + "\n");
			}
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try
		{
			buffer.append("   o Offsetting acct split: ");
			buffer.append("ID: " + getOffsettingAccountSplit().getID() + ", ");
			buffer.append("acct: " + getOffsettingAccountSplit().getAccount().getQualifiedName() + ", ");
			buffer.append("amt: " + getOffsettingAccountSplit().getValueFormatted() + "\n");
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		buffer.append(" - Date posted: ");
		try {
			buffer.append(getDatePosted().format(DATE_POSTED_FORMAT) + "\n");
		} catch (Exception e) {
			buffer.append(getDatePosted().toString() + "\n");
		}

		buffer.append(" - Date entered: ");
		try {
			buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT) + "\n");
		} catch (Exception e) {
			buffer.append(getDateEntered().toString() + "\n");
		}

		buffer.append(" - Description: '");
		buffer.append(getDescription() + "'\n");

		return buffer.toString();
	}

}
