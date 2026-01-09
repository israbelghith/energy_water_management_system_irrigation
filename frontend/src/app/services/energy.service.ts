import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pompe, PompeDTO } from '../models/pompe.model';
import { ConsommationElectrique, ConsommationElectriqueDTO } from '../models/consommation-electrique.model';

@Injectable({
  providedIn: 'root'
})
export class EnergyService {
  private apiUrl = 'http://localhost:30888/energy';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Pompe endpoints
  getAllPompes(): Observable<Pompe[]> {
    return this.http.get<Pompe[]>(`${this.apiUrl}/api/pompes`);
  }

  getPompeById(id: number): Observable<Pompe> {
    return this.http.get<Pompe>(`${this.apiUrl}/api/pompes/${id}`);
  }

  createPompe(pompe: PompeDTO): Observable<Pompe> {
    return this.http.post<Pompe>(`${this.apiUrl}/api/pompes`, pompe, this.httpOptions);
  }

  updatePompe(id: number, pompe: PompeDTO): Observable<Pompe> {
    return this.http.put<Pompe>(`${this.apiUrl}/api/pompes/${id}`, pompe, this.httpOptions);
  }

  deletePompe(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/pompes/${id}`);
  }

  activerPompe(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/api/pompes/${id}/activer`, {}, this.httpOptions);
  }

  desactiverPompe(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/api/pompes/${id}/desactiver`, {}, this.httpOptions);
  }

  // Consommation Electrique endpoints
  getAllConsommations(): Observable<ConsommationElectrique[]> {
    return this.http.get<ConsommationElectrique[]>(`${this.apiUrl}/api/consommations`);
  }

  getConsommationById(id: number): Observable<ConsommationElectrique> {
    return this.http.get<ConsommationElectrique>(`${this.apiUrl}/api/consommations/${id}`);
  }

  createConsommation(consommation: ConsommationElectriqueDTO): Observable<ConsommationElectrique> {
    return this.http.post<ConsommationElectrique>(`${this.apiUrl}/api/consommations`, consommation, this.httpOptions);
  }

  getConsommationsByPompe(pompeId: number): Observable<ConsommationElectrique[]> {
    return this.http.get<ConsommationElectrique[]>(`${this.apiUrl}/api/consommations/pompe/${pompeId}`);
  }

  getConsommationsTotales(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/api/consommations/total`);
  }

  getSurconsommations(seuil: number): Observable<ConsommationElectrique[]> {
    return this.http.get<ConsommationElectrique[]>(`${this.apiUrl}/api/consommations/surconsommation/${seuil}`);
  }
}
