package org.gnucash.apispec.read.impl;

import java.util.Currency;

import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.apispec.read.GnuCashCurrency;
import org.gnucash.base.basetypes.complex.InvalidCmdtyTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashCurrencyImpl extends GnuCashCommodityImpl
                                 implements GnuCashCurrency
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCurrencyImpl.class);

	// ---------------------------------------------------------------
	
	public GnuCashCurrencyImpl(GnuCashCommodityImpl cmdty) {
		super( cmdty.getJwsdpPeer(), cmdty.getGnuCashFile() );
	}

	// ---------------------------------------------------------------
	
	@Override
	public Currency getCurrency() {
		String isoCode = getQualifID().getCode();
		return Currency.getInstance(isoCode);
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
    	String result = "GnuCashCurrencyImpl [";

    	try {
    		result += "qualif-id='" + getQualifID().toString() + "'";
    	} catch (InvalidCmdtyTypeException e) {
    		result += "qualif-id=" + "ERROR";
    	}
	
    	result += ", namespace='" + getNameSpace() + "'"; 
    	result += ", name='" + getName() + "'"; 
    	result += ", x-code='" + getXCode() + "'"; 
    	result += ", fraction=" + getFraction() + "]";
	
    	return result;
    }

}
