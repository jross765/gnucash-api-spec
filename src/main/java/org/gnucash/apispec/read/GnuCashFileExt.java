package org.gnucash.apispec.read;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.apispec.read.hlp.fil.GnuCashFileExt_Curr;
import org.gnucash.apispec.read.hlp.fil.GnuCashFileExt_Sec;
import org.gnucash.apispec.read.hlp.fil.GnuCashFileExt_TrxSplt;

public interface GnuCashFileExt extends GnuCashFile,
                                        GnuCashFileExt_Curr,
                                        GnuCashFileExt_Sec,
                                        GnuCashFileExt_TrxSplt
{

	// ::EMPTY

}
