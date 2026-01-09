export interface Reservoir {
  id?: number;
  nom: string;
  capaciteTotale: number;
  volumeActuel: number;
  localisation: string;
}

export interface ReservoirDto {
  id?: number;
  nom: string;
  capaciteTotale: number;
  volumeActuel: number;
  localisation: string;
}
