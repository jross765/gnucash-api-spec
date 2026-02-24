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
	public FixedPointNumber getGrossDividend() throws TransactionSplitNotFoundException {
		return getIncomeAccountSplit().getValue().negate(); // Notice: negate
	}

	@Override
	public BigFraction getGrossDividendRat() throws TransactionSplitNotFoundException {
		return getIncomeAccountSplit().getValueRat().negate(); // Notice: negate
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedPointNumber getNetDividend() throws TransactionSplitNotFoundException
	{
		FixedPointNumber result = getGrossDividend();
		
		result.subtract( getFeesTaxes() ); // mutable
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigFraction getNetDividendRat() throws TransactionSplitNotFoundException
	{
		BigFraction result = getGrossDividendRat();
		
		result = result.subtract( getFeesTaxesRat() ); // immutable
		
		return result;
	}

    // ---------------------------------------------------------------
    
	@Override
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

	@Override
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
			String msg = "the split's shares is not valid";
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
