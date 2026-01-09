export interface Alert {
  id: string;
  type: 'ENERGY' | 'WATER' | 'SYSTEM';
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  message: string;
  timestamp: Date | string;
  pompeId?: number;
  resolved: boolean;
}

export interface SurconsommationEvent {
  pompeId: number;
  energieUtilisee: number;
  seuilDepasse: number;
  timestamp: string;
}
