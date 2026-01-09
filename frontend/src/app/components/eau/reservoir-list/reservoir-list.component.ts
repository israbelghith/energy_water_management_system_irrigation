import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Reservoir, ReservoirDto } from '../../../models/reservoir.model';
import { EauService } from '../../../services/eau.service';

@Component({
  selector: 'app-reservoir-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservoir-list.component.html',
  styleUrls: ['./reservoir-list.component.css']
})
export class ReservoirListComponent implements OnInit {
  reservoirs: Reservoir[] = [];
  selectedReservoir: Reservoir | null = null;
  showForm: boolean = false;
  isEditing: boolean = false;
  loading: boolean = false;
  error: string = '';
  seuilCritique: number = 20;

  reservoirForm: ReservoirDto = {
    nom: '',
    capaciteTotale: 0,
    volumeActuel: 0,
    localisation: ''
  };

  constructor(private eauService: EauService) { }

  ngOnInit(): void {
    this.loadReservoirs();
  }

  loadReservoirs(): void {
    this.loading = true;
    this.error = '';
    this.eauService.getAllReservoirs().subscribe({
      next: (data) => {
        this.reservoirs = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des réservoirs';
        // Log removed
        this.loading = false;
      }
    });
  }

  openCreateForm(): void {
    this.showForm = true;
    this.isEditing = false;
    this.resetForm();
  }

  openEditForm(reservoir: Reservoir): void {
    this.showForm = true;
    this.isEditing = true;
    this.reservoirForm = {
      nom: reservoir.nom,
      capaciteTotale: reservoir.capaciteTotale,
      volumeActuel: reservoir.volumeActuel,
      localisation: reservoir.localisation
    };
    this.selectedReservoir = reservoir;
  }

  saveReservoir(): void {
    this.loading = true;
    this.error = '';

    if (this.isEditing && this.selectedReservoir?.id) {
      this.eauService.updateReservoir(this.selectedReservoir.id, this.reservoirForm).subscribe({
        next: () => {
          this.loadReservoirs();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour du réservoir';
        // Log removed
          this.loading = false;
        }
      });
    } else {
      this.eauService.createReservoir(this.reservoirForm).subscribe({
        next: () => {
          this.loadReservoirs();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la création du réservoir';
        // Log removed
          this.loading = false;
        }
      });
    }
  }

  deleteReservoir(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce réservoir ?')) {
      this.loading = true;
      this.eauService.deleteReservoir(id).subscribe({
        next: () => {
          this.loadReservoirs();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression du réservoir';
        // Log removed
          this.loading = false;
        }
      });
    }
  }

  checkReservoirsCritiques(): void {
    this.eauService.getReservoirsCritiques(this.seuilCritique).subscribe({
      next: (data) => {
        if (data.length > 0) {
          alert(`⚠️ ${data.length} réservoir(s) critique(s) détecté(s) en dessous de ${this.seuilCritique}%`);
          this.reservoirs = data;
        } else {
          alert('✅ Aucun réservoir critique détecté');
        }
      },
      error: (err) => {
        this.error = 'Erreur lors de la vérification des réservoirs critiques';
        // Log removed
      }
    });
  }

  getRemplissage(reservoir: Reservoir): number {
    return (reservoir.volumeActuel / reservoir.capaciteTotale) * 100;
  }

  getRemplissageClass(reservoir: Reservoir): string {
    const percentage = this.getRemplissage(reservoir);
    if (percentage < 20) return 'critical';
    if (percentage < 50) return 'warning';
    return 'good';
  }

  closeForm(): void {
    this.showForm = false;
    this.resetForm();
    this.selectedReservoir = null;
  }

  resetForm(): void {
    this.reservoirForm = {
      nom: '',
      capaciteTotale: 0,
      volumeActuel: 0,
      localisation: ''
    };
  }
}
