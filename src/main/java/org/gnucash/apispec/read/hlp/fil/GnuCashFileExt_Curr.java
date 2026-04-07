package org.gnucash.apispec.read.hlp.fil;

import java.util.List;

import org.gnucash.apispec.read.GnuCashCurrency;
import org.gnucash.base.basetypes.complex.GCshCurrID;

public interface GnuCashFileExt_Curr {

	GnuCashCurrency getCurrencyByID(GCshCurrID currID);
	
	GnuCashCurrency getCurrencyByISOCode(String expr);

	List<GnuCashCurrency> getCurrencies();
	
}
