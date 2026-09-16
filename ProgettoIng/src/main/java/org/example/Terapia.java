package org.example;

//classe che descrive una terapia
public class Terapia {
    private int assunzioniGG;
    private String indicazioni;
    private int quantita;
    private Farmaco farmaco;
    private Diabetologo medicoPrescrittore;

    //cotruttore di default della classe
    public Terapia() {
    }

    //cotruttore con parametri della classe
    public Terapia(int assunzioniGG, String indicazioni, int quantita, Farmaco farmaco, Diabetologo medicoPrescrittore) {
        this.assunzioniGG = assunzioniGG;
        this.indicazioni = indicazioni;
        this.quantita = quantita;
        this.farmaco = farmaco;
        this.medicoPrescrittore = medicoPrescrittore;
    }

    //getter del campo della classe omonimo
    public int getAssunzioniGG() {
        return assunzioniGG;
    }

    //setter del campo della classe omonimo
    public void setAssunzioniGG(int assunzioniGG) {
        this.assunzioniGG = assunzioniGG;
    }

    //getter del campo della classe omonimo
    public String getIndicazioni() {
        return indicazioni;
    }

    //setter del campo della classe omonimo
    public void setIndicazioni(String indicazioni) {
        this.indicazioni = indicazioni;
    }

    //getter del campo della classe omonimo
    public int getQuantita() {
        return quantita;
    }

    //setter del campo della classe omonimo
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    //getter del campo della classe omonimo
    public Farmaco getFarmaco() {
        return farmaco;
    }

    //setter del campo della classe omonimo
    public void setFarmaco(Farmaco farmaco) {
        this.farmaco = farmaco;
    }

    //getter del campo della classe omonimo
    public Diabetologo getMedicoPrescrittore() {
        return medicoPrescrittore;
    }

    //setter del campo della classe omonimo
    public void setMedicoPrescrittore(Diabetologo medicoPrescrittore) {
        this.medicoPrescrittore = medicoPrescrittore;
    }

    //metodo per modificare tramite setter i dati della terapia
    public Boolean modificaDatiTerapia(int assunzioniGG, String indicazioni, int quantita, Farmaco farmaco, Diabetologo medicoPrescrittore){
        this.setAssunzioniGG(assunzioniGG);
        this.setFarmaco(farmaco);
        this.setIndicazioni(indicazioni);
        this.setMedicoPrescrittore(medicoPrescrittore);
        this.setQuantita(quantita);
        return true;
    }

    //override del metodo toString adattato alla classe
    @Override
    public String toString() {
        String nomeFarmaco = (farmaco != null) ? farmaco.getNomeFarmaco() : "Non specificato";
        String prescrittore = (medicoPrescrittore != null)
                ? medicoPrescrittore.getNome() + " " + medicoPrescrittore.getCognome()
                : "Non specificato";

        return "Farmaco: " + nomeFarmaco +
                " | Quantità: " + quantita +
                " | Frequenza: " + assunzioniGG + " volte/die" +
                " | Indicazioni: " + (indicazioni != null ? indicazioni : "Nessuna") +
                " | Prescrittore: Dott. " + prescrittore;
    }

}

