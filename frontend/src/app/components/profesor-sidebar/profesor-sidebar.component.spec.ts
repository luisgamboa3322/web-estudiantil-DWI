import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfesorSidebarComponent } from './profesor-sidebar.component';

describe('ProfesorSidebarComponent', () => {
  let component: ProfesorSidebarComponent;
  let fixture: ComponentFixture<ProfesorSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfesorSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfesorSidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
