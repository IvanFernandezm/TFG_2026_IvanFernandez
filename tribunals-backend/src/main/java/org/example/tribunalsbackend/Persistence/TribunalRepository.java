package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Docent;
import org.example.tribunalsbackend.Domain.Treball;
import org.example.tribunalsbackend.Domain.Tribunal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TribunalRepository extends JpaRepository<Tribunal, Long> {
    Tribunal findTribunalByTreball_Title(String treballTitle);

    List<Tribunal> findTribunalsByPresidencia_MailOrVocal_Mail(String presidenciaMail, String vocalMail);
}
