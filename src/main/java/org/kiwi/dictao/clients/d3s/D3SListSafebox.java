package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.EnvironmentFaultException;
import com.dictao.d3s.wsdl.v2.authority.SafeboxPort;
import com.dictao.d3s.wsdl.v2.authority.UserFaultException;
import com.dictao.d3s.wsdl.v2_1.authority.FindSafeboxesResponse;
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
public class D3SListSafebox extends D3SWebService {

    @Option(name = "--search-path", required = false, usage = "chemin de de recherche")
    protected String searchPath = null;

    @Option(name = "--criteria", required = false, usage = "(optionnel)crit�re de recherche")
    protected String criteria = null;

    @Option(name = "--orderBy", required = false, usage = "(optionnel)ordre de pr�sentation des r�sultats")
    protected String orderBy = null;

    @Option(name = "--maxResult", required = false, usage = "(optionnel) Nombre max de r�sultat � afficher, tous les r�sultats par d�faut)")
    protected String maxResult = "20";

    @Option(name = "--with-safebox-info", required = false, usage = "(optionnel) Nombre max de r�sultat � afficher, tous les r�sultats par d�faut)")
    protected boolean withSafeboxInfo = false;

    @Option(name = "--fillingPlan", required = false, usage = "(optionnel) Valeur possible : \n"
            + "'document' pour les coffres de document;\n"
            + "'password' pour les coffres de mot de passe;\n"
            + "'all' pour tous les types de coffres")
    protected String fillingPlan = "all";
    /**
     * Possible values for filingPlan , only lower case string
     *
     */
    /**
     * Filling Plan := Document
     */
    private final String documentPlan = "document";

    /**
     * Filling Plan := password
     */
    private final String passwordPlan = "password";

    /**
     * Filling Plan := document,password
     */
    private final String allPlan = "all";

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
        possibleValuesList.add(allPlan);

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

        if (!checkFilingPlan(fillingPlan)) {

            System.out.println("Le parametre --filingPlan est invalide \n");
            printHelp();
            return;
        }

        System.out.println(" applicantPath : " + applicantPath);
        System.out.println(" searchPath : " + searchPath);
        System.out.println(" criteria : " + criteria);
        System.out.println(" orderBy : " + orderBy);
        System.out.println(" maxResult : " + maxResult);

        //initialize safebox directory
        if (searchPath != null) {
            safeboxPath = D3SConstants.SAFEBOX_PATH_PREFIX + searchPath;
        }

        //get safeboxes' list
        try {
            AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory(), soapMessage);
            D3SSafeboxManager safeboxManager = new D3SSafeboxManager(authProxy);
            List<Safebox> boxes = safeboxManager.findSafeboxes(
                    D3SConstants.USER_PATH_PREFIX + "/" + applicantPath,
                    searchPath,
                    motivation,
                    Integer.decode(maxResult));
            if (boxes == null || boxes.isEmpty()) {
                //No safebox found
                System.out.println("parseAndRun :: no safeboxes");
                return;
            }

            //Print number of safeboxes found
            System.out.println("parseAndRun :: found " + boxes.size() + " safeboxes");

            if (withSafeboxInfo) {
                for (Safebox box : boxes) {
                    System.out.println("*********************************************");
                    safeboxManager.printInfo(box);
                }
            }

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
