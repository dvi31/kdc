package org.kiwi.dictao.clients.dvs;

import java.math.BigInteger;
import org.kiwi.dictao.responses.dvs.verifySignatureEx.DVSGlobalStatus;
import org.kiwi.dictao.responses.dvs.verifySignatureEx.DVSStatus;
import org.kiwi.dictao.responses.dvs.verifySignatureEx.OpStatus;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DVSSignCodeConverter {

    @Option(name = "--opStatus", required = true, usage = "opStatus")
    protected Integer opStatus = null;
    @Option(name = "--globalStatus", required = false, usage = "globalStatus")
    protected Integer globalStatus = null;
    @Option(name = "--detailedStatus", required = false, usage = "detailedStatus")
    protected String detailedStatus = null;

    public static void main(String[] args) {
        DVSSignCodeConverter monAppel = new DVSSignCodeConverter();
        monAppel.parseAndRun(args);
    }

    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            monParseur.parseArgument(arguments);

            System.out.println(new OpStatus(opStatus));
            if (opStatus == 0) {
                System.out.println(new DVSGlobalStatus(globalStatus));
                System.out.println(new DVSStatus(new BigInteger(detailedStatus)));
            }
        } catch (CmdLineException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            monParseur.printUsage(System.err);
        }
    }

}
