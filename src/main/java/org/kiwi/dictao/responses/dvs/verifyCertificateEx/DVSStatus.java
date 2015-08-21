package org.kiwi.dictao.responses.dvs.verifyCertificateEx;

import java.math.BigInteger;
import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;
import org.kiwi.dictao.responses.dvs.verifyCertificateEx.DVSStatus.DVSError;

public class DVSStatus extends ListeReponses<BigInteger> {

    public DVSStatus(BigInteger leCode) {
        super(leCode);
    }

    public enum DVSError {

        CERTIFICATE_INVALID(0x80000000),
        CERTIFICAT_EXPIRE(0x40000000),
        CERTIFICAT_NOT_LISTED(0x20000000),
        CERTIFICAT_DN_NOT_LISTED(0x10000000),
        KEY_USAGE_NOT_COMPLIANT(0x08000000),
        OID_NON_AUTORISE(0x04000000),
        QCSTATEMENTS_NOT_COMPLIANT(0x02000000),
        BUSINESS_CRL_INVALID(0x01000000),
        ALGORITHME_INTERDIT(0x00800000),
        NA_x0040(0x00400000),
        NA_x0020(0x00200000),
        NA_x0010(0x00100000),
        NA_x000C(0x000C0000),
        VALIDATION_OCSP_DISPONIBLE(0x00080000),
        VALIDATION_OCSP_CERT(0x00040000),
        CERTIFICAT_AC_NON_AUTORISE(0x00030000),
        CERTIFICAT_DONNEES_VALIDATION_MANQUANTE(0x00020000),
        CERTIFICAT_REVOQUE(0x00010000);

        public final long value;

        DVSError(long value) {
            this.value = value;
        }
    }

    @Override
    protected void computeListe(BigInteger leCode) {
        long iDVSDetailedStatus = leCode.longValue();

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_AC_NON_AUTORISE.value) == DVSError.CERTIFICAT_AC_NON_AUTORISE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC émettrice non référencée"));
        } else if ((iDVSDetailedStatus & DVSError.CERTIFICAT_DONNEES_VALIDATION_MANQUANTE.value) == DVSError.CERTIFICAT_DONNEES_VALIDATION_MANQUANTE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
        } else if ((iDVSDetailedStatus & DVSError.CERTIFICAT_REVOQUE.value) == DVSError.CERTIFICAT_REVOQUE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat révoqué"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat valide"));
        }

        if ((iDVSDetailedStatus & DVSError.NA_x000C.value) == DVSError.NA_x000C.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        } else if ((iDVSDetailedStatus & DVSError.VALIDATION_OCSP_DISPONIBLE.value) == DVSError.VALIDATION_OCSP_DISPONIBLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP dès que disponible"));
        } else if ((iDVSDetailedStatus & DVSError.VALIDATION_OCSP_CERT.value) == DVSError.VALIDATION_OCSP_CERT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP pour le certificat de sinature"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "CRL et ARL pour toute la chaîne"));
        }

        if ((iDVSDetailedStatus & 3) == 3) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC en dehors de sa période de validité"));
        } else if ((iDVSDetailedStatus & 3) == 2) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
        } else if ((iDVSDetailedStatus & 3) == 1) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins une AC révoquée"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "La chaîne est valide"));
        }

        if ((iDVSDetailedStatus & DVSError.ALGORITHME_INTERDIT.value) == DVSError.ALGORITHME_INTERDIT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Algorithme interdit"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Algorithme conforme"));
        }

        if ((iDVSDetailedStatus & DVSError.BUSINESS_CRL_INVALID.value) == DVSError.BUSINESS_CRL_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Validation métier d\'une CRL non valide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Validation métier d\'une CRL valide (ou non paramétrée)"));
        }

        if ((iDVSDetailedStatus & DVSError.QCSTATEMENTS_NOT_COMPLIANT.value) == DVSError.QCSTATEMENTS_NOT_COMPLIANT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Extensions QCStatements non conformes"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Extensions QCStatements conformes (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.OID_NON_AUTORISE.value) == DVSError.OID_NON_AUTORISE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "OID de la Politique de Certification non référencée"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OID de la Politique de Certification référencée (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.KEY_USAGE_NOT_COMPLIANT.value) == DVSError.KEY_USAGE_NOT_COMPLIANT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Usage de la clé non conforme"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Usage de la clé conforme (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_DN_NOT_LISTED.value) == DVSError.CERTIFICAT_DN_NOT_LISTED.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le DN du certificat n'est pas trouvé dans la liste blanche"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le DN du certificat est trouvé dans la liste blanche (ou aucune liste de DN n'est configurée)"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_NOT_LISTED.value) == DVSError.CERTIFICAT_NOT_LISTED.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le certificat n'est pas trouvé dans la liste blanche"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le certificat est trouvé dans la liste blanche de certificats (ou aucune liste n'est configurée)"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_EXPIRE.value) == DVSError.CERTIFICAT_EXPIRE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat de signature en dehors de sa période de validité"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat de signature en cours de validité"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICATE_INVALID.value) == DVSError.CERTIFICATE_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le certificat est invalide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le certificat est valide (tous les contrôles effectués ont retournés le résultat attendu)"));
        }

        if ((iDVSDetailedStatus & DVSError.NA_x0040.value) == DVSError.NA_x0040.value
                || (iDVSDetailedStatus & DVSError.NA_x0020.value) == DVSError.NA_x0020.value
                || (iDVSDetailedStatus & DVSError.NA_x0010.value) == DVSError.NA_x0010.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        }
    }
}
