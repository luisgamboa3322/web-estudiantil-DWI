import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentConfiguracionComponent } from './student-configuracion.component';

describe('StudentConfiguracionComponent', () => {
  let component: StudentConfiguracionComponent;
  let fixture: ComponentFixture<StudentConfiguracionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentConfiguracionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentConfiguracionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
