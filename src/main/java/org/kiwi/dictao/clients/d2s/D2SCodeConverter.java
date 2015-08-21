package org.kiwi.dictao.clients.d2s;

import org.kiwi.dictao.responses.d2s.signatureEx.D2SStatus;
import org.kiwi.dictao.responses.d2s.signatureEx.OpStatus;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D2SCodeConverter {

    @Option(name = "--opStatus", required = true, usage = "opStatus")
    protected Integer opStatus = null;
    @Option(name = "--detailedStatus", required = false, usage = "detailedStatus")
    protected Integer detailedStatus = null;

    public static void main(String[] args) {
        D2SCodeConverter monAppel = new D2SCodeConverter();
        monAppel.parseAndRun(args);
    }

    public void parseAndRun(String[] arguments) {
        CmdLineParser monParseur = new CmdLineParser(this);

        try {
            monParseur.parseArgument(arguments);

            System.out.println(new OpStatus(opStatus));
            if (opStatus == 0) {
                System.out.println(new D2SStatus(detailedStatus));
            }

        } catch (CmdLineException ex) {
            System.err.println("Erreur : " + ex.getMessage());
            monParseur.printUsage(System.err);
        }
    }

}
