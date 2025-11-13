import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentCalendarioComponent } from './student-calendario.component';

describe('StudentCalendarioComponent', () => {
  let component: StudentCalendarioComponent;
  let fixture: ComponentFixture<StudentCalendarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentCalendarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentCalendarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
