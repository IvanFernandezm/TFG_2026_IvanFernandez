import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Tribunal } from '../../../model/tribunal-model';
import { map, Observable } from 'rxjs';

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


}
