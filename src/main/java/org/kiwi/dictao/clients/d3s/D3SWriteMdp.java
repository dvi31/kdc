package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.xsd.v2010_10.common.Metadatas;
import java.io.IOException;
import org.kiwi.utils.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SWriteMdp extends D3SWebService {

    @Option(name = "--password-data", required = true, usage = "ID du mot de passe a lire")
    private String passwordData = null;

    @Option(name = "--domain-name", required = true, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = true, usage = "Login du compte; doit etre unique")
    private String appLogin;

    @Option(name = "--safebox-path", required = true, usage = "Chemin vers le coffre ou sauvegarder le secret")
    private final String safeboxPath = null;

    public static void main(String[] args) {
        D3SWriteMdp monAppel = new D3SWriteMdp();

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

        int errCode = writeMDP(authProxy, stoProxy, applicantPath, safeboxPath,
                appLogin, appDomainName, passwordData, motivation);

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

    public int writeMDP(AuthorityProxy authProxy, StorageProxy stoProxy,
            String applicantPath, String safeboxPath, String appLogin,
            String appDomainName, String passwordData, String motivation) {

        //secretManager
        D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);
        //username
        String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;

        Metadatas metadata = secretManager.buildPasswordMetadata(appLogin, appDomainName);
        int exitCde = secretManager.write(userName, motivation, D3SConstants.SAFEBOX_PATH_PREFIX
                + "/" + safeboxPath, metadata, passwordData);

        if (exitCde == 0) {
            System.out.println(appLogin + "/" + appDomainName + " writed");
        } else {
            System.out.println(appLogin + "/" + appDomainName + " NOT writed");
        }
        return exitCde;
    }
}
