import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservoir, ReservoirDto } from '../models/reservoir.model';
import { DebitMesure, DebitMesureDto } from '../models/debit-mesure.model';

@Injectable({
  providedIn: 'root'
})
export class EauService {
  private apiUrl = 'http://localhost:30888/eau';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Reservoir endpoints
  getAllReservoirs(): Observable<Reservoir[]> {
    return this.http.get<Reservoir[]>(`${this.apiUrl}/api/reservoirs`);
  }

  getReservoirById(id: number): Observable<Reservoir> {
    return this.http.get<Reservoir>(`${this.apiUrl}/api/reservoirs/${id}`);
  }

  createReservoir(reservoir: ReservoirDto): Observable<Reservoir> {
    return this.http.post<Reservoir>(`${this.apiUrl}/api/reservoirs`, reservoir, this.httpOptions);
  }

  updateReservoir(id: number, reservoir: ReservoirDto): Observable<Reservoir> {
    return this.http.put<Reservoir>(`${this.apiUrl}/api/reservoirs/${id}`, reservoir, this.httpOptions);
  }

  deleteReservoir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/reservoirs/${id}`);
  }

  getRemplissage(id: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/api/reservoirs/${id}/remplissage`);
  }

  getReservoirsCritiques(seuil: number): Observable<Reservoir[]> {
    return this.http.get<Reservoir[]>(`${this.apiUrl}/api/reservoirs/alertes`);
  }

  // Debit Mesure endpoints
  getAllDebits(): Observable<DebitMesure[]> {
    return this.http.get<DebitMesure[]>(`${this.apiUrl}/api/debits`);
  }

  getDebitById(id: number): Observable<DebitMesure> {
    return this.http.get<DebitMesure>(`${this.apiUrl}/api/debits/${id}`);
  }

  createDebit(debit: DebitMesureDto): Observable<DebitMesure> {
    return this.http.post<DebitMesure>(`${this.apiUrl}/api/debits`, debit, this.httpOptions);
  }

  getDebitsByPompe(pompeId: number): Observable<DebitMesure[]> {
    return this.http.get<DebitMesure[]>(`${this.apiUrl}/api/debits/pompe/${pompeId}`);
  }

  getDebitMoyen(pompeId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/api/debits/pompe/${pompeId}/moyen`);
  }

  // Communication synchrone
  verifierDisponibiliteElectrique(pompeId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/api/debits/verifier-energie/${pompeId}`);
  }
}
