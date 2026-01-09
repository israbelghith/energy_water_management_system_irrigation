import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConsommationElectrique, ConsommationElectriqueDTO } from '../../../models/consommation-electrique.model';
import { Pompe } from '../../../models/pompe.model';
import { EnergyService } from '../../../services/energy.service';

@Component({
  selector: 'app-consommation-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consommation-list.component.html',
  styleUrls: ['./consommation-list.component.css']
})
export class ConsommationListComponent implements OnInit {
  consommations: ConsommationElectrique[] = [];
  pompes: Pompe[] = [];
  showForm: boolean = false;
  loading: boolean = false;
  error: string = '';
  selectedPompeId: number | null = null;
  totalConsommation: number = 0;
  seuilSurconsommation: number = 100;

  consommationForm: ConsommationElectriqueDTO = {
    pompeId: 0,
    energieUtilisee: 0,
    duree: 0,
    dateMesure: new Date().toISOString()
  };

  constructor(private energyService: EnergyService) { }

  ngOnInit(): void {
    this.loadPompes();
    this.loadConsommations();
    this.loadTotalConsommation();
  }

  loadPompes(): void {
    this.energyService.getAllPompes().subscribe({
      next: (data) => {
        this.pompes = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des pompes', err);
      }
    });
  }

  loadConsommations(): void {
    this.loading = true;
    this.error = '';

    if (this.selectedPompeId) {
      this.energyService.getConsommationsByPompe(this.selectedPompeId).subscribe({
        next: (data) => {
          this.consommations = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des consommations';
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      this.energyService.getAllConsommations().subscribe({
        next: (data) => {
          this.consommations = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des consommations';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  loadTotalConsommation(): void {
    this.energyService.getConsommationsTotales().subscribe({
      next: (total) => {
        this.totalConsommation = total;
      },
      error: (err) => {
        console.error('Erreur lors du chargement de la consommation totale', err);
      }
    });
  }

  filterByPompe(): void {
    this.loadConsommations();
  }

  openCreateForm(): void {
    this.showForm = true;
    this.resetForm();
  }

  saveConsommation(): void {
    this.loading = true;
    this.error = '';

    this.energyService.createConsommation(this.consommationForm).subscribe({
      next: () => {
        this.loadConsommations();
        this.loadTotalConsommation();
        this.closeForm();
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'enregistrement de la consommation';
        console.error(err);
        this.loading = false;
      }
    });
  }

  checkSurconsommations(): void {
    this.energyService.getSurconsommations(this.seuilSurconsommation).subscribe({
      next: (data) => {
        if (data.length > 0) {
          alert(`${data.length} surconsommation(s) détectée(s) au-dessus du seuil de ${this.seuilSurconsommation} kWh`);
          this.consommations = data;
        } else {
          alert('Aucune surconsommation détectée');
        }
      },
      error: (err) => {
        this.error = 'Erreur lors de la vérification des surconsommations';
        console.error(err);
      }
    });
  }

  getPompeName(pompeId: number): string {
    const pompe = this.pompes.find(p => p.id === pompeId);
    return pompe ? pompe.reference : `Pompe #${pompeId}`;
  }

  closeForm(): void {
    this.showForm = false;
    this.resetForm();
  }

  resetForm(): void {
    this.consommationForm = {
      pompeId: this.pompes.length > 0 ? this.pompes[0].id! : 0,
      energieUtilisee: 0,
      duree: 0,
      dateMesure: new Date().toISOString().split('T')[0] + 'T' + new Date().toTimeString().split(' ')[0]
    };
  }
}
