package org.gnucash.apispec.write.impl;

import org.gnucash.api.write.impl.GnuCashWritableCommodityImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apispec.read.impl.GnuCashSecurityImpl;
import org.gnucash.apispec.write.GnuCashWritableSecurity;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashWritableSecurityImpl extends GnuCashWritableCommodityImpl
                                         implements GnuCashWritableSecurity
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableSecurityImpl.class);

	// ---------------------------------------------------------------

    protected GnuCashWritableSecurityImpl(
    		final GnuCashWritableFileImpl file,
    		final GCshSecID secID) {
    	super(createCommodity_int(file, secID), file);
    }

	public GnuCashWritableSecurityImpl(GnuCashWritableCommodityImpl cmdty) {
		super(cmdty.getJwsdpPeer(), cmdty.getGnuCashFile());
	}

	public GnuCashWritableSecurityImpl(GnuCashSecurityImpl sec) {
		super(sec);
	}

	// ---------------------------------------------------------------

	// ::TODO

}
