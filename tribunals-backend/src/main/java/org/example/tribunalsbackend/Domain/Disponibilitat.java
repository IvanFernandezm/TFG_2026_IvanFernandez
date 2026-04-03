package org.example.tribunalsbackend.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Disponibilitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @ManyToMany(mappedBy = "availability")
    private List<Docent> docents = new ArrayList<>();

    public Disponibilitat() {
    }

    public Disponibilitat(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void addDocent(Docent docent) {
        if (this.docents == null) this.docents = new ArrayList<>();
        if (!this.docents.contains(docent)) {
            this.docents.add(docent);
            docent.getAvailability().add(this);
        }
    }

    public void removeDocent(Docent docent) {
        if (this.docents != null && this.docents.contains(docent)) {
            this.docents.remove(docent);
            docent.getAvailability().remove(this);
        }
    }
}
