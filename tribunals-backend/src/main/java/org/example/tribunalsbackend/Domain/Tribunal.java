package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Tribunal {
    @Id
    @Column(name = "id")
    private String id;
    private String room;

    @ManyToOne
    @JoinColumn(name = "presidencia_mail", referencedColumnName = "mail")
    private Docent presidencia;

    @ManyToOne
    @JoinColumn(name = "vocal_mail", referencedColumnName = "mail")
    private Docent vocal;

    @OneToMany(mappedBy = "tribunal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adjudicacio> adjudicacions = new ArrayList<>();

    public Tribunal() {
    }

    public Tribunal(String id, String room, Docent presidency, Docent vocal) {
        this.id = id;
        this.room = room;
        this.presidencia = presidency;
        this.vocal = vocal;
    }

    public void addAdjudicacio(Adjudicacio a) {
        if (a == null) return;
        adjudicacions.add(a);
        a.setTribunal(this);
    }

    public void removeAdjudicacio(Adjudicacio a) {
        if (a == null) return;
        adjudicacions.remove(a);
        a.setTribunal(null);
    }
}
