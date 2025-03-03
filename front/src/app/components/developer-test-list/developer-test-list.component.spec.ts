import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeveloperTestListComponent } from './developer-test-list.component';

describe('DeveloperTestListComponent', () => {
  let component: DeveloperTestListComponent;
  let fixture: ComponentFixture<DeveloperTestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeveloperTestListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeveloperTestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
