package org.kiwi.dictao.responses;

import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;

public abstract class ListeReponses<TYPE> {

    protected ArrayList<Reponse> maListeDeReponses;
    protected TYPE leCode;

    protected ListeReponses(TYPE leCode) {
        this.leCode = leCode;
        maListeDeReponses = new ArrayList<Reponse>();
        computeListe(leCode);
    }

    protected abstract void computeListe(TYPE leCode);

    public ArrayList<Reponse> getMessagesList() {
        return maListeDeReponses;
    }

    public ArrayList<Reponse> getMessageListOf(Reponse.TypeReponse leType) {

        ArrayList<Reponse> maListeDeReponsesFiltrees = new ArrayList<Reponse>();

        for (Reponse maReponse : maListeDeReponses) {
            if (maReponse.getType() == leType) {
                maListeDeReponsesFiltrees.add(maReponse);
            }
        }

        return maListeDeReponsesFiltrees;
    }

    @Override
    public String toString() {
        StringBuilder monBuffer = new StringBuilder();

        Integer nombre;// = new Integer(0);
        if (leCode.getClass().getSimpleName().equals("BigInteger")) {
            nombre = ((BigInteger) leCode).intValue();
        } else if (leCode.getClass().getSimpleName().equals("Long")) {
            nombre = ((Long) leCode).intValue();
        } else if (leCode.getClass().getSimpleName().equals("PKIFailureInfo")) {
            nombre = ((PKIFailureInfo) leCode).intValue();
        } else {
            nombre = (Integer) leCode;
        }

        monBuffer.append(this.getClass().getSimpleName()).append(" : ").append(leCode).append(" (").append(Integer.toHexString(nombre)).append("[16], ").append(Integer.toBinaryString(nombre)).append("[2])\n");
        for (Reponse maReponse : maListeDeReponses) {
            monBuffer.append(" [").append(maReponse.getType()).append("] ").append(maReponse.getReponse()).append("\n");
        }

        return monBuffer.toString();

    }
}
