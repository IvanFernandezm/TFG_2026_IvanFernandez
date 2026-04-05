package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//ENTITAT QUE REPRESENTA EL TRIBUNAL QUE JUTJARÀ UN TREBALL DE FI DE GRAU
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

    @ManyToOne
    @JoinColumn(name = "adjudicacio_id", referencedColumnName = "id")
    private Adjudicacio adjudicacio;

    @OneToOne
    @JoinColumn(name = "treball_id", referencedColumnName = "id")
    private Treball treball;

    public Tribunal() {
    }

    public Tribunal(String id, String room, Docent presidency, Docent vocal, Treball treball) {
        this.id = id;
        this.room = room;
        this.presidencia = presidency;
        this.vocal = vocal;
        this.treball = treball;
    }

}
