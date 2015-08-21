package org.kiwi.dictao.clients.d3s;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SReadMdp extends D3SWebService {

    @Option(name = "--domain-name", required = true, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = true, usage = "Login du compte; doit etre unique")
    private String appLogin;

    @Option(name = "--safebox-path", required = false, usage = "Chemin vers le coffre ou sauvegarder le secret")
    private final String safeboxPath = null;

    public static void main(String[] args) {
        D3SReadMdp monAppel = new D3SReadMdp();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SReadMdp.class.getName() + " FAILED");
            log.println(e.getMessage());
        }
    }

    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException,
            IOException {
        super.parseAndRun(arguments);
        CmdLineParser myParser = new CmdLineParser(this);
        myParser.parseArgument(arguments);

        AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory(), soapMessage);
        StorageProxy stoProxy = new StorageProxy(storageWs, buildSSLSocketFactory(), soapMessage);
        int errCode = readMDP(authProxy, stoProxy, applicantPath, safeboxPath,
                appLogin, appDomainName, motivation);

        if (errCode == 0) {
            log.println(this.getClass().getName() + " SUCCESS");
//            System.exit(0);
        } else if (errCode == 1) {
            log.println(this.getClass().getName() + " WARNING");
//            System.exit(1);
        } else {
            log.println(this.getClass().getName() + " ERROR");
//            System.exit(2);
        }
    }

    public int readMDP(AuthorityProxy authProxy, StorageProxy stoProxy,
            String applicantPath, String safeboxPath, String appLogin, String appDomainName,
            String motivation)
            throws UnsupportedEncodingException {
        D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);

        //username
        String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;

        String parentPath;
        if (safeboxPath != null) {
            parentPath = "_boxPath=" + URLEncoder.encode(D3SConstants.SAFEBOX_PATH_PREFIX + "/" + safeboxPath, "UTF-8");
        } else {
            parentPath = "_fpPath=" + URLEncoder.encode(D3SConstants.FILINGPLAN_PATH_PWD, "UTF-8");
        }
        //depositPath
        String depositPath = "/DEPOSIT?" + parentPath
                + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");

        String dataRead = secretManager.read(userName, motivation, depositPath);

        if (dataRead != null) {
            System.out.println("deposit : " + depositPath);
            System.out.println("data read : " + dataRead);
            return 0;
        } else {
            return 1;
        }
    }
}
