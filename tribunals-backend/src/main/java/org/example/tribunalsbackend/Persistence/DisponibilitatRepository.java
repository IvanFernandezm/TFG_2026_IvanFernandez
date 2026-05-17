package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Disponibilitat;
import org.example.tribunalsbackend.Domain.Docent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilitatRepository extends JpaRepository<Disponibilitat, Long> {
}
