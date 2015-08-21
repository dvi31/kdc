package org.kiwi.dictao.responses.dvs.verifySignatureEx;

import java.math.BigInteger;
import org.kiwi.dictao.responses.ListeReponses;
import org.kiwi.dictao.responses.Reponse;
import org.kiwi.dictao.responses.Reponse.TypeReponse;

public class DVSStatus extends ListeReponses<BigInteger> {

    public DVSStatus(BigInteger leCode) {
        super(leCode);
    }

    public enum DVSError {

        VERIFICATION_ERROR(0x80000000),
        CERTIFICAT_EXPIRE(0x40000000),
        CERTIFICAT_NOT_LISTED(0x20000000),
        CERTIFICAT_DN_NOT_LISTED(0x10000000),
        KEY_USAGE_NOT_COMPLIANT(0x08000000),
        OID_NON_AUTORISE(0x04000000),
        QCSTATEMENTS_NOT_COMPLIANT(0x02000000),
        BUSINESS_CRL_INVALID(0x01000000),
        ALGORITHME_INTERDIT(0x00800000),
        NA_x00000040(0x00400000),
        AC_HORS_VALIDITE(0x00300000),
        AC_REVOQUEE(0x00200000),
        AC_DONNEES_VALIDATION_MANQUANTE(0x00100000),
        VALIDATION_OCSP_NA(0x000C0000),
        VALIDATION_OCSP_DISPONIBLE(0x00080000),
        VALIDATION_OCSP_CERT(0x00040000),
        CERTIFICAT_AC_NON_AUTORISE(0x00030000),
        CERTIFICAT_DONNEES_VALIDATION_MANQUANTE(0x00020000),
        CERTIFICAT_REVOQUE(0x00010000),
        HORODATE_IMPOSSIBLE(0x00008000),
        SIGNATURE_INCOMPLIANT(0x00004000),
        DOC_SEMANTIC_NA(0x00003000),
        DOC_SEMANTIC_IMPOSSIBLE(0x00002000),
        DOC_SEMANTIC_INSTABLE(0x00001000),
        SIGNATURE_INVALID(0x00000800),
        TEMPORAL_WINDOW_OUT(0x00000400),
        HORODATAGE_INVALID(0x00000200),
        REFERENCE_NOT_FOUND(0x00000100),
        ADES_REF_INCOHERENT(0x00000080),
        XADES_ATTRIBUTE_NOT_FOUND(0x00000040),
        NA_x00000020(0x00000020),
        NA_x00000010(0x00000010),
        NA_x00000008(0x00000008),
        NA_x00000004(0x00000004),
        NA_x00000002(0x00000002),
        NA_x00000001(0x00000001),
        NO_ERROR(0x00000000);

        public final long value;

        DVSError(long value) {
            this.value = value;
        }
    }

