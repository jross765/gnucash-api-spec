package org.gnucash.apispec.read.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.apispec.read.GnuCashCurrency;
import org.gnucash.apispec.read.GnuCashFileExt;
import org.gnucash.apispec.read.GnuCashSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class GnuCashFileExtImpl extends GnuCashFileImpl
                                implements GnuCashFileExt
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashFileExtImpl.class);

	// ---------------------------------------------------------------
	
	public GnuCashFileExtImpl(final File pFile) throws IOException {
		super(pFile);
	}
	
	public GnuCashFileExtImpl(final File pFile, final boolean withProgBar) throws IOException {
		super(pFile, withProgBar);
	}

	public GnuCashFileExtImpl(final InputStream is) throws IOException {
		super(is);
	}

	public GnuCashFileExtImpl(final InputStream is, final boolean withProgBar) throws IOException {
		super(is, withProgBar);
	}
	
	public GnuCashFileExtImpl(final GnuCashFile gcshFile) throws IOException {
		super(gcshFile.getFile());
	}
	
	// ---------------------------------------------------------------

	@Override
	public GnuCashCurrency getCurrencyByID(final GCshCurrID currID) {
		return new GnuCashCurrencyImpl( (GnuCashCommodityImpl) getCommodityByID(currID) );
	}

	@Override
	public GnuCashCurrency getCurrencyByISOCode(final String isoCode) {
		GCshCurrID currID = new GCshCurrID(isoCode);
		return getCurrencyByID(currID);
	}

	@Override
	public List<GnuCashCurrency> getCurrencies() {
		ArrayList<GnuCashCurrency> result = new ArrayList<GnuCashCurrency>();
		
		for ( GnuCashCommodity cmdty : getCommodities() ) {
			if ( cmdty.getQualifID().getType() == GCshCmdtyID.Type.CURRENCY ) {
				result.add( new GnuCashCurrencyImpl((GnuCashCommodityImpl) cmdty) );
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashSecurity getSecurityByID(final GCshSecID secID) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByID(secID) );
	}

	@Override
	public GnuCashSecurity getSecurityByNamSpcCode(final String nameSpace, final String code) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByNamSpcCode(nameSpace, code) );
	}
	
	@Override
	public GnuCashSecurity getSecurityByNamSpcCode(final GCshCmdtyNameSpace.Exchange exchange, final String code) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByNamSpcCode(exchange, code) );
	}

	@Override
	public GnuCashSecurity getSecurityByNamSpcCode(final GCshCmdtyNameSpace.MIC mic, final String code) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByNamSpcCode(mic, code) );
	}

	@Override
	public GnuCashSecurity getSecurityByNamSpcCode(final GCshCmdtyNameSpace.SecIdType secIdType, final String code) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByNamSpcCode(secIdType, code) );
	}

	@Override
	public GnuCashSecurity getSecurityByXCode(final String xCode) {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByXCode(xCode) );
	}

	@Override
	public List<GnuCashSecurity> getSecurities() {
		ArrayList<GnuCashSecurity> result = new ArrayList<GnuCashSecurity>();
		
		for ( GnuCashCommodity cmdty : getCommodities() ) {
			if ( cmdty.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashSecurityImpl((GnuCashCommodityImpl) cmdty) );
			}
		}
		
		return result;
	}

	@Override
	public List<GnuCashSecurity> getSecuritiesByName(final String expr) {
		ArrayList<GnuCashSecurity> result = new ArrayList<GnuCashSecurity>();
		
		for ( GnuCashCommodity cmdty : getCommoditiesByName(expr) ) {
			if ( cmdty.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashSecurityImpl((GnuCashCommodityImpl) cmdty) );
			}
		}
		
		return result;
	}

	@Override
	public List<GnuCashSecurity> getSecuritiesByName(final String expr, final boolean relaxed) {
		ArrayList<GnuCashSecurity> result = new ArrayList<GnuCashSecurity>();
		
		for ( GnuCashCommodity cmdty : getCommoditiesByName(expr, relaxed) ) {
			if ( cmdty.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashSecurityImpl((GnuCashCommodityImpl) cmdty) );
			}
		}
		
		return result;
	}

	@Override
	public GnuCashSecurity getSecurityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
		return new GnuCashSecurityImpl( (GnuCashCommodityImpl) getCommodityByNameUniq(expr) );
	}

}
