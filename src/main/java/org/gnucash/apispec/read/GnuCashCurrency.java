package org.gnucash.apispec.read;

import java.util.Currency;

import org.gnucash.api.read.GnuCashCommodity;

public interface GnuCashCurrency extends GnuCashCommodity {

	Currency getCurrency();
	
}
