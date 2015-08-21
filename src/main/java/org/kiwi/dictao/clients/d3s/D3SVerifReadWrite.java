package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.xsd.v2010_10.common.Metadatas;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.net.ssl.SSLSocketFactory;
import org.kiwi.dictao.clients.d3s.D3SConstants.D3SError;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Classe d'appel au D3S : ecriture d'un mot de passe puis lecture du mot de
 * passe ajoute
 *
 * @author i21653q
 *
 */
public class D3SVerifReadWrite extends D3SWebService {

    @Option(name = "--password-data", required = true, usage = "ID du mot de passe a lire")
    private String passwordData = null;

    @Option(name = "--domain-name", required = true, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = true, usage = "Login du compte; doit etre unique")
    private String appLogin;

    @Option(name = "--safebox-path", required = true, usage = "Chemin vers le coffre ou sauvegarder le secret")
    private final String safeboxPath = null;

    public static void main(String[] args) {
        D3SVerifReadWrite monAppel = new D3SVerifReadWrite();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SVerifReadWrite.class.getName() + " FAILED");
            log.println(e.getMessage());
        }
    }

    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {
        CmdLineParser myParser = new CmdLineParser(this);
        super.parseAndRun(arguments);
        myParser.parseArgument(arguments);
        final SSLSocketFactory buildSSLSocketFactory = buildSSLSocketFactory();

        //secretManager
        AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory, soapMessage);
        StorageProxy stoProxy = new StorageProxy(storageWs, buildSSLSocketFactory, soapMessage);
        D3SSecretManager secretManager = new D3SSecretManager(authProxy, stoProxy);
        int exitCde = writeReadDeletePassword(secretManager, applicantPath,
                appLogin, appDomainName, safeboxPath, passwordData, motivation);

        printResult(exitCde);

        boolean authorityOK = !((exitCde & D3SError.ERROR_GRANT_WRITE.value) == D3SError.ERROR_GRANT_WRITE.value);

        if (exitCde == 0) {
            log.println(this.getClass().getName() + " SUCCESS");
//            System.exit(0);
        } else if (authorityOK) {
            log.println(this.getClass().getName() + " WARNING");
//            System.exit(1);
        } else {
            log.println(this.getClass().getName() + " ERROR");
//            System.exit(2);
        }

        //print list SSL session
        printListSSLSesion();
    }

    private void printResult(int exitCde) {
        log.println(String.format("%nDetailed result :"));
        for (D3SError val : D3SError.values()) {
            if (val != D3SError.NO_ERROR) {
                log.println(
                        String.format("  > %15s : %s",
                                val.name().substring("ERROR_".length()),
                                (((exitCde & val.value) == val.value) ? "KO" : "OK")));
            }
        }
        log.println("");
    }

    public int writeReadDeletePassword(D3SSecretManager secretManager,
            String applicantPath, String appLogin, String appDomainName, String safeboxPath,
            String passwordData, String motivation)
            throws UnsupportedEncodingException {
        //username
        String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;
        String boxPath = D3SConstants.SAFEBOX_PATH_PREFIX + "/" + safeboxPath;
        //depositPath
        String depositPath = "/DEPOSIT?_boxPath=" + boxPath
                + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");
        Metadatas metadata = secretManager.buildPasswordMetadata(appLogin, appDomainName);
        int exitCde = secretManager.write(userName, motivation, D3SConstants.SAFEBOX_PATH_PREFIX
                + "/" + safeboxPath, metadata, passwordData);
        String dataRead = secretManager.read(userName, motivation, depositPath);
        if (dataRead != null) {
            log.println(this.getClass().getName() + " deposit : " + depositPath);
            log.println(this.getClass().getName() + " data read : " + dataRead);
        } else {
            exitCde = exitCde | D3SError.ERROR_READ.value;
        }
        exitCde = exitCde | secretManager.setDeletable(userName, motivation, depositPath);
        exitCde = exitCde | secretManager.discard(userName, motivation, depositPath);
        exitCde = exitCde | secretManager.delete(userName, motivation, depositPath);

        return exitCde;
    }

}
