package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Docent;
import org.example.tribunalsbackend.Domain.Treball;
import org.example.tribunalsbackend.Domain.Tribunal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TribunalRepository extends JpaRepository<Tribunal, Long> {
    Tribunal findTribunalByTreball_Title(String treballTitle);

    List<Tribunal> findTribunalsByPresidencia_MailOrVocal_Mail(String presidenciaMail, String vocalMail);

    @Query("""
             SELECT t FROM Tribunal t
                WHERE (t.presidencia.mail = :mail OR t.vocal.mail = :mail)
                  AND t.adjudicacio = :adjudicacio
            """)
    List<Tribunal> findByDocentAndAdjudicacio(
            @Param("mail") String mail,
            @Param("adjudicacio") LocalDateTime adjudicacio);

    List<Tribunal> findTribunalsByAdjudicacio(LocalDateTime adjudicacio);

    void deleteTribunalByTreball(Treball treball);
}
