package tn.isra.belghith.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.isra.belghith.DTO.PompeDTO;
import tn.isra.belghith.entities.Pompe;
import tn.isra.belghith.entities.StatutPompe;
import tn.isra.belghith.repositories.PompeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PompeService {
    
    @Autowired
    private PompeRepository pompeRepository;
    
    public PompeDTO createPompe(Pompe pompe) {
        log.info("Création nouvelle pompe avec référence: {}", pompe.getReference());
        
        // Vérification métier: référence unique
        if (pompeRepository.findByReference(pompe.getReference()) != null) {
            throw new RuntimeException("Une pompe avec cette référence existe déjà: " + pompe.getReference());
        }
        
        // Validation métier: puissance > 0
        if (pompe.getPuissance() == null || pompe.getPuissance() <= 0) {
            throw new RuntimeException("La puissance doit être supérieure à 0");
        }
        
        Pompe savedPompe = pompeRepository.save(pompe);
        log.info("Pompe créée avec succès - ID: {}", savedPompe.getId());
        return convertToDTO(savedPompe);
    }

    public PompeDTO createPompeFromDTO(PompeDTO pompeDTO) {
        log.info("Création nouvelle pompe avec référence: {}", pompeDTO.getReference());
        
        // Vérification métier: référence unique
        if (pompeRepository.findByReference(pompeDTO.getReference()) != null) {
            throw new RuntimeException("Une pompe avec cette référence existe déjà: " + pompeDTO.getReference());
        }
        
        // Validation métier: puissance > 0
        if (pompeDTO.getPuissance() == null || pompeDTO.getPuissance() <= 0) {
            throw new RuntimeException("La puissance doit être supérieure à 0");
        }
        
        Pompe pompe = new Pompe();
        pompe.setReference(pompeDTO.getReference());
        pompe.setPuissance(pompeDTO.getPuissance());
        pompe.setStatut(pompeDTO.getStatut());
        pompe.setDateMiseEnService(pompeDTO.getDateMiseEnService());
        
        Pompe savedPompe = pompeRepository.save(pompe);
        log.info("Pompe créée avec succès - ID: {}", savedPompe.getId());
        return convertToDTO(savedPompe);
    }
    
    public PompeDTO updatePompe(Long id, Pompe pompe) {
        log.info("Mise à jour pompe id: {}", id);
        
        Pompe pompeExist = pompeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pompe non trouvée avec id: " + id));
        
        // Validation métier: vérifier que la référence n'est pas déjà utilisée par une autre pompe
        Pompe pompeAvecMemeRef = pompeRepository.findByReference(pompe.getReference());
        if (pompeAvecMemeRef != null && !pompeAvecMemeRef.getId().equals(id)) {
            throw new RuntimeException("Cette référence est déjà utilisée par une autre pompe");
        }
        
        // Validation métier: puissance
        if (pompe.getPuissance() == null || pompe.getPuissance() <= 0) {
            throw new RuntimeException("La puissance doit être supérieure à 0");
        }
        
        pompeExist.setReference(pompe.getReference());
        pompeExist.setPuissance(pompe.getPuissance());
        pompeExist.setStatut(pompe.getStatut());
        pompeExist.setDateMiseEnService(pompe.getDateMiseEnService());
        
        Pompe updatedPompe = pompeRepository.save(pompeExist);
        log.info("Pompe mise à jour avec succès - ID: {}", updatedPompe.getId());
        return convertToDTO(updatedPompe);
    }

    public PompeDTO updatePompeFromDTO(Long id, PompeDTO pompeDTO) {
        log.info("Mise à jour pompe id: {}", id);
        
        Pompe pompeExist = pompeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pompe non trouvée avec id: " + id));
        
        // Validation métier: vérifier que la référence n'est pas déjà utilisée par une autre pompe
        Pompe pompeAvecMemeRef = pompeRepository.findByReference(pompeDTO.getReference());
        if (pompeAvecMemeRef != null && !pompeAvecMemeRef.getId().equals(id)) {
            throw new RuntimeException("Cette référence est déjà utilisée par une autre pompe");
        }
        
        // Validation métier: puissance
        if (pompeDTO.getPuissance() == null || pompeDTO.getPuissance() <= 0) {
            throw new RuntimeException("La puissance doit être supérieure à 0");
        }
        
        pompeExist.setReference(pompeDTO.getReference());
        pompeExist.setPuissance(pompeDTO.getPuissance());
        pompeExist.setStatut(pompeDTO.getStatut());
        pompeExist.setDateMiseEnService(pompeDTO.getDateMiseEnService());
        
        Pompe updatedPompe = pompeRepository.save(pompeExist);
        log.info("Pompe mise à jour avec succès - ID: {}", updatedPompe.getId());
        return convertToDTO(updatedPompe);
    }
    
    public PompeDTO getPompeById(Long id) {
        log.debug("Récupération pompe id: {}", id);
        Pompe pompe = pompeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pompe non trouvée avec id: " + id));
        return convertToDTO(pompe);
    }
    
    public PompeDTO getPompeByReference(String reference) {
        log.debug("Récupération pompe référence: {}", reference);
        Pompe pompe = pompeRepository.findByReference(reference);
        if (pompe == null) {
            throw new RuntimeException("Pompe non trouvée avec référence: " + reference);
        }
        return convertToDTO(pompe);
    }
    
    public List<PompeDTO> getAllPompes() {
        log.debug("Récupération de toutes les pompes");
        return pompeRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<PompeDTO> getPompesByStatut(StatutPompe statut) {
        log.debug("Récupération pompes avec statut: {}", statut);
        return pompeRepository.findByStatut(statut).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public void deletePompe(Long id) {
        log.info("Suppression pompe id: {}", id);
        
        // Vérifier que la pompe existe
        if (!pompeRepository.existsById(id)) {
            throw new RuntimeException("Pompe non trouvée avec id: " + id);
        }
        
        // Règle métier: on pourrait vérifier s'il y a des consommations liées
        // avant de supprimer (à implémenter si besoin)
        
        pompeRepository.deleteById(id);
        log.info("Pompe supprimée avec succès - ID: {}", id);
    }
    
    /**
     * LOGIQUE MÉTIER - Communication synchrone avec service Eau
     * Vérifie si la pompe est disponible pour démarrage
     * Règles métier:
     * - La pompe doit exister
     * - Le statut doit être ACTIVE
     * - On pourrait ajouter: vérifier la puissance disponible, pas de maintenance prévue, etc.
     */
    public boolean verifierDisponibiliteElectrique(Long pompeId) {
        log.info("Vérification disponibilité électrique pour pompe id: {}", pompeId);
        
        Pompe pompe = pompeRepository.findById(pompeId)
            .orElseThrow(() -> new RuntimeException("Pompe non trouvée avec id: " + pompeId));
        
        // Règle métier principale: statut ACTIVE
        boolean disponible = pompe.getStatut() == StatutPompe.ACTIVE;
        
        // Règles métier supplémentaires (à enrichir selon les besoins):
        // - Vérifier que la puissance est suffisante
        // - Vérifier qu'il n'y a pas de maintenance programmée
        // - Vérifier le niveau de charge du réseau électrique
        // - etc.
        
        if (disponible) {
            log.info("Pompe {} disponible pour démarrage", pompe.getReference());
        } else {
            log.warn("Pompe {} NON disponible - Statut: {}", pompe.getReference(), pompe.getStatut());
        }
        
        return disponible;
    }
    
    public PompeDTO changerStatut(Long id, StatutPompe nouveauStatut) {
        log.info("Changement statut pompe id: {} vers {}", id, nouveauStatut);
        
        Pompe pompe = pompeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pompe non trouvée avec id: " + id));
        
        pompe.setStatut(nouveauStatut);
        Pompe updatedPompe = pompeRepository.save(pompe);
        
        log.info("Statut changé avec succès pour pompe {}: {}",pompe.getReference(), nouveauStatut);
        
        return convertToDTO(updatedPompe);
    }
    
    // Conversion DTO
    
    private PompeDTO convertToDTO(Pompe pompe) {
        return new PompeDTO(
            pompe.getId(),
            pompe.getReference(),
            pompe.getPuissance(),
            pompe.getStatut(),
            pompe.getDateMiseEnService()
        );
    }
    
    private Pompe convertToEntity(PompeDTO dto) {
        Pompe pompe = new Pompe();
        pompe.setId(dto.getId());
        pompe.setReference(dto.getReference());
        pompe.setPuissance(dto.getPuissance());
        pompe.setStatut(dto.getStatut());
        pompe.setDateMiseEnService(dto.getDateMiseEnService());
        return pompe;
    }
}