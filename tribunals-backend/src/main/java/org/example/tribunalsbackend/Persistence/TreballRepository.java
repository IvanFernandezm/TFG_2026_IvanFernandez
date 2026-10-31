package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Estudiant;
import org.example.tribunalsbackend.Domain.Treball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreballRepository extends JpaRepository<Treball, Long> {
    Optional<Treball>findTreballByStudent(Estudiant student);

    Optional<Treball> getTreballByTitle(String title);

    List<Treball> findAllByApte(boolean apte);

    @Query("""
            SELECT t
            FROM Treball t
            WHERE t.apte = true
              AND NOT EXISTS (
                  SELECT tribunal
                  FROM Tribunal tribunal
                  WHERE tribunal.treball = t
              )
            """)
    List<Treball> findAptesWithoutTribunal();
}
