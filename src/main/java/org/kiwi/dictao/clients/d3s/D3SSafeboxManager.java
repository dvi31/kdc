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
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Affiche la liste des coffres disponibles pour l'utilisateur
 *
 * @author i21653q
 *
 */
public class D3SSafeboxManager {

    private final Logger log = Logger.getInstance();
    private final AuthorityProxy authProxy;

    /**
     *
     * @param authProxy
     */
    public D3SSafeboxManager(AuthorityProxy authProxy) {
        this.authProxy = authProxy;
    }

    /**
     *
     * @param userPath
     * @param containerPath
     * @param orgUnitPath
     * @param filingPlan
     * @param name
     * @param motivation
     * @return
     * @throws EnvironmentFaultException
     * @throws UserFaultException
     */
    public String createSafeboxes(String userPath, String containerPath,
            String orgUnitPath, String filingPlan, String name, String motivation)
            throws EnvironmentFaultException, UserFaultException {
        SafeboxPort safeboxPort;

        /**
         * Get safeboxport
         */
        if (this.authProxy == null) {
            return null;
        }

        //Initialize safeboxPort
        safeboxPort = this.authProxy.getSafeboxPort();

        final Safebox safebox = new Safebox();
        safebox.setName(name);
//        safebox.setDescription(name);
        safebox.setMimeType(MimeType.SAFEBOX);
        safebox.setContentMimeType(MimeType.DEPOSIT);
        safebox.setApprobationsNumber(0L);
        safebox.setCipherMode(CipherMode.CERTLIST);
        safebox.setFilingPlanPath(filingPlan);

        //Requetes WS pour la récupération de la liste des coffres
        String response = safeboxPort.createSafebox(userPath,
                motivation,
                containerPath,
                safebox,
                orgUnitPath);

        return response;
    }

    /**
     * Get the list of safeboxes for the current users
     *
     * @param userPath
     * @param safeboxPath
     * @param motivation
     * @param maxResult : max number of safebox to return, if -1, return all the
     * safeboxes
     * @return List<Safebox>
     * @throws UserFaultException
     * @throws EnvironmentFaultException
     */
    public List<Safebox> findSafeboxes(String userPath, String safeboxPath, String motivation, int maxResult) throws EnvironmentFaultException, UserFaultException {
        List<Safebox> safeboxList = null;
        SafeboxPort safeboxPort;
        FindSafeboxesResponse safeboxResponse = null;

        //Initialize safeboxPort
        safeboxPort = this.authProxy.getSafeboxPort();

        //Requ�tes WS pour la récupération de la liste des coffres
        safeboxResponse = safeboxPort.findSafeboxes(userPath,
                motivation,
                safeboxPath,
                null,
                null,
                maxResult);

        //Traitement des safebox
        safeboxList = safeboxResponse.getSafeboxes();

        return safeboxList;

    }

    public void printInfo(Safebox safebox) {

        if (safebox != null) {
            System.out.println("ID \t: " + safebox.getID());
            System.out.println("Nom du coffre \t: " + safebox.getName());
            System.out.println("Description \t : " + safebox.getDescription());
            System.out.println("Nombre d'approbations \t: " + safebox.getApprobationsNumber());
            System.out.println("Type de coffre \t: " + safebox.getFilingPlanPath());
            System.out.println("Date de création \t: " + safebox.getCreationDate());
            System.out.println("Taille de l'historique \t: " + safebox.getDepositHistoricSize());
        } else {
            System.out.println("Aucune information a afficher");
        }
    }

}
