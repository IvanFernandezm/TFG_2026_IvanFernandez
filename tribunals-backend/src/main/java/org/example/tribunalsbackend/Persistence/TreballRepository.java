package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Estudiant;
import org.example.tribunalsbackend.Domain.Treball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreballRepository extends JpaRepository<Treball, Long> {
    Optional<Treball>findTreballByStudent(Estudiant student);
}
