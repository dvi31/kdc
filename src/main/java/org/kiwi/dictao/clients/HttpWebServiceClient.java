package org.kiwi.dictao.clients;

import com.dictao.d2s.ws.D2S;
import com.dictao.d2s.ws.D2SSoap;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.kiwi.utils.TraceHandler;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class HttpWebServiceClient {

    @Option(name = "--ws-uri", required = true, usage = "Adresse du WebService")
    protected URL wsUri = null;
    @Option(name = "--messages-soap", required = false, usage = "(optionnel) Montrer les message SOAP")
    protected Boolean soapMessage = false;
    @Option(name = "--wsdl-uri", required = false, usage = "(optionnel) URI du WSDL")
    protected URL wsdlUri = null;
    private long msStart, msStop;

    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {

        String banierre = "Client " + this.getClass().getName();
        System.out.println(banierre + '\n' + new String(new char[banierre.length()]).replace('\0', '='));

        CmdLineParser monParseur = new CmdLineParser(this);
        monParseur.parseArgument(arguments);

        System.out.println("URI WebService : " + wsUri);
    }

    public void beginCall() {
        msStart = System.currentTimeMillis();
    }


    public void endCall() {
        msStop = System.currentTimeMillis();
        System.out.println("Temps de réponse de la couche d'appel WebService : " + (msStop - msStart) + " ms\n");
    }
}
