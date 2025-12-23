package tn.isra.belghith.DTO;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import tn.isra.belghith.entities.StatutPompe;

@Data
@AllArgsConstructor
public class PompeDTO {
	
	private Long id;
    private String reference;
    private Double puissance;
    private StatutPompe statut;
    private Date dateMiseEnService;
}
