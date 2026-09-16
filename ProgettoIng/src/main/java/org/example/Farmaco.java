package org.example;

//classe che descrive un farmaco
public class Farmaco {
    private String nomeFarmaco;
    private String principioAttivo;

    //costruttore di default di un farmaco
    public Farmaco(){}

    //costruttore con parametri della classe
    public Farmaco(String nomeFarmaco, String principioAttivo){
        this.nomeFarmaco = nomeFarmaco;
        this.principioAttivo = principioAttivo;
    }

    //getter del campo della classe omonimo
    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    //setter del campo della classe omonimo
    public void setNomeFarmaco(String nomeFarmaco) {
        this.nomeFarmaco = nomeFarmaco;
    }

    //getter del campo della classe omonimo
    public String getPrincipioAttivo() {
        return principioAttivo;
    }

    //setter del campo della classe omonimo
    public void setPrincipioAttivo(String principioAttivo) {
        this.principioAttivo = principioAttivo;
    }

    //override di equals per verificare se due farmaci confrontati corrispondono in base al nome commerciale
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Farmaco farmaco = (Farmaco) o;
        if (this.nomeFarmaco == null) {
            return farmaco.nomeFarmaco == null;
        }
        return this.nomeFarmaco.equalsIgnoreCase(farmaco.nomeFarmaco);
    }

    //override del metodo per generare l'hash dell'oggetto farmaco
    @Override
    public int hashCode() {
        return nomeFarmaco != null ? nomeFarmaco.toLowerCase().hashCode() : 0;
    }
}
