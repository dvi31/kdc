package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.EnvironmentFaultException;
import com.dictao.d3s.wsdl.v2.authority.SafeboxPort;
import com.dictao.d3s.wsdl.v2.authority.UserFaultException;
import com.dictao.d3s.wsdl.v2_1.authority.FindSafeboxesResponse;
import com.dictao.d3s.xsd.v2010_10.common.CipherMode;
import com.dictao.d3s.xsd.v2010_10.common.MimeType;
import com.dictao.d3s.xsd.v2010_10.common.Safebox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Affiche la liste des coffres disponibles pour l'utilisateur
 *
 * @author i21653q
 *
 */
public class D3SCreateSafebox extends D3SWebService {

    @Option(name = "--boxPath", required = true, usage = "Chemin de l'armoire parente")
    protected String boxPath = null;

    @Option(name = "--orgUnitPath", required = true, usage = "Chemin du groupe")
    protected String orgUnitPath = null;

    @Option(name = "--boxName", required = true, usage = "Nom du coffre à créer")
    protected String boxName = null;

    @Option(name = "--fillingPlan", required = true, usage = "Valeur possible : \n"
            + "'document' pour les coffres de document;\n"
            + "'password' pour les coffres de mot de passe;\n")
    protected String fillingPlan = null;
    /**
     * Possible values for filingPlan , only lower case string
     *
     */
    /**
     * Filling Plan := Document
     */
    private final String documentPlan = "DOCUMENT";

    /**
     * Filling Plan := password
     */
    private final String passwordPlan = "PASSWORD";

    /**
     * Check that the specified value is in the list of fillingplan possible
     * values: all, document, password
     *
     * @param valueToCheck
     * @return
     */
    private boolean checkFilingPlan(String valueToCheck) {

        if (valueToCheck == null) {
            return false;
        }

        //Create the list of possible values
        List<String> possibleValuesList = new ArrayList<String>();

        //add all the posible values
        possibleValuesList.add(documentPlan);
        possibleValuesList.add(passwordPlan);

        return possibleValuesList.contains(valueToCheck.toLowerCase());

    }

    @SuppressWarnings("deprecation")
    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {
        String safeboxPath;

        CmdLineParser myParser = new CmdLineParser(this);

        super.parseAndRun(arguments);

        try {
            myParser.parseArgument(arguments);
        } catch (CmdLineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!checkFilingPlan(fillingPlan.toUpperCase())) {

            System.out.println("Le parametre --boxPath est invalide \n");
            printHelp();
            return;
        }

        System.out.println(" applicantPath : " + applicantPath);
        System.out.println(" boxPath : " + boxPath);
        System.out.println(" orgUnitPath : " + orgUnitPath);
        System.out.println(" boxName : " + boxName);
        System.out.println(" fillingPlan : " + fillingPlan);

        try {
            AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory(), soapMessage);
            D3SSafeboxManager safeboxManager = new D3SSafeboxManager(authProxy);
            String response = safeboxManager.createSafeboxes(
                    D3SConstants.USER_PATH_PREFIX + "/" + applicantPath,
                    D3SConstants.SAFEBOX_PATH_PREFIX + "/" + boxPath,
                    D3SConstants.ORGUNIT_PATH_PREFIX + "/" + orgUnitPath,
                    D3SConstants.FILINGPLAN_PATH_PREFIX + "/" + fillingPlan,
                    boxName,
                    motivation);

            //Print number of safeboxes found
            System.out.println("parseAndRun :: safeboxes created " + response);

            log.println(this.getClass().getName() + " SUCCESS");
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CmdLineException(e.getMessage());
        } catch (EnvironmentFaultException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CmdLineException(e.getMessage());
        } catch (UserFaultException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CmdLineException(e.getMessage());
        }

    }

    /**
     * @TODO : todo
     */
    @Override
    public void printHelp() {
        CmdLineParser myParser = new CmdLineParser(this);

        myParser.printUsage(System.out);
    }

}
