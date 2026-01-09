import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Pompe } from '../../models/pompe.model';
import { Reservoir } from '../../models/reservoir.model';
import { ConsommationElectrique } from '../../models/consommation-electrique.model';
import { Alert } from '../../models/alert.model';
import { EnergyService } from '../../services/energy.service';
import { EauService } from '../../services/eau.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Data
  pompes: Pompe[] = [];
  reservoirs: Reservoir[] = [];
  consommations: ConsommationElectrique[] = [];
  alerts: Alert[] = [];

  // Stats
  totalPompes: number = 0;
  pompesActives: number = 0;
  totalReservoirs: number = 0;
  reservoirsCritiques: number = 0;
  consommationTotale: number = 0;

  // Loading states
  loading: boolean = true;
  refreshInterval: any;

  // Services
  private readonly energyService = inject(EnergyService);
  private readonly eauService = inject(EauService);

  // Expose Date for template
  get currentDate(): Date {
    return new Date();
  }

  ngOnInit(): void {
    this.loadDashboardData();
    // Auto-refresh every 30 seconds
    this.refreshInterval = setInterval(() => {
      this.loadDashboardData();
    }, 30000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadDashboardData(): void {
    this.loading = true;

    // Load all data in parallel
    Promise.all([
      this.loadPompes(),
      this.loadReservoirs(),
      this.loadConsommations()
    ]).then(() => {
      this.calculateStats();
      this.checkAlerts();
      this.loading = false;
    }).catch(err => {
      console.error('Erreur lors du chargement du dashboard', err);
      this.loading = false;
    });
  }

  loadPompes(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.energyService.getAllPompes().subscribe({
        next: (data: Pompe[]) => {
          this.pompes = data;
          resolve();
        },
        error: (err: any) => {
          console.error('Erreur lors du chargement des pompes', err);
          reject(err);
        }
      });
    });
  }

  loadReservoirs(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.eauService.getAllReservoirs().subscribe({
        next: (data: Reservoir[]) => {
          this.reservoirs = data;
          resolve();
        },
        error: (err: any) => {
          console.error('Erreur lors du chargement des réservoirs', err);
          reject(err);
        }
      });
    });
  }

  loadConsommations(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.energyService.getAllConsommations().subscribe({
        next: (data: ConsommationElectrique[]) => {
          this.consommations = data;
          resolve();
        },
        error: (err: any) => {
          console.error('Erreur lors du chargement des consommations', err);
          reject(err);
        }
      });
    });
  }

  calculateStats(): void {
    // Pompes stats
    this.totalPompes = this.pompes.length;
    this.pompesActives = this.pompes.filter(p => p.statut === 'ACTIVE').length;

    // Reservoirs stats
    this.totalReservoirs = this.reservoirs.length;
    this.reservoirsCritiques = this.reservoirs.filter(r =>
      (r.volumeActuel / r.capaciteTotale) * 100 < 20
    ).length;

    // Energy stats
    this.consommationTotale = this.consommations.reduce((sum, c) =>
      sum + c.energieUtilisee, 0
    );
  }

  checkAlerts(): void {
    this.alerts = [];

    // Check for overconsumption
    const seuilSurconsommation = 100;
    this.consommations.forEach(c => {
      if (c.energieUtilisee > seuilSurconsommation) {
        const pompe = this.pompes.find(p => p.id === c.pompeId);
        this.alerts.push({
          id: `energy-${c.id}`,
          type: 'ENERGY',
          severity: 'WARNING',
          message: `Surconsommation détectée sur ${pompe?.reference || 'Pompe #' + c.pompeId}: ${c.energieUtilisee} kWh`,
          timestamp: c.dateMesure,
          pompeId: c.pompeId,
          resolved: false
        });
      }
    });

    // Check for critical reservoirs
    this.reservoirs.forEach(r => {
      const percentage = (r.volumeActuel / r.capaciteTotale) * 100;
      if (percentage < 20) {
        this.alerts.push({
          id: `water-${r.id}`,
          type: 'WATER',
          severity: percentage < 10 ? 'CRITICAL' : 'WARNING',
          message: `Niveau critique du réservoir ${r.nom}: ${percentage.toFixed(1)}%`,
          timestamp: new Date().toISOString(),
          resolved: false
        });
      }
    });

    // Check for inactive pumps with recent consumption
    const recentDate = new Date();
    recentDate.setHours(recentDate.getHours() - 24);

    this.pompes.filter(p => p.statut === 'INACTIVE').forEach(p => {
      const recentConsumption = this.consommations.some(c =>
        c.pompeId === p.id && new Date(c.dateMesure) > recentDate
      );

      if (recentConsumption) {
        this.alerts.push({
          id: `system-${p.id}`,
          type: 'SYSTEM',
          severity: 'INFO',
          message: `Pompe ${p.reference} inactive mais avec consommation récente`,
          timestamp: new Date().toISOString(),
          pompeId: p.id,
          resolved: false
        });
      }
    });

    // Sort alerts by severity
    this.alerts.sort((a, b) => {
      const severityOrder: { [key: string]: number } = { 'CRITICAL': 0, 'WARNING': 1, 'INFO': 2 };
      return severityOrder[a.severity] - severityOrder[b.severity];
    });
  }

  getRemplissage(reservoir: Reservoir): number {
    return (reservoir.volumeActuel / reservoir.capaciteTotale) * 100;
  }

  getSeverityClass(severity: string): string {
    return `alert-${severity.toLowerCase()}`;
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'ENERGY': return '⚡';
      case 'WATER': return '💧';
      case 'SYSTEM': return '⚙️';
      default: return '📌';
    }
  }

  refreshDashboard(): void {
    this.loadDashboardData();
  }
}
