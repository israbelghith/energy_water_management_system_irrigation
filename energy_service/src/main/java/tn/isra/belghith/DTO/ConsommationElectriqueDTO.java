package tn.isra.belghith.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsommationElectriqueDTO {
  private Long id;
  private PompeDTO pompe;
  private Double energieUtilisee;
  private Double duree;
  private Date dateMesure;
}
