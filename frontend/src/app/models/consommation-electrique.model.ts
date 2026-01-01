export interface ConsommationElectrique {
  id?: number;
  pompeId: number;
  energieUtilisee: number;
  duree: number;
  dateMesure: Date | string;
}

export interface ConsommationElectriqueDTO {
  id?: number;
  pompeId: number;
  energieUtilisee: number;
  duree: number;
  dateMesure: string;
}
