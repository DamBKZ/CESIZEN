import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-diagnostic-list',
  standalone: true,
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent {
  private readonly router = inject(Router);

  startDiagnostic(): void {
    void this.router.navigate(['/diagnostic/run']);
  }
}
