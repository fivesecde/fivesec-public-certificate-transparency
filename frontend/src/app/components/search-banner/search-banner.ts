import {AfterViewInit, Component, DestroyRef, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {State} from '../../services/state';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-search-banner',
  imports: [
    MatInputModule,
    MatButton,
    ReactiveFormsModule,
    MatIconModule
  ],
  templateUrl: './search-banner.html',
  styleUrl: './search-banner.scss'
})
export class SearchBanner implements AfterViewInit {
  public searchControl = new FormControl('');
  private readonly stateService = inject(State);
  private readonly destroyRef = inject(DestroyRef);

  public ngAfterViewInit(): void {
    this.searchControl.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef),
      debounceTime(500),
      distinctUntilChanged(),
    ).subscribe((value) => {
      const trimmedValue = value?.trim() || '';

      if (trimmedValue.length === 0) {
        this.stateService.searchTerm.set(undefined);
      } else if (trimmedValue.length > 1) {
        this.stateService.searchTerm.set(trimmedValue);
      }
    });
  }

  public onClickSearch(): void {
    const value = this.searchControl.value;
    if (value && value.trim().length > 0) {
      this.stateService.searchTerm.set(value.trim());
    }
  }
}
