import {inject, Injectable} from '@angular/core';
import {Snackbar} from './snackbar';
import {HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandler {
  private readonly snackBarService = inject(Snackbar);

  public handleError(error: HttpErrorResponse | Error): Observable<never> {

    let errorCode;
    let errorMessage = 'An error occurred';

    if (error instanceof HttpErrorResponse) {
      errorCode = error.status;
      errorMessage = error.message;
      console.error(
        `Backend returned code ${errorCode}, body was ${errorMessage}`,
      );
    } else {
      console.error('An error occurred:', error);
    }

    this.snackBarService.openSnackBar(errorMessage, false);

    return throwError(() => error);
  }
}
