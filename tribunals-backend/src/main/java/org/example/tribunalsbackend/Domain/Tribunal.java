package org.example.tribunalsbackend.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Tribunal {
    private String id;
    private String room;
    private Disponibilitat availability;
    private Docent presidency;
    private Docent vocal;

        public Tribunal(String id, String room, Disponibilitat availability, Docent presidency, Docent vocal) {
            this.id = id;
            this.room = room;
            this.availability = availability;
            this.presidency = presidency;
            this.vocal = vocal;
        }
}
