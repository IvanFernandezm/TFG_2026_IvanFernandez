package org.example.Data.Domain;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.example.Data.Domain.Abstracts.AppUser;

import java.util.ArrayList;
import java.util.List;

//USUARI DOCENT
@Entity
@Getter
@Setter
public class Docent extends AppUser {
    private boolean veteran; //DETERMINA LA VETERANIA, DOS  NO VETERANS  NO PODEN ESTAR AL MATEIX TRIBUNAL.

    @ManyToMany
    @JoinTable(
            name = "disponibilitat_docent",
            joinColumns = @JoinColumn(name = "docent_mail", referencedColumnName = "mail"),
            inverseJoinColumns = @JoinColumn(name = "disponibilitat_id", referencedColumnName = "id")
    )
    private List<Disponibilitat> availability = new ArrayList<>(); //DISPONIBILITATS DEL DOCENT

    @ManyToMany
    @JoinTable(
            name = "docent_expertesa",
            joinColumns = @JoinColumn(name = "docent_mail", referencedColumnName = "mail"),
            inverseJoinColumns = @JoinColumn(name = "expertesa_id", referencedColumnName = "id")
    )
    private List<Expertesa> experteses = new ArrayList<>(); //EXPERTESES DEL DOCENT, NOMÉS POT SER TUTOR I JUTGE DE TREBALLS DELS QUE TINGUI EXPERTESES

    public Docent() {
    }

    public Docent(String email, String name) {
        super(email, name);
        this.veteran = true; //PER DEFECTE, TOTS ELS DOCENTS SÓN VETERANS, L'ADMINISTRADOR POT CANVIAR AQUESTA PROPIETAT
    }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
        if (!this.availability.contains(disponibilitat)) {
            this.availability.add(disponibilitat);
            if (!disponibilitat.getDocents().contains(this)) {
                disponibilitat.getDocents().add(this);
            }
        }
    }

    public void removeDisponibilitat(Disponibilitat disponibilitat) {
        if (this.availability.contains(disponibilitat)) {
            this.availability.remove(disponibilitat);
            disponibilitat.getDocents().remove(this);
        }
    }

    public void addExpertesa(Expertesa expertesa) {
        this.experteses.add(expertesa);
    }

    public boolean isVeteran() {
        return veteran;
    }

    public void removeExpertesa(Expertesa expertesa) {
        this.experteses.remove(expertesa);
    }

    public void setVeteran() {
        this.veteran = !this.veteran;
    }
}
