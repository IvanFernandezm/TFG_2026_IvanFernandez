import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Tribunal } from '../../../model/tribunal-model';
import { map, Observable } from 'rxjs';
import { Expertesa } from '../../../model/expertesa-model';
import { Disponibilitat } from '../../../model/disponibilitat-model';

@Injectable({
  providedIn: 'root',
})
export class TribunalService {
  private apiUrl = '/api/tribunal'
  constructor(private http: HttpClient) { }

  getTribunals(): Observable<Tribunal[]> {
    return this.http.get<Tribunal[]>(`${this.apiUrl}/all`).pipe(
      map(data =>
        data.map(item => ({
          ...item,
          data: new Date(item.data)
        }))
      )
    );
  }

  importExcel(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import`, formData, { responseType: 'text' });
  }

  organitzarTribunals(classrooms: number): Observable<Tribunal[]> {
    return this.http.get<Tribunal[]>(`${this.apiUrl}/organitzar`, { params: { maxClassrooms: classrooms.toString() } }).pipe(
      map(data =>
        data.map(item => ({
          ...item,
          data: new Date(item.data)
        }))
      )
    );
  }

  getExperteses(): Observable<Expertesa[]> {
    return this.http.get<Expertesa[]>(`${this.apiUrl}/experteses`);
  }

  getDisponibilitats(): Observable<Disponibilitat[]> {
    return this.http.get<Disponibilitat[]>(`${this.apiUrl}/disponibilitats`).pipe(
      map(data =>
        data.map(item => ({
          ...item,
          timestamp: new Date(item.timestamp)
        }))
      )
    );
  }
}