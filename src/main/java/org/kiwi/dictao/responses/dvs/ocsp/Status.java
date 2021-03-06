package org.kiwi.dictao.responses.dvs.ocsp;

import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;

public class Status extends ListeReponses<Integer>{

    public Status(Integer leCode) {super(leCode);}
    
    @Override
    protected void computeListe(Integer leCode) {
                switch (leCode) {
            case 0:
                maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Succès"));
                break;
            case 1:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Requête malformée"));
                break;
            case 2:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur interne"));
                break;
            case 3:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Serveur occupé, réessayer plus tard"));
                break;
            case 4:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement attribuée (?)"));
                break;
            case 5:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "La requète doit être signée"));
                break;
            case 6:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Non-autorisée"));
                break;
            default:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non référencée (hors protocole)"));
        }
    }
}
