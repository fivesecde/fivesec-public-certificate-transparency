import {inject, Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class Snackbar {
  private readonly snackBar = inject(MatSnackBar);

  public openSnackBar(
    message: string,
    success: boolean,
    duration: number = 10000,
  ): void {
    this.snackBar.open(message, 'X', {
      duration,
      panelClass: ['snackbar', `snackbar-${success ? 'success' : 'error'}`],
    });
  }
}
