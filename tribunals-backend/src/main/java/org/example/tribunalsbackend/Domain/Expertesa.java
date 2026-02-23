package org.example.tribunalsbackend.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Expertesa {
    private String id;
    private String description;

    public Expertesa(String id, String description) {
        this.id = id;
        this.description = description;
    }
}
