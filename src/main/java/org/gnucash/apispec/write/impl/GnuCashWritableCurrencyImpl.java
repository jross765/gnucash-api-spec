package org.gnucash.apispec.write.impl;

import java.util.Currency;

import org.gnucash.api.write.impl.GnuCashWritableCommodityImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apispec.read.impl.GnuCashCurrencyImpl;
import org.gnucash.apispec.write.GnuCashWritableCurrency;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashWritableCurrencyImpl extends GnuCashWritableCommodityImpl
                                        implements GnuCashWritableCurrency
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCurrencyImpl.class);

	// ---------------------------------------------------------------

    protected GnuCashWritableCurrencyImpl(
    		final GnuCashWritableFileImpl file,
    		final GCshCurrID currID) {
    	super(createCommodity_int(file, currID), file);
    }

	public GnuCashWritableCurrencyImpl(GnuCashWritableCommodityImpl cmdty) {
		super(cmdty.getJwsdpPeer(), cmdty.getGnuCashFile());
	}

	public GnuCashWritableCurrencyImpl(GnuCashCurrencyImpl curr) {
		super(curr);
	}

	// ---------------------------------------------------------------

	@Override
	public Currency getCurrency() {
		String isoCode = getQualifID().getCode();
		return Currency.getInstance(isoCode);
	}

}
