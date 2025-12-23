package tn.isra.belghith.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.clients.EnergyServiceClient;
import tn.isra.belghith.dto.DebitMesureDto;
import tn.isra.belghith.entities.DebitMesure;
import tn.isra.belghith.events.SurconsommationEvent;
import tn.isra.belghith.repositories.DebitMesureRepository;

@Service
@Slf4j
@Transactional
public class DebitMesureService {

    @Autowired
    private DebitMesureRepository debitMesureRepository;

    @Autowired
    private EnergyServiceClient energyServiceClient;

    public DebitMesureDto enregistrerDebitMesure(DebitMesureDto dto) {
        log.info("Enregistrement d'une mesure de débit pour la pompe: {}", dto.getPompeId());

        // Communication synchrone: vérifier la disponibilité électrique avant démarrage
        try {
            Boolean disponibiliteElectrique = energyServiceClient.verifierDisponibiliteElectrique(dto.getPompeId());

            if (disponibiliteElectrique == null || !disponibiliteElectrique) {
                log.warn("Disponibilité électrique insuffisante pour la pompe ID: {}", dto.getPompeId());
                throw new RuntimeException("La pompe ne peut pas démarrer: disponibilité électrique insuffisante");
            }

            log.info("Disponibilité électrique confirmée pour la pompe ID: {}", dto.getPompeId());
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de la disponibilité électrique: {}", e.getMessage());
            throw new RuntimeException("Erreur de communication avec le service énergie: " + e.getMessage());
        }

        DebitMesure debitMesure = convertToEntity(dto);
        DebitMesure saved = debitMesureRepository.save(debitMesure);

        return convertToDto(saved);
    }

    public DebitMesureDto getDebitMesure(Long id) {
        DebitMesure debitMesure = debitMesureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesure de débit non trouvée avec l'ID: " + id));
        return convertToDto(debitMesure);
    }

    public List<DebitMesureDto> getDebitMesuresByPompe(Long pompeId) {
        return debitMesureRepository.findByPompeId(pompeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DebitMesureDto> getDebitMesuresByPeriod(Long pompeId, Date dateDebut, Date dateFin) {
        if (pompeId != null) {
            return debitMesureRepository.findByPompeIdAndDateMesureBetween(pompeId, dateDebut, dateFin).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return debitMesureRepository.findByDateMesureBetween(dateDebut, dateFin).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public Double getDebitMoyen(Long pompeId) {
        Double moyenneDebit = debitMesureRepository.findAverageDebitByPompeId(pompeId);
        return moyenneDebit != null ? moyenneDebit : 0.0;
    }

    public Double getDebitTotal(Long pompeId, Date dateDebut, Date dateFin) {
        Double totalDebit = debitMesureRepository.sumDebitByPompeIdAndPeriod(pompeId, dateDebut, dateFin);
        return totalDebit != null ? totalDebit : 0.0;
    }

    // Communication asynchrone: écouter les événements de surconsommation
    @RabbitListener(queues = "irrigation.eau.queue")
    public void handleSurconsommationEvent(SurconsommationEvent event) {
        log.warn("=== ÉVÉNEMENT DE SURCONSOMMATION REÇU ===");
        log.warn("Pompe ID: {}", event.getPompeId());
        log.warn("Référence: {}", event.getPompeReference());
        log.warn("Énergie utilisée: {} kWh", event.getEnergieUtilisee());
        log.warn("Seuil dépassé: {} kWh", event.getSeuilDepasse());
        log.warn("Message: {}", event.getMessage());
        log.warn("Date de détection: {}", event.getDateDetection());

        // Logique métier: actions à effectuer lors d'une surconsommation
        // Par exemple: arrêter la pompe, alerter un opérateur, ajuster le débit, etc.
        try {
            List<DebitMesure> mesures = debitMesureRepository.findByPompeId(event.getPompeId());
            if (!mesures.isEmpty()) {
                log.info("Nombre de mesures de débit pour cette pompe: {}", mesures.size());
                // Ici on pourrait implémenter une logique pour réduire le débit
                // ou arrêter temporairement la pompe
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement de surconsommation: {}", e.getMessage());
        }
    }

    public void deleteDebitMesure(Long id) {
        log.info("Suppression de la mesure de débit ID: {}", id);
        debitMesureRepository.deleteById(id);
    }

    private DebitMesureDto convertToDto(DebitMesure debitMesure) {
        DebitMesureDto dto = new DebitMesureDto();
        dto.setId(debitMesure.getId());
        dto.setPompeId(debitMesure.getPompeId());
        dto.setDebit(debitMesure.getDebit());
        dto.setUnite(debitMesure.getUnite());
        dto.setDateMesure(debitMesure.getDateMesure());
        return dto;
    }

    private DebitMesure convertToEntity(DebitMesureDto dto) {
        DebitMesure debitMesure = new DebitMesure();
        debitMesure.setId(dto.getId());
        debitMesure.setPompeId(dto.getPompeId());
        debitMesure.setDebit(dto.getDebit());
        debitMesure.setUnite(dto.getUnite());
        debitMesure.setDateMesure(dto.getDateMesure());
        return debitMesure;
    }
}
