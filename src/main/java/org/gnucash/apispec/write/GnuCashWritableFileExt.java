package org.gnucash.apispec.write;

import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.apispec.read.GnuCashFileExt;
import org.gnucash.apispec.write.hlp.fil.GnuCashWritableFileExt_Curr;
import org.gnucash.apispec.write.hlp.fil.GnuCashWritableFileExt_Sec;
import org.gnucash.apispec.write.hlp.fil.GnuCashWritableFileExt_TrxSplt;

public interface GnuCashWritableFileExt extends GnuCashWritableFile,
                                                GnuCashFileExt,
                                                GnuCashWritableFileExt_Curr,
                                                GnuCashWritableFileExt_Sec,
                                                GnuCashWritableFileExt_TrxSplt
{

	// ::EMPTY

}
