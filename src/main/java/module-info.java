module gnucash.apispec {

	requires static org.slf4j;
	requires java.desktop;
	
	// ----------------------------

	requires transitive schnorxoborx.schnorxolib;

	requires transitive gnucash.base;
	requires transitive gnucash.api;

	// ----------------------------

	exports org.gnucash.apispec.read;
	exports org.gnucash.apispec.read.impl;
	
	exports org.gnucash.apispec.write;
	exports org.gnucash.apispec.write.impl;

}
