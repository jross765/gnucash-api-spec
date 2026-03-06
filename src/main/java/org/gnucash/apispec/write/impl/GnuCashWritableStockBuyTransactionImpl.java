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
import org.gnucash.apispec.read.impl.GnuCashStockBuyTransactionImpl.SplitAccountType;
import org.gnucash.apispec.read.impl.TransactionValidationException;
import org.gnucash.apispec.write.GnuCashWritableStockBuyTransaction;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
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
public class GnuCashWritableStockBuyTransactionImpl extends GnuCashWritableTransactionImpl 
                                                    implements GnuCashWritableStockBuyTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableStockBuyTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS_STOCK = 1;
    
    private static final int NOF_SPLITS_FEES_TAXES_MIN = 0;
    private static final int NOF_SPLITS_FEES_TAXES_MAX = 4; // more is implausible

    private static final int NOF_SPLITS_OFFSETTING = 1;
    
    // ---

    private static final int NOF_SPLITS_MIN = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MIN + NOF_SPLITS_OFFSETTING;
    private static final int NOF_SPLITS_MAX = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MAX + NOF_SPLITS_OFFSETTING;

	// ---------------------------------------------------------------
    
    private int[] splitCounter;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockBuyTransactionImpl(final GnuCashStockBuyTransactionImpl trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public GnuCashWritableStockBuyTransactionImpl(final GnuCashWritableStockBuyTransaction trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
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
	public FixedPointNumber getNofShares() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getQuantity();
	}

	@Override
	public BigFraction getNofSharesRat() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getQuantityRat();
	}

    // ----------------------------
    
	@Override
	public FixedPointNumber getPricePerShare() throws TransactionSplitNotFoundException {
		return getPricePerShare_Var2();
	}

	private FixedPointNumber getPricePerShare_Var1() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNetPrice_Var1();
		
		result.divide( getNofShares() ); // mutable
		
		return result;
	}

	private FixedPointNumber getPricePerShare_Var2() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNetPrice_Var3();
		
		result.divide( getNofShares() ); // mutable
		
		return result;
	}

	@Override
	public BigFraction getPricePerShareRat() throws TransactionSplitNotFoundException {
		return getPricePerShareRat_Var2();
	}

	private BigFraction getPricePerShareRat_Var1() throws TransactionSplitNotFoundException {
		BigFraction result = getNetPriceRat_Var1();
		
		result = result.divide( getNofSharesRat() ); // immutable
		
		return result;
	}

	private BigFraction getPricePerShareRat_Var2() throws TransactionSplitNotFoundException {
		BigFraction result = getNetPriceRat_Var3();
		
		result = result.divide( getNofSharesRat() ); // mutable
		
		return result;
	}

    // ----------------------------
    
	@Override
	public FixedPointNumber getNetPrice() throws TransactionSplitNotFoundException {
		return getNetPrice_Var1();
	}

	private FixedPointNumber getNetPrice_Var1() throws TransactionSplitNotFoundException {
		return getGrossPrice().subtract( getFeesTaxes() );
	}

	private FixedPointNumber getNetPrice_Var2() throws TransactionSplitNotFoundException {
		return getNofShares().multiply( getPricePerShare() );
	}

	private FixedPointNumber getNetPrice_Var3() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getValue();
	}

	@Override
	public BigFraction getNetPriceRat() throws TransactionSplitNotFoundException {
		return getNetPriceRat_Var1();
	}

	private BigFraction getNetPriceRat_Var1() throws TransactionSplitNotFoundException {
		return getGrossPriceRat().subtract( getFeesTaxesRat() );
	}

	private BigFraction getNetPriceRat_Var2() throws TransactionSplitNotFoundException {
		return getNofSharesRat().multiply( getPricePerShareRat() );
	}

	private BigFraction getNetPriceRat_Var3() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getValueRat();
	}

    // ----------------------------
    
	@Override
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
	public void setStockAcctID(GCshAcctID stockAcctID) throws TransactionSplitNotFoundException {
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
	public void setStockAcct(GnuCashAccount stockAcct) throws TransactionSplitNotFoundException {
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
	public void setNofShares(final FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setQuantity(val);
	}

	@Override
	public void setNofShares(final BigFraction val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setQuantity(val);
	}

	// ----------------------------

	@Override
	public void setNofShares(GCshAcctID stockAcctID, FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setNofShares(val);
	}

	@Override
	public void setNofShares(GCshAcctID stockAcctID, BigFraction val) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setNofShares(val);
	}
	
	// ----------------------------

	@Override
	public void setPricePerShare(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		// getWritableStockAccountSplit().setPrice(amt);
		
		FixedPointNumber netPrc = getNofShares().multiply(amt);
		getWritableStockAccountSplit().setValue(netPrc);
	}

	@Override
	public void setPricePerShare(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		// getWritableStockAccountSplit().setPrice(amt);
		
		BigFraction netPrc = getNofSharesRat().multiply(amt);
		getWritableStockAccountSplit().setValue(netPrc);
	}
	
	// ----------------------------

	@Override
	public void setPricePerShare(GCshAcctID stockAcctID, FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setPricePerShare(amt);
	}

	@Override
	public void setPricePerShare(GCshAcctID stockAcctID, BigFraction amt) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setPricePerShare(amt);
	}

	// ----------------------------

	@Override
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
	public void setGrossPrice(FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		FixedPointNumber amtNeg = amt.copy().negate(); // mutable
		
		getWritableOffsettingAccountSplit().setQuantity(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	@Override
	public void setGrossPrice(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		BigFraction amtNeg = amt.negate(); // immutable
		
		getWritableOffsettingAccountSplit().setQuantity(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	// ----------------------------

	@Override
	public void setGrossPrice(final GCshAcctID offsettingAcctID, final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setOffsetttingAcctID(offsettingAcctID);
		setGrossPrice(amt);
	}

	@Override
	public void setGrossPrice(final GCshAcctID offsettingAcctID, final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setOffsetttingAcctID(offsettingAcctID);
		setGrossPrice(amt);
	}

	// ----------------------------
	
	@Override
	public void refreshGrossPrice() throws TransactionSplitNotFoundException {
		FixedPointNumber grossPrc = getNetPrice_Var2().add( getFeesTaxes() ); // <-- important: Var2
		setGrossPrice(grossPrc);
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
		if ( splt.getAction() != GnuCashTransactionSplit.Action.BUY ) {
			String msg = "the split's action is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException("msg");
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
		
		if ( splt.getQuantity().doubleValue() <= 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() <= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
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
		
		if ( splt.getQuantity().doubleValue() >= 0.0 ) {
			String msg = "the split's quantity is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() >= 0.0 ) {
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