    @Override
    protected void computeListe(BigInteger leCode) {
        long iDVSDetailedStatus = leCode.intValue();

        if ((iDVSDetailedStatus & DVSError.VERIFICATION_ERROR.value) == DVSError.VERIFICATION_ERROR.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins un des contrôles effectué n'a pas retourné le résultat attendu"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Tous les contrôles effectués sur la signature et le certificat du signataire ont retourné le résultat attendu"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_EXPIRE.value) == DVSError.CERTIFICAT_EXPIRE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat de signature en dehors de sa période de validité"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat de signature en cours de validité"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_NOT_LISTED.value) == DVSError.CERTIFICAT_NOT_LISTED.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le certificat n'est pas trouvé dans la liste blanche/noire"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le certificat est trouvé dans la liste blanche/noire de certificats (ou aucune liste n'est configurée)"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_DN_NOT_LISTED.value) == DVSError.CERTIFICAT_DN_NOT_LISTED.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le DN du certificat n'est pas trouvé dans la liste blanche/noire"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le DN du certificat est trouvé dans la liste blanche/noire (ou aucune liste de DN n'est configurée)"));
        }

        if ((iDVSDetailedStatus & DVSError.KEY_USAGE_NOT_COMPLIANT.value) == DVSError.KEY_USAGE_NOT_COMPLIANT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Usage de la clé non conforme"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Usage de la clé conforme (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.OID_NON_AUTORISE.value) == DVSError.OID_NON_AUTORISE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "OID de la Politique de Certification non référencée"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OID de la Politique de Certification référencée (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.QCSTATEMENTS_NOT_COMPLIANT.value) == DVSError.QCSTATEMENTS_NOT_COMPLIANT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Extensions QCStatements non conformes"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Extensions QCStatements conformes (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.BUSINESS_CRL_INVALID.value) == DVSError.BUSINESS_CRL_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Validation métier d\'une CRL non valide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Validation métier d\'une CRL valide (ou non paramétrée)"));
        }

        if ((iDVSDetailedStatus & DVSError.ALGORITHME_INTERDIT.value) == DVSError.ALGORITHME_INTERDIT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Algorithme interdit"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Algorithme conforme"));
        }
        if ((iDVSDetailedStatus & DVSError.NA_x00000040.value) == DVSError.NA_x00000040.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        }

        if ((iDVSDetailedStatus & DVSError.AC_HORS_VALIDITE.value) == DVSError.AC_HORS_VALIDITE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC en dehors de sa période de validité"));
        } else if ((iDVSDetailedStatus & DVSError.AC_DONNEES_VALIDATION_MANQUANTE.value) == DVSError.AC_DONNEES_VALIDATION_MANQUANTE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
        } else if ((iDVSDetailedStatus & DVSError.AC_REVOQUEE.value) == DVSError.AC_REVOQUEE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins une AC révoquée"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "La chaîne est valide"));
        }

        if ((iDVSDetailedStatus & DVSError.VALIDATION_OCSP_NA.value) == DVSError.VALIDATION_OCSP_NA.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        } else if ((iDVSDetailedStatus & DVSError.VALIDATION_OCSP_DISPONIBLE.value) == DVSError.VALIDATION_OCSP_DISPONIBLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP dès que disponible"));
        } else if ((iDVSDetailedStatus & DVSError.VALIDATION_OCSP_CERT.value) == DVSError.VALIDATION_OCSP_CERT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP pour le certificat de sinature"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "CRL et ARL pour toute la chaîne"));
        }

        if ((iDVSDetailedStatus & DVSError.CERTIFICAT_AC_NON_AUTORISE.value) == DVSError.CERTIFICAT_AC_NON_AUTORISE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC émettrice non référencée"));
        } else if ((iDVSDetailedStatus & DVSError.CERTIFICAT_DONNEES_VALIDATION_MANQUANTE.value) == DVSError.CERTIFICAT_DONNEES_VALIDATION_MANQUANTE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
        } else if ((iDVSDetailedStatus & DVSError.CERTIFICAT_REVOQUE.value) == DVSError.CERTIFICAT_REVOQUE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat révoqué"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat valide"));
        }

        if ((iDVSDetailedStatus & DVSError.HORODATE_IMPOSSIBLE.value) == DVSError.HORODATE_IMPOSSIBLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Horodatage de réception de la signature impossible"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Horodatage de réception de la signature effectué (ou non demandé)"));
        }

        if ((iDVSDetailedStatus & DVSError.DOC_SEMANTIC_NA.value) == DVSError.DOC_SEMANTIC_NA.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        } else if ((iDVSDetailedStatus & DVSError.DOC_SEMANTIC_IMPOSSIBLE.value) == DVSError.DOC_SEMANTIC_IMPOSSIBLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Vérification de la stabilité sémantique du document impossible"));
        } else if ((iDVSDetailedStatus & DVSError.DOC_SEMANTIC_INSTABLE.value) == DVSError.DOC_SEMANTIC_INSTABLE.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Stabilité sémantique du document : instable"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Stabilité sémantique du document : stable (ou non demandé)"));
        }

        if ((iDVSDetailedStatus & DVSError.SIGNATURE_INCOMPLIANT.value) == DVSError.SIGNATURE_INCOMPLIANT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Propriétés de la signatures non-conformes"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Propriétés de la signatures conformes (ou non spécifiées)"));
        }

        if ((iDVSDetailedStatus & DVSError.SIGNATURE_INVALID.value) == DVSError.SIGNATURE_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Signature cryptographique invalide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Signature cryptographique valide"));
        }

        if ((iDVSDetailedStatus & DVSError.TEMPORAL_WINDOW_OUT.value) == DVSError.TEMPORAL_WINDOW_OUT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "En dehors de la fenêtre temporelle (non implémenté)"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Fenêtre temporelle respectée (non implémenté)"));
        }

        if ((iDVSDetailedStatus & DVSError.HORODATAGE_INVALID.value) == DVSError.HORODATAGE_INVALID.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Jeton d\'horodatage de la signature invalide"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Jeton d\'horodatage de la signature valide"));
        }

        if ((iDVSDetailedStatus & DVSError.REFERENCE_NOT_FOUND.value) == DVSError.REFERENCE_NOT_FOUND.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Référence introuvable lors de la résolution des références"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Succès de la résolution des références"));
        }

        if ((iDVSDetailedStatus & DVSError.ADES_REF_INCOHERENT.value) == DVSError.ADES_REF_INCOHERENT.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Incohérence entre les références (AdES-C) et les valeurs (AdES-L) des données de validation d\'une signature AdES-A (les références ne correspondent pas aux valeurs)"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Cohérence entre les références (AdES-C) et les valeurs (AdES-L) des données de validation d\'une signature AdES-A"));
        }

        if ((iDVSDetailedStatus & DVSError.XADES_ATTRIBUTE_NOT_FOUND.value) == DVSError.XADES_ATTRIBUTE_NOT_FOUND.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Attribut XAdES non supportés touvés"));
        } else {
            maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Succès de la validation des attibuts XAdES"));
        }

        if ((iDVSDetailedStatus & DVSError.NA_x00000020.value) == DVSError.NA_x00000020.value
                || (iDVSDetailedStatus & DVSError.NA_x00000020.value) == DVSError.NA_x00000020.value
                || (iDVSDetailedStatus & DVSError.NA_x00000010.value) == DVSError.NA_x00000010.value
                || (iDVSDetailedStatus & DVSError.NA_x00000008.value) == DVSError.NA_x00000008.value
                || (iDVSDetailedStatus & DVSError.NA_x00000004.value) == DVSError.NA_x00000004.value
                || (iDVSDetailedStatus & DVSError.NA_x00000002.value) == DVSError.NA_x00000002.value
                || (iDVSDetailedStatus & DVSError.NA_x00000001.value) == DVSError.NA_x00000001.value) {
            maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
        }
    }

