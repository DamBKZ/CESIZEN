import { Component, inject, signal } from '@angular/core';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-information-list',
  standalone: true,
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent {
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);

  items = signal<any[]>([]);

  constructor() {
    this.service.getAll().subscribe({
      next: (res: any) => this.items.set(res)
    });
  }

  open(id: string) {
    this.router.navigate(['/information/details', id]);
  }

  edit(id: string) {
    this.router.navigate(['/information/edit', id]);
  }
}
