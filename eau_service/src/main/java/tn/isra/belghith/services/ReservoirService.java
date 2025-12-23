package tn.isra.belghith.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.dto.ReservoirDto;
import tn.isra.belghith.entities.Reservoir;
import tn.isra.belghith.repositories.ReservoirRepository;

@Service
@Slf4j
@Transactional
public class ReservoirService {

    @Autowired
    private ReservoirRepository reservoirRepository;

    public ReservoirDto creerReservoir(ReservoirDto dto) {
        log.info("Création d'un nouveau réservoir: {}", dto.getNom());

        Reservoir reservoir = convertToEntity(dto);
        Reservoir saved = reservoirRepository.save(reservoir);

        return convertToDto(saved);
    }

    public ReservoirDto getReservoir(Long id) {
        Reservoir reservoir = reservoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservoir non trouvé avec l'ID: " + id));
        return convertToDto(reservoir);
    }

    public List<ReservoirDto> getAllReservoirs() {
        return reservoirRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ReservoirDto updateVolumeActuel(Long id, Double nouveauVolume) {
        log.info("Mise à jour du volume pour le réservoir ID: {}", id);

        Reservoir reservoir = reservoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservoir non trouvé avec l'ID: " + id));

        if (nouveauVolume > reservoir.getCapaciteTotale()) {
            throw new RuntimeException("Le volume actuel ne peut pas dépasser la capacité totale");
        }

        reservoir.setVolumeActuel(nouveauVolume);
        Reservoir updated = reservoirRepository.save(reservoir);

        // Vérifier si le réservoir est en alerte (moins de 20% de capacité)
        if (nouveauVolume < reservoir.getCapaciteTotale() * 0.2) {
            log.warn("ALERTE: Le réservoir {} est en dessous de 20% de sa capacité", reservoir.getNom());
        }

        return convertToDto(updated);
    }

    public List<ReservoirDto> getReservoirsEnAlerte() {
        log.info("Récupération des réservoirs en alerte");
        return reservoirRepository.findReservoirsEnAlerte().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ReservoirDto> getReservoirsByLocalisation(String localisation) {
        return reservoirRepository.findByLocalisation(localisation).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteReservoir(Long id) {
        log.info("Suppression du réservoir ID: {}", id);
        reservoirRepository.deleteById(id);
    }

    private ReservoirDto convertToDto(Reservoir reservoir) {
        ReservoirDto dto = new ReservoirDto();
        dto.setId(reservoir.getId());
        dto.setNom(reservoir.getNom());
        dto.setCapaciteTotale(reservoir.getCapaciteTotale());
        dto.setVolumeActuel(reservoir.getVolumeActuel());
        dto.setLocalisation(reservoir.getLocalisation());
        return dto;
    }

    private Reservoir convertToEntity(ReservoirDto dto) {
        Reservoir reservoir = new Reservoir();
        reservoir.setId(dto.getId());
        reservoir.setNom(dto.getNom());
        reservoir.setCapaciteTotale(dto.getCapaciteTotale());
        reservoir.setVolumeActuel(dto.getVolumeActuel());
        reservoir.setLocalisation(dto.getLocalisation());
        return reservoir;
    }
}
