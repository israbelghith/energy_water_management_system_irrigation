export interface DebitMesure {
  id?: number;
  pompeId: number;
  debit: number;
  dateMesure: Date | string;
  unite: string;
}

export interface DebitMesureDto {
  id?: number;
  pompeId: number;
  debit: number;
  dateMesure: string;
  unite: string;
}
