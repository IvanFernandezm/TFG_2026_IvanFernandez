import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralInfo } from '../../../model/genInfo-model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GeneralService {
  private apiUrl = '/api/general'
  constructor(private http: HttpClient) { }

  getGeneralInfo(): Observable<GeneralInfo> {
    return this.http.get<GeneralInfo>(this.apiUrl+'/info');
  }

}
