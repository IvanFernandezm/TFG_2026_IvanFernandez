package org.example.tribunalsbackend.Domain;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;

import java.util.ArrayList;
import java.util.List;

//USUARI DOCENT
@Entity
@Getter
@Setter
public class Docent extends AppUser {
    private boolean veteran;

    @ManyToMany
    @JoinTable(
            name = "disponibilitat_docent",
            joinColumns = @JoinColumn(name = "docent_mail", referencedColumnName = "mail"),
            inverseJoinColumns = @JoinColumn(name = "disponibilitat_id", referencedColumnName = "id")
    )
    private List<Disponibilitat> availability; //DISPONIBILITATS DEL DOCENT

    @ManyToMany
    @JoinTable(
            name = "docent_expertesa",
            joinColumns = @JoinColumn(name = "docent_mail", referencedColumnName = "mail"),
            inverseJoinColumns = @JoinColumn(name = "expertesa_id", referencedColumnName = "id")
    )
    private List<Expertesa> experteses; //EXPERTESES DEL DOCENT, NOMÉS POT SER TUTOR I JUTGE DE TREBALLS DELS QUE TINGUI EXPERTESES

    public Docent() {
    }

    public Docent(String email, String name, boolean veteran) {
        super(email, name);
        this.veteran = veteran;
        this.experteses = new ArrayList<>();
    }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
        this.availability.add(disponibilitat);
    }

    public void removeDisponibilitat(Disponibilitat disponibilitat) {
        this.availability.remove(disponibilitat);
    }

    public void addExpertesa(Expertesa expertesa) {
        this.experteses.add(expertesa);
    }

    public void removeExpertesa(Expertesa expertesa) {

        this.experteses.remove(expertesa);

    }
}
