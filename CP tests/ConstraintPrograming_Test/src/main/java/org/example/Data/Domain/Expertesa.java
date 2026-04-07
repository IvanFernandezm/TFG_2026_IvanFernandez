package org.example.Data.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//ENTITAT QUE REPRESENTA L'EXPERTESA QUE PODEN TENIR ELS TREBALLS I ELS DOCENTS
@Entity
@Getter
@Setter
public class Expertesa {
    @Id
    private String id;
    private String description;

    public Expertesa() {
    }

    public Expertesa(String id, String description) {
        this.id = id;
        this.description = description;
    }
}
