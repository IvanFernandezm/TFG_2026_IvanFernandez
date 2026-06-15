import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Docent } from '../../../model/docent-model';

@Injectable({
  providedIn: 'root',
})
export class DocentService {
    private apiUrl = '/api/docent'
  constructor(private http: HttpClient) { }

  getDocents():Observable<Docent[]> {
    return this.http.get<Docent[]>(this.apiUrl+'/all');
  }
}
