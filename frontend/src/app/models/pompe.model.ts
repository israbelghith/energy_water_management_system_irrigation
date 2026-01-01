export enum StatutPompe {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  EN_MAINTENANCE = 'EN_MAINTENANCE'
}

export interface Pompe {
  id?: number;
  reference: string;
  puissance: number;
  statut: StatutPompe;
  dateMiseEnService: Date | string;
}

export interface PompeDTO {
  id?: number;
  reference: string;
  puissance: number;
  statut: StatutPompe;
  dateMiseEnService: string;
}
