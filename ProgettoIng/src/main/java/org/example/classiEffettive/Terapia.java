package org.example.classiEffettive;

public class Terapia {
    private int assunzioniGG;
    private String indicazioni;
    private int quantita;
    private Farmaco farmaco;
    private Diabetologo medicoPrescrittore;


    public Terapia() {
    }

    public Terapia(int assunzioniGG, String indicazioni, int quantita, Farmaco farmaco, Diabetologo medicoPrescrittore) {
        this.assunzioniGG = assunzioniGG;
        this.indicazioni = indicazioni;
        this.quantita = quantita;
        this.farmaco = farmaco;
        this.medicoPrescrittore = medicoPrescrittore;
    }

    public int getAssunzioniGG() {
        return assunzioniGG;
    }

    public void setAssunzioniGG(int assunzioniGG) {
        this.assunzioniGG = assunzioniGG;
    }

    public String getIndicazioni() {
        return indicazioni;
    }

    public void setIndicazioni(String indicazioni) {
        this.indicazioni = indicazioni;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public Farmaco getFarmaco() {
        return farmaco;
    }

    public void setFarmaco(Farmaco farmaco) {
        this.farmaco = farmaco;
    }

    public Diabetologo getMedicoPrescrittore() {
        return medicoPrescrittore;
    }

    public void setMedicoPrescrittore(Diabetologo medicoPrescrittore) {
        this.medicoPrescrittore = medicoPrescrittore;
    }

    public Boolean modificaDatiTerapia(int assunzioniGG, String indicazioni, int quantita, Farmaco farmaco, Diabetologo medicoPrescrittore){
        this.setAssunzioniGG(assunzioniGG);
        this.setFarmaco(farmaco);
        this.setIndicazioni(indicazioni);
        this.setMedicoPrescrittore(medicoPrescrittore);
        this.setQuantita(quantita);
        return true;
    }

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

