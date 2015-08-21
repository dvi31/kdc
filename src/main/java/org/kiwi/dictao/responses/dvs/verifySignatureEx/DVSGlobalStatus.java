package org.kiwi.dictao.responses.dvs.verifySignatureEx;

import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;

public class DVSGlobalStatus extends ListeReponses<Integer> {

    public DVSGlobalStatus(Integer leCode) {
        super(leCode);
    }

    public enum DVSGlobalStatusCode {

        NA_x8000(0x8000),
        INVALID_SIGNATURE(0x4000),
        CRL_UPDATE_ERROR(0x2000),
        COMPLETION_ERROR(0x1000),
        NA_x0800(0x0800),
        NA_x0400(0x0400),
        NA_x0200(0x0200),
        NA_x0100(0x0100),
        NA_x0080(0x0080),
        NA_x0040(0x0040),
        NA_x0020(0x0020),
        NA_x0010(0x0010),
        NA_x0008(0x0008),
        NA_x0004(0x0004),
        NA_x0002(0x0002),
        NA_x0001(0x0001);

        public final long value;

        DVSGlobalStatusCode(long value) {
            this.value = value;
        }
    }

    @Override
    protected void computeListe(Integer leCode) {

        if ((leCode & DVSGlobalStatusCode.CRL_UPDATE_ERROR.value) == DVSGlobalStatusCode.CRL_UPDATE_ERROR.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Mise à jour des CRLs impossible"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Mise à jour des CRLs forcée avant exécution ou mise à jour non demandée"));
        }

        if ((leCode & DVSGlobalStatusCode.INVALID_SIGNATURE.value) == DVSGlobalStatusCode.INVALID_SIGNATURE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins une signature est invalide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Toutes les signatures sont valides (tous les contrôles effectués ont retournés le résultats attendu)"));
        }

        if ((leCode & DVSGlobalStatusCode.NA_x8000.value) == DVSGlobalStatusCode.NA_x8000.value
                || (leCode & DVSGlobalStatusCode.NA_x0800.value) == DVSGlobalStatusCode.NA_x0800.value
                || (leCode & DVSGlobalStatusCode.NA_x0400.value) == DVSGlobalStatusCode.NA_x0400.value
                || (leCode & DVSGlobalStatusCode.NA_x0200.value) == DVSGlobalStatusCode.NA_x0200.value
                || (leCode & DVSGlobalStatusCode.NA_x0100.value) == DVSGlobalStatusCode.NA_x0100.value
                || (leCode & DVSGlobalStatusCode.NA_x0080.value) == DVSGlobalStatusCode.NA_x0080.value
                || (leCode & DVSGlobalStatusCode.NA_x0040.value) == DVSGlobalStatusCode.NA_x0040.value
                || (leCode & DVSGlobalStatusCode.NA_x0020.value) == DVSGlobalStatusCode.NA_x0020.value
                || (leCode & DVSGlobalStatusCode.NA_x0010.value) == DVSGlobalStatusCode.NA_x0010.value
                || (leCode & DVSGlobalStatusCode.NA_x0008.value) == DVSGlobalStatusCode.NA_x0008.value
                || (leCode & DVSGlobalStatusCode.NA_x0004.value) == DVSGlobalStatusCode.NA_x0004.value
                || (leCode & DVSGlobalStatusCode.NA_x0002.value) == DVSGlobalStatusCode.NA_x0002.value
                || (leCode & DVSGlobalStatusCode.NA_x0001.value) == DVSGlobalStatusCode.NA_x0001.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        }
    }
}
