package org.kiwi.dictao.responses.dtss.ts;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;

public class TSFailureInfo extends ListeReponses<PKIFailureInfo> {

    public TSFailureInfo(PKIFailureInfo lInfo) {
        super(lInfo);
    }

    @Override
    protected void computeListe(PKIFailureInfo leCode) {
        switch (leCode.intValue()) {
            case PKIFailureInfo.badAlg:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badAlg: Algorithme non supporté ou non reconnu"));
                break;
            case PKIFailureInfo.badMessageCheck:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badMessageCheck"));
                break;
            case PKIFailureInfo.badRequest:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badRequest: Transaction non supportée ou non permise"));
                break;
            case PKIFailureInfo.badTime:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badTime"));
                break;
            case PKIFailureInfo.badCertId:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badCertId"));
                break;
            case PKIFailureInfo.badDataFormat:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badDataFormat: La donnée soumise est au mauvais format"));
                break;
            case PKIFailureInfo.wrongAuthority:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "wrongAuthority"));
                break;
            case PKIFailureInfo.incorrectData:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "incorrectData"));
                break;
            case PKIFailureInfo.missingTimeStamp:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "missingTimeStamp"));
                break;
            case PKIFailureInfo.badPOP:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badPOP"));
                break;
            case PKIFailureInfo.certRevoked:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "certRevoked"));
                break;
            case PKIFailureInfo.certConfirmed:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "certConfirmed"));
                break;
            case PKIFailureInfo.wrongIntegrity:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "wrongIntegrity"));
                break;
            case PKIFailureInfo.badRecipientNonce:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badRecipientNonce"));
                break;
            case PKIFailureInfo.timeNotAvailable:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "timeNotAvailable: La source de temps n'est pas disponible"));
                break;
            case PKIFailureInfo.unacceptedPolicy:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "unacceptedPolicy: La politique d'horodatage n'est pas supportée"));
                break;
            case PKIFailureInfo.unacceptedExtension:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "unacceptedExtension: L'extension demandée n'est pas supportée"));
                break;
            case PKIFailureInfo.addInfoNotAvailable:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "addInfoNotAvailable: Les informations aditionnelles demandées ne sont pas disponibles ou comprises"));
                break;
            case PKIFailureInfo.badSenderNonce:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badSenderNonce"));
                break;
            case PKIFailureInfo.badCertTemplate:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "badCertTemplate"));
                break;
            case PKIFailureInfo.signerNotTrusted:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "signerNotTrusted"));
                break;
            case PKIFailureInfo.transactionIdInUse:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "transactionIdInUse"));
                break;
            case PKIFailureInfo.unsupportedVersion:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "unsupportedVersion"));
                break;
            case PKIFailureInfo.notAuthorized:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "notAuthorized"));
                break;
            case PKIFailureInfo.systemUnavail:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "systemUnavail"));
                break;
            case PKIFailureInfo.systemFailure:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "systemFailure: Erreur système"));
                break;
            case PKIFailureInfo.duplicateCertReq:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "duplicateCertReq"));
                break;
            default:
                maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non référencée !"));
        }
        String laChaine = leCode.getString();
        if (laChaine != null) {
            maListeDeReponses.add(new Reponse(TypeReponse.NEUTRE, "Information : " + laChaine));
        }
    }
}
