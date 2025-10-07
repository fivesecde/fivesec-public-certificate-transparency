import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CaLoader {
  private readonly httpClient = inject(HttpClient);

  private readonly baseUrl = '/api/v1/ca/events';

  public getAllData(page: number = 0, size: number = 15): Observable<any> {
    return this.httpClient.get(
      `${this.baseUrl}?page=${page}&size=${size}`
    );
  }

  public search(domain: string, page: number, size: number): Observable<any> {
    return this.httpClient.get(
      `${this.baseUrl}?domain=${domain}&page=${page}&size=${size}`
    );
  }
}
