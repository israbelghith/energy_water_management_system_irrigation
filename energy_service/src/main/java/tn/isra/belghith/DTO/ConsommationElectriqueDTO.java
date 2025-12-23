package tn.isra.belghith.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import tn.isra.belghith.entities.Pompe;

@Data
public class ConsommationElectriqueDTO {
    public ConsommationElectriqueDTO(Long id2, Pompe pompe, Double energieUtilisee2, Double duree2, Date dateMesure2) {
		// TODO Auto-generated constructor stub
	}
	private Long id;
    private Pompe pompe;
    private Double energieUtilisee;
    private Double duree;
    private Date dateMesure;
  //  private boolean surconsommation;
}
