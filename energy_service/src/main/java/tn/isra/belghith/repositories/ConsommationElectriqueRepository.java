package tn.isra.belghith.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import tn.isra.belghith.entities.ConsommationElectrique;

@Repository
public interface ConsommationElectriqueRepository extends JpaRepository<ConsommationElectrique, Long> {
    List<ConsommationElectrique> findByPompeId(Long pompeId);
    
    List<ConsommationElectrique> findByPompeIdAndDateMesureBetween(
        Long pompeId, Date debut, Date fin);
    
    @Query("SELECT SUM(c.energieUtilisee) FROM ConsommationElectrique c WHERE c.pompe.id = :pompeId AND c.dateMesure BETWEEN :debut AND :fin")
    Double sumEnergieByPompeIdAndPeriod(
        @Param("pompeId") Long pompeId, 
        @Param("debut") Date debut, 
        @Param("fin") Date fin);
}