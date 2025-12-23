package tn.isra.belghith.dto;

import lombok.Data;

@Data
public class ReservoirDto {

private Long id;
  private String nom;
  private Double capaciteTotale;
  private Double volumeActuel;
  private String localisation;

}
