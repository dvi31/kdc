package org.kiwi.dictao.clients.d3s;

import java.io.IOException;
import java.net.UnknownHostException;

import org.kiwi.dictao.clients.HttpsWebServiceClient;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Classe qui definit le sparametres de bases pour un appel a D3S
 *
 * @author i21653q
 *
 */
public abstract class D3SWebService extends HttpsWebServiceClient {

    //@Option(name="--base-url", required=true, usage ="URL de base du service")
    //protected String baseURL = null;
    @Option(name = "--storage-ws", required = true, usage = "Adresse du WebService Storage")
    protected String storageWs = null;

    @Option(name = "--applicant-path", required = true, usage = "chemin de l'utilisateur � l'origine de l'action")
    protected String applicantPath = null;

    @Option(name = "--motivation", required = true, usage = "Motif de l'action")
    protected String motivation = null;

    @Option(name = "--resultFile", required = false, usage = "(optionnel) Chemin vers le fichier excel (extension .xls)de r�sultat")
    protected boolean resultFile = false;

    @Option(name = "--verbose", required = false, usage = "(optionnel) Activation du mode debug avanc�")
    protected boolean verbose = false;

    @Option(name = "--help", required = false, usage = "(optionnel) Affichage de l'aide")
    protected boolean help = false;

    @Option(name = "--log-file-path", required = false, usage = "(optionnel) Chemin vers le fichier de log")
    protected String logFilePath = null;

    protected Logger log = Logger.getInstance();

    /**
     * Recuperation et verification des parametres d'appels suivants :
     * --truststore-pass --applicant-path --motivation --verbose --help
     *
     * @author
     * @version D3S 4.x
     * @param arguments : list of arguments
     * @throws CmdLineException
     * @throws UnknownHostException
     * @throws IOException
     * @return
     */
    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {
        CmdLineParser monParseur = new CmdLineParser(this);

        /**
         * Verification des parametres
         */
        super.parseAndRun(arguments);
        monParseur.parseArgument(arguments);

        if (logFilePath != null) {
            Logger.setLogFile(logFilePath);
        }

        /**
         * *
         * Si l'un des param�tres est '--help' Rian � faire
         */
        if (help) {
            return;
        }

        /**
         * V�rification de la pr�sence d'un mot de passe pour le trustore Si le
         * mot de passe est indiqu�, il est configur� dans ssl
         */
        if (trustStorePassword != null) {
            System.setProperty("javax.net.ssl. trustStorePassword", trustStorePassword);
        }

        /**
         * Affichage des paramètres
         */
        log.println("\n********************************");
        log.println("***** Liste des parametres *****");
        log.println("********************************");
        for (int i = 0; i < arguments.length; i++) {
            String argV = arguments[i];
            if (argV.contains("-pass")) {
                log.print(argV);
                log.println("\t" + "********");
                i++;
            } else if (argV.contains("--")) {
                log.print(argV);
            } else {
                log.println("\t" + argV);
            }
        }
        log.println("\n********************************\n");

    }

    /**
     * @TODO : todo
     */
    public void printHelp() {

    }
}
