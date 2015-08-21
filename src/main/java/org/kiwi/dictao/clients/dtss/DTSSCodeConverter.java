package org.kiwi.dictao.clients.dtss;

import org.kiwi.dictao.clients.dtss.*;
import org.kiwi.dictao.responses.dtss.insertTimeStampEx.DTSSStatus;
import org.kiwi.dictao.responses.dtss.insertTimeStampEx.OpStatus;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DTSSCodeConverter {

    @Option(name = "--opStatus", required = true, usage = "opStatus")
    protected Integer opStatus = null;
    @Option(name = "--detailedStatus", required = false, usage = "detailedStatus")
    protected Integer detailedStatus = null;

    public static void main(String[] args) {
        DTSSCodeConverter monAppel = new DTSSCodeConverter();
        monAppel.parseAndRun(args);
    }

    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            monParseur.parseArgument(arguments);

            System.out.println(new OpStatus(opStatus));
            if (opStatus == 0) {
                System.out.println(new DTSSStatus(detailedStatus));
            }

        } catch (CmdLineException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            monParseur.printUsage(System.err);
        }
    }

}
