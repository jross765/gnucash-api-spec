package org.gnucash.apispec.write.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.ObjectCascadeException;
import org.gnucash.api.write.impl.GnuCashWritableCommodityImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionSplitImpl;
import org.gnucash.apispec.read.GnuCashCurrency;
import org.gnucash.apispec.read.GnuCashSecurity;
import org.gnucash.apispec.read.impl.GnuCashCurrencyImpl;
import org.gnucash.apispec.read.impl.GnuCashSecurityImpl;
import org.gnucash.apispec.write.GnuCashWritableCurrency;
import org.gnucash.apispec.write.GnuCashWritableFileExt;
import org.gnucash.apispec.write.GnuCashWritableSecurity;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace.Exchange;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace.MIC;
import org.gnucash.base.basetypes.complex.GCshCmdtyNameSpace.SecIdType;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.GCshSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class GnuCashWritableFileExtImpl extends GnuCashWritableFileImpl
                                        implements GnuCashWritableFileExt
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableFileExtImpl.class);

	// ---------------------------------------------------------------

	public GnuCashWritableFileExtImpl(final File file) throws IOException {
		super(file);
	}

	public GnuCashWritableFileExtImpl(final File file, final boolean withProgBar) throws IOException {
		super(file, withProgBar);
	}

	public GnuCashWritableFileExtImpl(final InputStream is) throws IOException {
		super(is);
	}

	public GnuCashWritableFileExtImpl(final InputStream is, final boolean withProgBar) throws IOException {
		super(is, withProgBar);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashWritableCurrency getWritableCurrencyByID(GCshCurrID currID) {
		GnuCashCurrency curr = getCurrencyByID(currID);
		return new GnuCashWritableCurrencyImpl((GnuCashCurrencyImpl) curr);
	}

	@Override
	public GnuCashWritableCurrency getWritableCurrencyByISOCode(String isoCode) {
		GnuCashCurrency curr = getCurrencyByISOCode(isoCode);
		return new GnuCashWritableCurrencyImpl((GnuCashCurrencyImpl) curr);
	}

	@Override
	public Collection<GnuCashWritableCurrency> getWritableCurrencies() {
		ArrayList<GnuCashWritableCurrency> result = new ArrayList<GnuCashWritableCurrency>();
		
		for ( GnuCashCurrency curr : getCurrencies() ) {
			if ( curr.getQualifID().getType() == GCshCmdtyID.Type.CURRENCY ) {
				result.add( new GnuCashWritableCurrencyImpl((GnuCashCurrencyImpl) curr) );
			}
		}
		
		return result;
	}

	@Override
	public GnuCashWritableCurrency createWritableCurrency(
			final GCshCurrID currID, 
			final String code, 
			final String name) {
		GnuCashWritableCommodity cmdty = super.createWritableCommodity(currID, code, name);
		return new GnuCashWritableCurrencyImpl((GnuCashWritableCommodityImpl) cmdty );
	}

	@Override
	public void removeCurrency(final GnuCashWritableCurrency curr) throws ObjectCascadeException {
		super.removeCommodity(curr);
	}
	
	// ---------------------------------------------------------------

	@Override
	public GnuCashWritableSecurity getWritableSecurityByID(GCshSecID secID) {
		GnuCashSecurity sec = getSecurityByID(secID);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByQualifID(String nameSpace, String code) {
		GnuCashSecurity sec = getSecurityByNamSpcCode(nameSpace, code);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByQualifID(Exchange exchange, String code) {
		GnuCashSecurity sec = getSecurityByNamSpcCode(exchange, code);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByQualifID(MIC mic, String code) {
		GnuCashSecurity sec = getSecurityByNamSpcCode(mic, code);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByQualifID(SecIdType secIdType, String code) {
		GnuCashSecurity sec = getSecurityByNamSpcCode(secIdType, code);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByXCode(String xCode) {
		GnuCashSecurity sec = getSecurityByXCode(xCode);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec);
	}

	@Override
	public List<GnuCashWritableSecurity> getWritableSecuritiesByName(String expr) {
		ArrayList<GnuCashWritableSecurity> result = new ArrayList<GnuCashWritableSecurity>();
		
		for ( GnuCashSecurity sec : getSecuritiesByName(expr) ) {
			if ( sec.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec));
			}
		}
		
		return result;
	}

	@Override
	public List<GnuCashWritableSecurity> getWritableSecuritiesByName(String expr, boolean relaxed) {
		ArrayList<GnuCashWritableSecurity> result = new ArrayList<GnuCashWritableSecurity>();
		
		for ( GnuCashSecurity sec : getSecuritiesByName(expr, relaxed) ) {
			if ( sec.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec));
			}
		}
		
		return result;
	}

	@Override
	public GnuCashWritableSecurity getWritableSecurityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
		GnuCashSecurity cmdty = getSecurityByNameUniq(expr);
		return new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) cmdty);
	}

	@Override
	public Collection<GnuCashWritableSecurity> getWritableSecurities() {
		ArrayList<GnuCashWritableSecurity> result = new ArrayList<GnuCashWritableSecurity>();
		
		for ( GnuCashSecurity sec : getSecurities() ) {
			if ( sec.getQualifID().getType() == GCshCmdtyID.Type.SECURITY ) {
				result.add( new GnuCashWritableSecurityImpl((GnuCashSecurityImpl) sec) );
			}
		}
		
		return result;
	}

	@Override
	public GnuCashWritableSecurity createWritableSecurity(
			final GCshSecID secID, 
			final String code, 
			final String name) {
		GnuCashWritableCommodity cmdty = super.createWritableCommodity(secID, code, name);
		return new GnuCashWritableSecurityImpl((GnuCashWritableCommodityImpl) cmdty );
	}

	@Override
	public void removeSecurity(final GnuCashWritableSecurity sec) throws ObjectCascadeException {
		super.removeCommodity(sec);
	}

	// ---------------------------------------------------------------
	
	@Override
	public List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsBySecID(final GCshSecID secID) {
		ArrayList<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getTransactionSplitsBySecID(secID) ) {
			result.add( new GnuCashWritableTransactionSplitImpl(splt) );
		}
		
		return result;
	}

	@Override
	public List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsByCurrID(final GCshCurrID currID) {
		ArrayList<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getTransactionSplitsByCurrID(currID) ) {
			result.add( new GnuCashWritableTransactionSplitImpl(splt) );
		}
		
		return result;
	}

	@Override
	public List<GnuCashWritableTransactionSplit> getWritableTransactionSplitsByCurr(final Currency curr) {
		ArrayList<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getTransactionSplitsByCurr(curr) ) {
			result.add( new GnuCashWritableTransactionSplitImpl(splt) );
		}
		
		return result;
	}

	// ---------------------------------------------------------------
	// The following are redundant re-implementations

	@Override
	public GnuCashCurrency getCurrencyByID(final GCshCurrID currID) {
		return new GnuCashCurrencyImpl( (GnuCashCommodityImpl) getCommodityByID(currID) );
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

	@Override
	public GnuCashCurrency getCurrencyByISOCode(final String isoCode) {
		GCshCurrID currID = new GCshCurrID(isoCode);
		return getCurrencyByID(currID);
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

	// ---------------------------------------------------------------

	@Override
	public List<GnuCashTransactionSplit> getTransactionSplitsBySecID(final GCshSecID secID) {
		return getTransactionSplitsByCmdtyID(secID);
	}

	@Override
	public List<GnuCashTransactionSplit> getTransactionSplitsByCurrID(final GCshCurrID currID) {
		return getTransactionSplitsByCmdtyID(currID);
	}

	@Override
	public List<GnuCashTransactionSplit> getTransactionSplitsByCurr(final Currency curr) {
		return getTransactionSplitsByCmdtyID(new GCshCmdtyID(curr));
	}

}
