import {afterNextRender, Component, DestroyRef, inject, signal} from '@angular/core';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {DatePipe} from '@angular/common';
import {CaLoader} from '../../services/ca-loader';
import {takeUntilDestroyed, toObservable} from '@angular/core/rxjs-interop';
import {ISliceDto} from '../../models/ISliceDto';
import {ICertInfoDto} from '../../models/ICertInfoDto';
import {State} from '../../services/state';
import {ErrorHandler} from '../../services/error-handler';
import {distinctUntilChanged, skip} from 'rxjs';

@Component({
  selector: 'app-results',
  imports: [MatProgressSpinnerModule, MatTableModule, MatSortModule, MatPaginatorModule, DatePipe],
  templateUrl: './results.html',
  styleUrl: './results.scss'
})
export class Results {

  public caData = signal<Array<ICertInfoDto>>([]);
  public sliceData = signal<ISliceDto>({
    size: 15,
    page: 0,
    hasNext: false,
    hasPrevious: false,
  });
  public displayedColumns: string[] = ['domain', 'issuerDn', 'subjectDn', 'notAfter', 'notBefore'];
  public isLoadingResults = signal<boolean>(true);
  public hasError = signal<boolean>(false);
  protected readonly Infinity = Infinity;
  private readonly caLoader = inject(CaLoader);
  private readonly destroyRef = inject(DestroyRef);
  private readonly stateService = inject(State);
  private readonly errorHandler = inject(ErrorHandler);
  private readonly searchTerm = toObservable(this.stateService.searchTerm);

  public constructor() {
    afterNextRender(() => {
      this.fetch();

      this.searchTerm.pipe(
        skip(1),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      ).subscribe(() => {
        this.clearSliceData();
        this.fetch();
      });
    });
  }

  public onPaginatorClick(event: PageEvent): void {
    if (event.pageIndex > this.sliceData().page) {
      this.onClickNextPage();
    } else if (event.pageIndex < this.sliceData().page) {
      this.onClickPreviousPage();
    }
  }

  private onClickNextPage(): void {
    if (this.sliceData().hasNext) {
      this.sliceData.update((data) => ({
        ...data,
        page: data.page + 1
      }));

      this.fetch();
    }
  }

  private onClickPreviousPage(): void {
    if (this.sliceData().hasPrevious) {
      this.sliceData.update((data) => ({
        ...data,
        page: Math.max(data.page - 1, 0)
      }));

      this.fetch();
    }
  }

  private fetch(): void {
    this.isLoadingResults.set(true);
    this.hasError.set(false);

    const searchTerm = this.stateService.searchTerm();
    const isSearchEmpty = searchTerm === undefined || searchTerm === null || searchTerm.trim() === '';

    const request$ = isSearchEmpty
      ? this.caLoader.getAllData(this.sliceData().page, this.sliceData().size)
      : this.caLoader.search(searchTerm.trim(), this.sliceData().page, this.sliceData().size);

    request$.pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (response) => {
        this.caData.set(response.data);
        this.sliceData.set(response.slicing);
        this.isLoadingResults.set(false);
      },
      error: (error) => {
        this.errorHandler.handleError(error);
        this.hasError.set(true);
        this.isLoadingResults.set(false);
        this.caData.set([]);
      }
    });
  }

  private clearSliceData(): void {
    this.sliceData.set({
      size: 15,
      page: 0,
      hasNext: false,
      hasPrevious: false,
    });
  }
}
