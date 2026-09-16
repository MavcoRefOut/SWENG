package org.example;

public class Farmaco {
    private String nomeFarmaco;
    private String principioAttivo;

    public Farmaco(){}

    public Farmaco(String nomeFarmaco, String principioAttivo){
        this.nomeFarmaco = nomeFarmaco;
        this.principioAttivo = principioAttivo;
    }

    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    public void setNomeFarmaco(String nomeFarmaco) {
        this.nomeFarmaco = nomeFarmaco;
    }

    public String getPrincipioAttivo() {
        return principioAttivo;
    }

    public void setPrincipioAttivo(String principioAttivo) {
        this.principioAttivo = principioAttivo;
    }

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

    @Override
    public int hashCode() {
        return nomeFarmaco != null ? nomeFarmaco.toLowerCase().hashCode() : 0;
    }
}
