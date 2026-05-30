import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { InformationService } from '../information.service';

@Component({
  selector: 'app-information-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);

  info = signal<any | null>(null);

  constructor() {
    const slug = this.route.snapshot.paramMap.get('slug') ?? '';

    this.service.getById(slug).subscribe({
      next: (res: any) => this.info.set(res)
    });
  }

  getEmbedUrl(url: string): string {
    if (!url) return '';

    if (url.includes('youtube.com/watch')) {
      const id = url.split('v=')[1];
      return `https://www.youtube.com/embed/${id}`;
    }

    if (url.includes('youtu.be/')) {
      const id = url.split('youtu.be/')[1];
      return `https://www.youtube.com/embed/${id}`;
    }

    return url;
  }

  goBack(): void {
    this.router.navigate(['/information/list']);
  }
}
