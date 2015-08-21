package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.EnvironmentFaultException;
import com.dictao.d3s.wsdl.v2.authority.UserFaultException;
import com.dictao.d3s.wsdl.v2_1.authority.FindDepositsResponse;
import com.dictao.d3s.xsd.v2010_10.common.Deposit;
import com.dictao.d3s.xsd.v2010_10.common.FileSourcePath;
import com.dictao.d3s.xsd.v2010_10.common.Metadata;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.kiwi.utils.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class D3SDepositSearch extends D3SWebService {

    @Option(name = "--search-path", required = false, usage = "chemin de de recherche")
    protected String searchPath = null;
    @Option(name = "--criteria", required = false, usage = "(optionnel)crit�re de recherche")
    protected String criteria = null;
    @Option(name = "--orderBy", required = false, usage = "(optionnel)ordre de pr�sentation des r�sultats")
    protected String orderBy = null;
    @Option(name = "--maxResult", required = false, usage = "(optionnel) Nombre max de r�sultat � afficher, tous les r�sultats par d�faut)")
    protected String maxResult = "20";

    @Option(name = "--with-deposit-info", required = false, usage = "(optionnel) Nombre max de r�sultat � afficher, tous les r�sultats par d�faut)")
    protected boolean withDepositInfo = false;
    @Option(name = "--filingplan-path", required = false, usage = "type de coffre (DOCUMENT ou PASSWORD)", metaVar = "DOCUMENT")
    private String filingplan_path = "PASSWORD";

    private LinkedList<Deposit> deposits = new LinkedList<Deposit>();

    public LinkedList<Deposit> getDeposits() {
        return deposits;
    }

    public static void main(String[] args) {
        D3SDepositSearch monAppel = new D3SDepositSearch();

        Logger log = Logger.getInstance();
        try {
            monAppel.parseAndRun(args);
        } catch (Exception e) {
            log.println(D3SVerifReadWrite.class.getName() + " FAILED");
            log.println(e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void parseAndRun(String[] arguments) throws CmdLineException, IOException {
        CmdLineParser myParser = new CmdLineParser(this);
        FindDepositsResponse depositResponse;
        List<Deposit> depositList;
        Deposit iterDeposit;
        boolean listAllSecrets = false;

        try {
            super.parseAndRun(arguments);
            myParser.parseArgument(arguments);

            if (help) {
                //affichage de l'aide
                myParser.printUsage(System.out);
                return;
            }

            System.out.println(" applicantPath : " + applicantPath);
            System.out.println(" motivation : " + motivation);
            System.out.println(" searchPath : " + searchPath);

            String userName = D3SConstants.USER_PATH_PREFIX + "/" + applicantPath;

            boolean isBoxPath = false;
            //path to safebox
            if (searchPath != null) {
                searchPath = D3SConstants.SAFEBOX_PATH_PREFIX + "/" + searchPath;
                isBoxPath = true;
            } else {
                searchPath = D3SConstants.FILINGPLAN_PATH_PREFIX + "?_name=" + filingplan_path;
            }

            AuthorityProxy authProxy = new AuthorityProxy(wsUri.toString(), buildSSLSocketFactory(), soapMessage);
            if (listAllSecrets) {
	       	//search for deposit
                
                //PASSWORD
                depositResponse = authProxy.getDepositPort().findDeposits(
                        userName,
                        motivation,
                        D3SConstants.FILINGPLAN_PATH_PWD,
                        null,
                        null,
                        Integer.decode(maxResult));

                depositList = depositResponse.getDeposits();

                //DOCUMENT
                depositResponse = authProxy.getDepositPort().findDeposits(
                        userName,
                        motivation,
                        D3SConstants.FILINGPLAN_PATH_DOC,
                        null,
                        null,
                        Integer.decode(maxResult));

                depositList.addAll(depositResponse.getDeposits());

            } else {

                //search for deposit
                depositResponse = authProxy.getDepositPort().findDeposits(
                        userName,
                        motivation,
                        searchPath,
                        null,
                        null,
                        Integer.decode(maxResult));

                depositList = depositResponse.getDeposits();
            }

            if ((depositList == null) || depositList.isEmpty()) {
                System.out.println("parseAndRun :: no deposits");
            } else {
                deposits.addAll(depositList);
                Iterator<Deposit> iterator = depositList.iterator();
                System.out.println("parseAndRun :: found " + depositList.size() + " deposits");

                int count = 0;
                while (iterator.hasNext()) {
                    iterDeposit = iterator.next();
                    Map<String, String> deposit = toMap(iterDeposit);
                    String depositPath = "/DEPOSIT?"
                            + (isBoxPath ? "_boxPath=" : "_fpPath=") + searchPath
                            + "&appLogin=" + deposit.get("appLogin")
                            + "&appDomainName=" + deposit.get("appDomainName");
                    System.out.println("*********** n°" + count + " ***********");
                    System.out.println("  Deposit path : " + depositPath);
                    if (withDepositInfo) {
                        System.out.println("  >>> ID : " + iterDeposit.getID());
                        System.out.println("  >>> " + "Date de creation : " + iterDeposit.getCreationDate());
                        System.out.println("  >>> " + "Date d'expiration : " + iterDeposit.getExpirationDate());
                        for (Map.Entry entry : deposit.entrySet()) {
                            System.out.println("  >>> " + entry.getKey() + ": " + entry.getValue());
                        }
                    }
                    count++;
                }
            }

        } catch (NumberFormatException e) {
            throw new CmdLineException(e.getMessage());
        } catch (UserFaultException e) {
            throw new CmdLineException(e.getMessage());
        } catch (EnvironmentFaultException e) {
            throw new CmdLineException(e.getMessage());
        }

        log.println(this.getClass().getName() + " SUCCESS");
    }

    private Map<String, String> toMap(Deposit deposit) {
        Map<String, String> result = new HashMap<String, String>();
        List<Metadata> metadatList = deposit.getMetadatas().getMetadata();
        Iterator<Metadata> metadatIterator = metadatList.iterator();
        while (metadatIterator.hasNext()) {
            Metadata curIndex = metadatIterator.next();
            switch (curIndex.getType()) {
                case STRING_TYPE:
                    result.put(curIndex.getName(), curIndex.getValue().getStringValue());
                    break;
                case DATE_TYPE:
                    result.put(curIndex.getName(), curIndex.getValue().getDateValue().toString());
                    break;
                case LONG_TYPE:
                    result.put(curIndex.getName(), curIndex.getValue().getLongValue().toString());
                    break;
            }
        }
        return result;
    }
}
