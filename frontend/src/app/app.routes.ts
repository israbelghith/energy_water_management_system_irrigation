import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { PompeListComponent } from './components/energy/pompe-list/pompe-list.component';
import { ConsommationListComponent } from './components/energy/consommation-list/consommation-list.component';
import { ReservoirListComponent } from './components/eau/reservoir-list/reservoir-list.component';
import { DebitListComponent } from './components/eau/debit-list/debit-list.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'pompes', component: PompeListComponent },
  { path: 'consommations', component: ConsommationListComponent },
  { path: 'reservoirs', component: ReservoirListComponent },
  { path: 'debits', component: DebitListComponent },
  { path: '**', redirectTo: '/dashboard' }
];
