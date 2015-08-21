package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.EnvironmentFaultException;
import com.dictao.d3s.wsdl.v2.authority.UserFaultException;
import com.dictao.d3s.wsdl.v2_1.authority.GrantDeleteResponse;
import com.dictao.d3s.wsdl.v2_1.authority.GrantReadResponse;
import com.dictao.d3s.wsdl.v2_1.authority.GrantWriteResponse;
import com.dictao.d3s.xsd.v2010_10.common.SAMLToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SGrant extends D3SWebService {

    public enum GrantType {

        READ,
        WRITE,
        DELETE
    };

    @Option(name = "--grant-type", required = true, usage = "Type de grant (READ, WRITE, DELETE)")
    private GrantType grantType;

    @Option(name = "--safebox-path", required = true, usage = "Path du coffre")
    private String safeboxPath;

    @Option(name = "--domain-name", required = false, usage = "Nom du domaine")
    private String appDomainName;

    @Option(name = "--login-name", required = false, usage = "Login du compte; doit etre unique")
    private String appLogin;

    @Option(name = "--samlFile", required = false, usage = "(optionel) Fichier SAML en sortie")
    protected File samlFile = null;

    public static void main(String[] args) {
        D3SGrant monAppel = new D3SGrant();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SGrant.class.getName() + " FAILED");
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
        int exitCde = grant(authProxy, grantType, applicantPath, safeboxPath,
                appLogin, appDomainName, motivation);
        if (exitCde == 0) {
            log.println(this.getClass().getName() + " SUCCESS");
        }
//        System.exit(exitCde);
        //print list SSL session
        printListSSLSesion();
    }

    public int grant(AuthorityProxy authProxy, GrantType grantType,
            String applicantPath, String safeboxPath, String appLogin, String appDomainName,
            String motivation) {
        //username
        String _applicantPath = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;
        String _boxPath = D3SConstants.SAFEBOX_PATH_PREFIX + "/" + safeboxPath;

        try {
            SAMLToken saml = null;
            switch (grantType) {
                case READ: {
                    //depositPath
                    String depositPath = "/DEPOSIT?_boxPath=" + URLEncoder.encode(_boxPath, "UTF-8")
                            + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                            + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");
                    GrantReadResponse response = authProxy.getSecretPort().grantRead(
                            _applicantPath, motivation, depositPath);
                    saml = response.getSecurityToken();
                    break;
                }
                case DELETE: {
                    //depositPath
                    String depositPath = "/DEPOSIT?_boxPath=" + URLEncoder.encode(_boxPath, "UTF-8")
                            + "&appLogin=" + URLEncoder.encode(appLogin, "UTF-8")
                            + "&appDomainName=" + URLEncoder.encode(appDomainName, "UTF-8");
                    GrantDeleteResponse response = authProxy.getSecretPort().grantDelete(
                            _applicantPath, motivation, depositPath);
                    saml = response.getSecurityToken();
                    break;
                }
                case WRITE: {
                    //boxPath
                    GrantWriteResponse response = authProxy.getSecretPort().grantWrite(
                            _applicantPath, motivation, _boxPath);
                    saml = response.getSecurityToken();
                    break;
                }
            }

            if (saml != null) {
                if (samlFile != null) {
                    System.out.println("Sortie dans : " + samlFile.getPath());
                    FileOutputStream monStream = new FileOutputStream(samlFile);
                    monStream.write(saml.getValue());
                } else {
                    System.out.println("Taille de la réponse signée : " + saml.getValue().length);
                }
                return 0;
            } else {
                return 2;
            }
        } catch (Exception e) {
            log.println(D3SGrant.class.getName() + " FAILED");
            log.println(e.getMessage());
            return 3;
        }
    }
}
