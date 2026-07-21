import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-information-search',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);

  keyword = signal('');
  results = signal<any[]>([]);
  loading = signal(false);

  search() {
    if (!this.keyword().trim()) {
      this.results.set([]);
      return;
    }

    this.loading.set(true);

    this.service.search({ keyword: this.keyword() }).subscribe({
      next: (res: any) => this.results.set(res.content ?? res),
      complete: () => this.loading.set(false)
    });
  }

  open(slug: string) {
    this.router.navigate(['/information/details', slug]);
  }

  edit(slug: string) {
    this.router.navigate(['/information/edit', slug]);
  }
}
