package org.gnucash.apispec.read.impl;

import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.apispec.read.GnuCashSecurity;
import org.gnucash.base.basetypes.complex.InvalidCmdtyTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashSecurityImpl extends GnuCashCommodityImpl
                                 implements GnuCashSecurity
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashSecurityImpl.class);

	// ---------------------------------------------------------------
	
	public GnuCashSecurityImpl(GnuCashCommodityImpl cmdty) {
		super( cmdty.getJwsdpPeer(), cmdty.getGnuCashFile() );
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
    	String result = "GnuCashSecurityImpl [";

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
