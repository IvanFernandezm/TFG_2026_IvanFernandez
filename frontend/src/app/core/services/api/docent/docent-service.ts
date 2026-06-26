import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Docent } from '../../../model/docent-model';
import { DocentDetails } from '../../../model/docent-details';

@Injectable({
  providedIn: 'root',
})
export class DocentService {
  private apiUrl = '/api/docent'
  constructor(private http: HttpClient) { }

  getDocents(): Observable<Docent[]> {
    return this.http.get<Docent[]>(this.apiUrl + '/all');
  }

  getDocentByEmail(email: string): Observable<DocentDetails> {
    return this.http.get<DocentDetails>(this.apiUrl + `/details?mail=${email}`).pipe(
      map(data => ({
        ...data,
        disponibilitat: (data.disponibilitat ?? []).map(date => new Date(date))
      }))
    );
  }
}