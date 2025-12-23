package tn.isra.belghith.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.DTO.ConsommationElectriqueDTO;
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

        // Vérifier que la pompe existe
        Pompe pompe = pompeRepository.findById(consommationElec.getPompe().getId())
                .orElseThrow(
                        () -> new RuntimeException("Pompe not found with id: " + consommationElec.getPompe().getId()));

        ConsommationElectrique savedConsommation = consommationRepository.save(consommationElec);

        // Vérifier la surconsommation
        verifierSurconsommation(savedConsommation, pompe);

        return convertToDTO(savedConsommation);
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
        return new ConsommationElectriqueDTO(
                consommation.getId(),
                consommation.getPompe(),
                consommation.getEnergieUtilisee(),
                consommation.getDuree(),
                consommation.getDateMesure());
    }

    private ConsommationElectrique convertToEntity(ConsommationElectriqueDTO dto) {
        ConsommationElectrique consommation = new ConsommationElectrique();
        consommation.setId(dto.getId());
        consommation.setPompe(dto.getPompe());
        consommation.setEnergieUtilisee(dto.getEnergieUtilisee());
        consommation.setDuree(dto.getDuree());
        consommation.setDateMesure(dto.getDateMesure());
        return consommation;
    }
}