//    @Override
//    protected void computeListe(BigInteger leCode) {
//        int iDVSDetailedStatus = leCode.intValue();
//
//        if ((iDVSDetailedStatus & 127) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
//        }
//        iDVSDetailedStatus >>= 7;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Incohérence entre les références (AdES-C) et les valeurs (AdES-L) des données de validation d\'une signature AdES-A (les références ne correspondent pas aux valeurs)"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Cohérence entre les références (AdES-C) et les valeurs (AdES-L) des données de validation d\'une signature AdES-A"));
//        }
//        iDVSDetailedStatus >>= 1;
//        
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Référence introuvable lors de la résolution des références"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Succès de la résolution des références"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Jeton d\'horodatage de la signature invalide"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Jeton d\'horodatage de la signature valide"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "En dehors de la fenêtre temporelle (non implémenté)"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Fenêtre temporelle respectée (non implémenté)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Signature cryptographique invalide"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Signature cryptographique valide"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 3) == 3) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
//        } else if ((iDVSDetailedStatus & 3) == 2) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Vérification de la stabilité sémantique du document impossible"));
//        } else if ((iDVSDetailedStatus & 3) == 1) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Stabilité sémantique du document : instable"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Stabilité sémantique du document : stable (ou non demandé)"));
//        }
//        iDVSDetailedStatus >>= 2;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Propriétés de la signatures non-conformes"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Propriétés de la signatures conformes (ou non spécifiées)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Horodatage de réception de la signature impossible"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Horodatage de réception de la signature effectué (ou non demandé)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 3) == 3) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC émettrice non référencée"));
//        } else if ((iDVSDetailedStatus & 3) == 2) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
//        } else if ((iDVSDetailedStatus & 3) == 1) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat révoqué"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat valide"));
//        }
//        iDVSDetailedStatus >>= 2;
//
//         if ((iDVSDetailedStatus & 3) == 3) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
//        } else if ((iDVSDetailedStatus & 3) == 2) {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP dès que disponible"));
//        } else if ((iDVSDetailedStatus & 3) == 1) {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OCSP pour le certificat de sinature"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "CRL et ARL pour toute la chaîne"));
//        }
//        iDVSDetailedStatus >>= 2;
//
//        if ((iDVSDetailedStatus & 3) == 3) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "AC en dehors de sa période de validité"));
//        } else if ((iDVSDetailedStatus & 3) == 2) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Données de validation manquantes"));
//        } else if ((iDVSDetailedStatus & 3) == 1) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins une AC révoquée"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "La chaîne est valide"));
//        }
//        iDVSDetailedStatus >>= 2;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Erreur normalement non attribué"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Algorithme interdit"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Algorithme conforme"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Validation métier d\'une CRL non valide"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Validation métier d\'une CRL valide (ou non paramétrée)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Extensions QCStatements non conformes"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Extensions QCStatements conformes (ou non spécifiées)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "OID de la Politique de Certification non référencée"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "OID de la Politique de Certification référencée (ou non spécifiées)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Usage de la clé non conforme"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Usage de la clé conforme (ou non spécifiées)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le DN du certificat n'est pas trouvé dans la liste blanche/noire"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le DN du certificat est trouvé dans la liste blanche/noire (ou aucune liste de DN n'est configurée)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Le certificat n'est pas trouvé dans la liste blanche/noire"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Le certificat est trouvé dans la liste blanche/noire de certificats (ou aucune liste n'est configurée)"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Certificat de signature en dehors de sa période de validité"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Certificat de signature en cours de validité"));
//        }
//        iDVSDetailedStatus >>= 1;
//
//        if ((iDVSDetailedStatus & 1) != 0) {
//           maListeDeReponses.add(new Reponse(TypeReponse.ERREUR, "Au moins un des contrôles effectué n'a pas retourné le résultat attendu"));
//        } else {
//           maListeDeReponses.add(new Reponse(TypeReponse.SUCCES, "Tous les contrôles effectués sur la signature et le certificat du signataire ont retourné le résultat attendu"));
//        }
//    }
}
