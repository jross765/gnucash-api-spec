package org.gnucash.apispec;

// On purpose redundant to according class in org.kmymoney.api
public class ConstTest {

    public static final String GCSH_FILENAME     = "test.gnucash";

    public static final String GCSH_FILENAME_IN  = GCSH_FILENAME;

    public static final String GCSH_FILENAME_OUT = "test_out.gnucash";
    
    // ---------------------------------------------------------------
    // Stats for above-mentioned GnuCash test file (before write operations)
    
    public class Stats {
    
    	public static final int NOF_SEC  = 6;
    	public static final int NOF_CURR = 2;
    
    }

    // -----------------------------------------------------------------

    public static final double DIFF_TOLERANCE = 0.005;

}
