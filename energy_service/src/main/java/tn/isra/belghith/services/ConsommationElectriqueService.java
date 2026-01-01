package tn.isra.belghith.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.DTO.ConsommationElectriqueDTO;
import tn.isra.belghith.DTO.PompeDTO;
import tn.isra.belghith.entities.ConsommationElectrique;
import tn.isra.belghith.entities.Pompe;
import tn.isra.belghith.events.SurconsommationEvent;
import tn.isra.belghith.repositories.ConsommationElectriqueRepository;
import tn.isra.belghith.repositories.PompeRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ConsommationElectriqueService {
    @Autowired
    private ConsommationElectriqueRepository consommationRepository;

    @Autowired
    private PompeRepository pompeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.seuil.surconsommation:100.0}")
    private Double seuilSurconsommation;

    public ConsommationElectriqueDTO enregistrerConsommation(ConsommationElectrique consommationElec) {
        log.info("Enregistrement consommation pour pompe: {}", consommationElec.getPompe().getId());

        // Vérifier que la pompe existe et la récupérer complètement
        Pompe pompe = pompeRepository.findById(consommationElec.getPompe().getId())
                .orElseThrow(
                        () -> new RuntimeException("Pompe not found with id: " + consommationElec.getPompe().getId()));

        // Attacher la pompe complète à la consommation
        consommationElec.setPompe(pompe);

        ConsommationElectrique savedConsommation = consommationRepository.save(consommationElec);

        // Vérifier la surconsommation
        verifierSurconsommation(savedConsommation, pompe);

        return convertToDTO(savedConsommation);
    }

    public List<ConsommationElectriqueDTO> getAllConsommations() {
        log.info("Récupération de toutes les consommations");
        return consommationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ConsommationElectriqueDTO> getConsommationsByPompe(Long pompeId) {
        return consommationRepository.findByPompeId(pompeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ConsommationElectriqueDTO> getConsommationsByPeriod(
            Long pompeId, Date debut, Date fin) {
        return consommationRepository
                .findByPompeIdAndDateMesureBetween(pompeId, debut, fin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double getConsommationTotale(Long pompeId, Date debut, Date fin) {
        Double total = consommationRepository.sumEnergieByPompeIdAndPeriod(pompeId, debut, fin);
        return total != null ? total : 0.0;
    }

    public Double getConsommationTotaleGlobale() {
        log.info("Calcul de la consommation totale globale");
        List<ConsommationElectrique> allConsommations = consommationRepository.findAll();
        return allConsommations.stream()
                .mapToDouble(ConsommationElectrique::getEnergieUtilisee)
                .sum();
    }

    public ConsommationElectriqueDTO enregistrerConsommationFromDTO(ConsommationElectriqueDTO dto) {
        // Récupérer le pompeId de manière sécurisée
        Long pompeId = null;
        if (dto.getPompe() != null && dto.getPompe().getId() != null) {
            pompeId = dto.getPompe().getId();
        } else if (dto.getPompeId() != null) {
            pompeId = dto.getPompeId();
        }

        log.info("Enregistrement consommation depuis DTO pour pompe: {}", pompeId);

        // Convertir DTO en entité
        ConsommationElectrique consommation = convertToEntity(dto);

        // Enregistrer
        return enregistrerConsommation(consommation);
    }

    public List<ConsommationElectriqueDTO> getSurconsommations(Double seuil) {
        log.info("Recherche des surconsommations au-dessus de {} kWh", seuil);
        return consommationRepository.findAll().stream()
                .filter(c -> c.getEnergieUtilisee() > seuil)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void verifierSurconsommation(ConsommationElectrique consommation, Pompe pompe) {
        if (consommation.getEnergieUtilisee() > seuilSurconsommation) {
            log.warn("Surconsommation détectée pour pompe: {}", pompe.getReference());

            SurconsommationEvent event = new SurconsommationEvent(
                    pompe.getId(),
                    pompe.getReference(),
                    consommation.getEnergieUtilisee(),
                    seuilSurconsommation,
                    new Date(),
                    String.format("Surconsommation détectée: %.2f kWh (seuil: %.2f kWh)",
                            consommation.getEnergieUtilisee(), seuilSurconsommation));
            rabbitTemplate.convertAndSend("irrigation.exchange", "irrigation.surconsommation", event);
            // irrigation.eau.queue
            // kafkaTemplate.send("surconsommation-topic", event);
        }
    }

    private ConsommationElectriqueDTO convertToDTO(ConsommationElectrique consommation) {
        PompeDTO pompeDTO = null;
        if (consommation.getPompe() != null) {
            Pompe pompe = consommation.getPompe();
            pompeDTO = new PompeDTO(
                    pompe.getId(),
                    pompe.getReference(),
                    pompe.getPuissance(),
                    pompe.getStatut(),
                    pompe.getDateMiseEnService());
        }

        return new ConsommationElectriqueDTO(
                consommation.getId(),
                pompeDTO,
                null, // pompeId - not needed when we have the full pompe object
                consommation.getEnergieUtilisee(),
                consommation.getDuree(),
                consommation.getDateMesure());
    }

    private ConsommationElectrique convertToEntity(ConsommationElectriqueDTO dto) {
        ConsommationElectrique consommation = new ConsommationElectrique();
        consommation.setId(dto.getId());

        // Récupérer la pompe depuis la base de données
        final Long pompeId;
        if (dto.getPompe() != null && dto.getPompe().getId() != null) {
            pompeId = dto.getPompe().getId();
        } else if (dto.getPompeId() != null) {
            pompeId = dto.getPompeId();
        } else {
            pompeId = null;
        }

        if (pompeId != null) {
            Pompe pompe = pompeRepository.findById(pompeId)
                    .orElseThrow(() -> new RuntimeException("Pompe not found with id: " + pompeId));
            consommation.setPompe(pompe);
        }

        consommation.setEnergieUtilisee(dto.getEnergieUtilisee());
        consommation.setDuree(dto.getDuree());
        consommation.setDateMesure(dto.getDateMesure());
        return consommation;
    }
}
