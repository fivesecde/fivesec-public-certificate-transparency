import { TestBed } from '@angular/core/testing';

import { CaLoader } from './ca-loader';

describe('CaLoader', () => {
  let service: CaLoader;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CaLoader);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
