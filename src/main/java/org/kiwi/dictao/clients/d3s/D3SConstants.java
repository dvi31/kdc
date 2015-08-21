package org.kiwi.dictao.clients.d3s;

public class D3SConstants {

    public static final String USER_PATH_PREFIX = "/USER";
    public static final String DEPOSIT_PATH_PREFIX = "/DEPOSIT";
    public static final String ORGUNIT_PATH_PREFIX = "/ORGUNIT";
    public final static String SAFEBOX_PATH_PREFIX = "/SAFEBOX";
    public final static String FILINGPLAN_PATH_PREFIX = "/FILINGPLAN";
    public final static String FILINGPLAN_PATH_PWD = "/FILINGPLAN/PASSWORD";
    public final static String FILINGPLAN_PATH_DOC = "/FILINGPLAN/DOCUMENT";

    public enum D3SError {

        NO_ERROR(0x0000),
        ERROR_GRANT_WRITE(0x0001),
        ERROR_WRITE(0x0002),
        //ERROR_GRANT_READ(0x0010),
        ERROR_READ(0x0020),
        ERROR_SETPERMISSION(0x0100),
        ERROR_DISCARD(0x0200),
        ERROR_GRANT_DELETE(0x0400),
        ERROR_DELETE(0x0800);

        public final int value;

        D3SError(int value) {
            this.value = value;
        }
    }
}
