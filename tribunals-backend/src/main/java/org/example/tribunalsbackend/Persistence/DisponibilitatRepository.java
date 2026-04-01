package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Disponibilitat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilitatRepository extends JpaRepository<Disponibilitat, Long> {
}
