package tn.isra.belghith.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.isra.belghith.entities.DebitMesure;

@Repository
public interface DebitMesureRepository extends JpaRepository<DebitMesure, Long> {

    List<DebitMesure> findByPompeId(Long pompeId);

    List<DebitMesure> findByDateMesureBetween(Date dateDebut, Date dateFin);

    List<DebitMesure> findByPompeIdAndDateMesureBetween(Long pompeId, Date dateDebut, Date dateFin);

    @Query("SELECT AVG(d.debit) FROM DebitMesure d WHERE d.pompeId = :pompeId")
    Double findAverageDebitByPompeId(@Param("pompeId") Long pompeId);

    @Query("SELECT SUM(d.debit) FROM DebitMesure d WHERE d.pompeId = :pompeId AND d.dateMesure BETWEEN :dateDebut AND :dateFin")
    Double sumDebitByPompeIdAndPeriod(@Param("pompeId") Long pompeId, @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin);
}
