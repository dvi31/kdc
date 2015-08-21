package org.kiwi.dictao.responses.d2s.signatureEx;

import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;

public class D2SStatus extends ListeReponses<Integer> {

    public D2SStatus(Integer leCode) {
        super(leCode);
    }

    public enum D2SError {

        NA_x8000(0x8000),
        CERTIFICAT_ENTITE_INVALID(0x4000),
        HORADATAGE_IMPOSSIBLE(0x2000),
        DOC_SEMANTIC_NA(0x1800),
        DOCUMENT_INSTABLE(0x1000),
        DOCUMENT_STABLE(0x0800),
        ALGORITHME_HASH_EXPIRE(0x0400),
        NA_x0200(0x0200),
        NA_x0100(0x0100),
        NA_x0080(0x0080),
        NA_x0040(0x0040),
        NA_x0020(0x0020),
        NA_x0010(0x0010),
        NA_x0008(0x0008),
        NA_x0004(0x0004),
        NA_x0002(0x0002),
        NA_x0001(0x0001),
        NO_ERROR(0x0000);

        public final int value;

        D2SError(int value) {
            this.value = value;
        }
    }

    protected void computeListe(Integer leCode) {

        if ((leCode & D2SError.CERTIFICAT_ENTITE_INVALID.value) == D2SError.CERTIFICAT_ENTITE_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat entité invalide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat entité valide"));
        }
        
        if ((leCode & D2SError.HORADATAGE_IMPOSSIBLE.value) == D2SError.HORADATAGE_IMPOSSIBLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Horodatage de réception de la signature impossible"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Horodatage de réception de la signature effectué (ou non demandé)"));
        }

        if ((leCode & D2SError.DOC_SEMANTIC_NA.value) == D2SError.DOC_SEMANTIC_NA.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        } else if ((leCode & D2SError.DOCUMENT_INSTABLE.value) == D2SError.DOCUMENT_INSTABLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Stabilité sémantique du document : instable"));
        } else if ((leCode & D2SError.DOCUMENT_STABLE.value) == D2SError.DOCUMENT_STABLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Stabilité sémantique du document : stable"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.NEUTRE, "Vérification de la stabilité sémantique du document non demandée"));
        }

        if ((leCode & D2SError.ALGORITHME_HASH_EXPIRE.value) == D2SError.ALGORITHME_HASH_EXPIRE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Algorithme de hash expiré"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Algorithme de hash valide"));
        }
        
        if ((leCode & D2SError.NA_x8000.value) == D2SError.NA_x8000.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0200.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0100.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0080.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0040.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0020.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0010.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0008.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0004.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0002.value
                || (leCode & D2SError.NA_x0200.value) == D2SError.NA_x0001.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        }
    }
}
