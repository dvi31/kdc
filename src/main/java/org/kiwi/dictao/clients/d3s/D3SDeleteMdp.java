package org.kiwi.dictao.clients.d3s;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SDeleteMdp extends D3SWebService {

    @Option(name = "--domain-name", required = true, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = true, usage = "Login du compte; doit etre unique")
    private String appLogin;

    public static void main(String[] args) {
        D3SDeleteMdp monAppel = new D3SDeleteMdp();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SVerifReadWrite.class.getName() + " FAILED");
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
        D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);

        int exitCde = deleteMDP(authProxy, stoProxy, applicantPath,
                appLogin, appDomainName, motivation);

        if (exitCde == 0) {
            log.println(this.getClass().getName() + " SUCCESS");
//            System.exit(0);
        } else if (exitCde == 1) {
            log.println(this.getClass().getName() + " WARNING");
//            System.exit(1);
        } else {
            log.println(this.getClass().getName() + " ERROR");
//            System.exit(2);
        }
    }

    public int deleteMDP(AuthorityProxy authProxy, StorageProxy stoProxy,
            String applicantPath, String appLogin, String appDomainName,
            String motivation) throws UnsupportedEncodingException {

        //secretManager
        D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);
        //username
        String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;
        String depositPath = "/DEPOSIT?_fpPath=" + URLEncoder.encode(D3SConstants.FILINGPLAN_PATH_PWD, "UTF-8")
                + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");

        int exitCde = secretManager.setDeletable(userName, motivation, depositPath);
        exitCde = exitCde | secretManager.discard(userName, motivation, depositPath);
        exitCde = exitCde | secretManager.delete(userName, motivation, depositPath);

        if (exitCde == 0) {
            System.out.println(appLogin + "/" + appDomainName + " deleted");
        } else {
            System.out.println(appLogin + "/" + appDomainName + " NOT deleted");
        }
        return exitCde;
    }

}
