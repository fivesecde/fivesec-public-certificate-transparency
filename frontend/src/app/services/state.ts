import {Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class State {
  public searchTerm = signal<string | undefined | null>(undefined);

}
