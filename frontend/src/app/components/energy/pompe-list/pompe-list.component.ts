import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pompe, PompeDTO, StatutPompe } from '../../../models/pompe.model';
import { EnergyService } from '../../../services/energy.service';

@Component({
  selector: 'app-pompe-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pompe-list.component.html',
  styleUrls: ['./pompe-list.component.css']
})
export class PompeListComponent implements OnInit {
  pompes: Pompe[] = [];
  selectedPompe: Pompe | null = null;
  showForm: boolean = false;
  isEditing: boolean = false;
  loading: boolean = false;
  error: string = '';

  pompeForm: PompeDTO = {
    reference: '',
    puissance: 0,
    statut: StatutPompe.INACTIVE,
    dateMiseEnService: new Date().toISOString().split('T')[0]
  };

  statutOptions = Object.values(StatutPompe);

  constructor(private energyService: EnergyService) { }

  ngOnInit(): void {
    this.loadPompes();
  }

  loadPompes(): void {
    this.loading = true;
    this.error = '';
    this.energyService.getAllPompes().subscribe({
      next: (data) => {
        this.pompes = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des pompes';
        console.error(err);
        this.loading = false;
      }
    });
  }

  openCreateForm(): void {
    this.showForm = true;
    this.isEditing = false;
    this.resetForm();
  }

  openEditForm(pompe: Pompe): void {
    this.showForm = true;
    this.isEditing = true;
    this.pompeForm = {
      reference: pompe.reference,
      puissance: pompe.puissance,
      statut: pompe.statut,
      dateMiseEnService: typeof pompe.dateMiseEnService === 'string'
        ? pompe.dateMiseEnService.split('T')[0]
        : new Date(pompe.dateMiseEnService).toISOString().split('T')[0]
    };
    this.selectedPompe = pompe;
  }

  savePompe(): void {
    this.loading = true;
    this.error = '';

    if (this.isEditing && this.selectedPompe?.id) {
      this.energyService.updatePompe(this.selectedPompe.id, this.pompeForm).subscribe({
        next: () => {
          this.loadPompes();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour de la pompe';
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      this.energyService.createPompe(this.pompeForm).subscribe({
        next: () => {
          this.loadPompes();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la création de la pompe';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  deletePompe(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette pompe ?')) {
      this.loading = true;
      this.energyService.deletePompe(id).subscribe({
        next: () => {
          this.loadPompes();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression de la pompe';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  activerPompe(id: number): void {
    this.energyService.activerPompe(id).subscribe({
      next: () => {
        this.loadPompes();
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'activation de la pompe';
        console.error(err);
      }
    });
  }

  desactiverPompe(id: number): void {
    this.energyService.desactiverPompe(id).subscribe({
      next: () => {
        this.loadPompes();
      },
      error: (err) => {
        this.error = 'Erreur lors de la désactivation de la pompe';
        console.error(err);
      }
    });
  }

  closeForm(): void {
    this.showForm = false;
    this.resetForm();
    this.selectedPompe = null;
  }

  resetForm(): void {
    this.pompeForm = {
      reference: '',
      puissance: 0,
      statut: StatutPompe.INACTIVE,
      dateMiseEnService: new Date().toISOString().split('T')[0]
    };
  }
}
