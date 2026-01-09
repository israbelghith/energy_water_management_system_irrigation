import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DebitMesure, DebitMesureDto } from '../../../models/debit-mesure.model';
import { Pompe } from '../../../models/pompe.model';
import { EauService } from '../../../services/eau.service';
import { EnergyService } from '../../../services/energy.service';

@Component({
  selector: 'app-debit-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './debit-list.component.html',
  styleUrls: ['./debit-list.component.css']
})
export class DebitListComponent implements OnInit {
  debits: DebitMesure[] = [];
  pompes: Pompe[] = [];
  showForm: boolean = false;
  loading: boolean = false;
  error: string = '';
  selectedPompeId: number | null = null;
  checkingEnergy: boolean = false;

  debitForm: DebitMesureDto = {
    pompeId: 0,
    debit: 0,
    dateMesure: new Date().toISOString(),
    unite: 'm³/h'
  };

  constructor(
    private eauService: EauService,
    private energyService: EnergyService
  ) { }

  ngOnInit(): void {
    this.loadPompes();
    this.loadDebits();
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

  loadDebits(): void {
    this.loading = true;
    this.error = '';

    if (this.selectedPompeId) {
      this.eauService.getDebitsByPompe(this.selectedPompeId).subscribe({
        next: (data) => {
          this.debits = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des débits';
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      this.eauService.getAllDebits().subscribe({
        next: (data) => {
          this.debits = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des débits';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  filterByPompe(): void {
    this.loadDebits();
  }

  openCreateForm(): void {
    this.showForm = true;
    this.resetForm();
  }

  saveDebit(): void {
    this.loading = true;
    this.error = '';

    this.eauService.createDebit(this.debitForm).subscribe({
      next: () => {
        this.loadDebits();
        this.closeForm();
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'enregistrement du débit';
        console.error(err);
        this.loading = false;
      }
    });
  }

  verifierDisponibiliteElectrique(pompeId: number): void {
    this.checkingEnergy = true;
    this.eauService.verifierDisponibiliteElectrique(pompeId).subscribe({
      next: (disponible) => {
        const pompeName = this.getPompeName(pompeId);
        if (disponible) {
          alert(`✅ Énergie disponible pour ${pompeName}. La pompe peut démarrer.`);
        } else {
          alert(`❌ Énergie non disponible pour ${pompeName}. Impossible de démarrer la pompe.`);
        }
        this.checkingEnergy = false;
      },
      error: (err) => {
        this.error = 'Erreur lors de la vérification de la disponibilité électrique';
        console.error(err);
        this.checkingEnergy = false;
      }
    });
  }

  getDebitMoyen(pompeId: number): void {
    this.eauService.getDebitMoyen(pompeId).subscribe({
      next: (moyen) => {
        const pompeName = this.getPompeName(pompeId);
        alert(`Débit moyen pour ${pompeName}: ${moyen.toFixed(2)} m³/h`);
      },
      error: (err) => {
        this.error = 'Erreur lors du calcul du débit moyen';
        console.error(err);
      }
    });
  }

  getPompeName(pompeId: number): string {
    const pompe = this.pompes.find(p => p.id === pompeId);
    return pompe ? pompe.reference : `Pompe #${pompeId}`;
  }

  getPompeStatus(pompeId: number): string {
    const pompe = this.pompes.find(p => p.id === pompeId);
    return pompe ? pompe.statut : 'UNKNOWN';
  }

  closeForm(): void {
    this.showForm = false;
    this.resetForm();
  }

  resetForm(): void {
    this.debitForm = {
      pompeId: this.pompes.length > 0 ? this.pompes[0].id! : 0,
      debit: 0,
      dateMesure: new Date().toISOString().split('T')[0] + 'T' + new Date().toTimeString().split(' ')[0],
      unite: 'm³/h'
    };
  }
}
