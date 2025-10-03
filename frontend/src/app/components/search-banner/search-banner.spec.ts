import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchBanner } from './search-banner';

describe('SearchBanner', () => {
  let component: SearchBanner;
  let fixture: ComponentFixture<SearchBanner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchBanner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchBanner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
