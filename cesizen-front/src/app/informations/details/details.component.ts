import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InformationService } from '../information.service';

@Component({
  selector: 'app-information-details',
  standalone: true,
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(InformationService);

  info = signal<any | null>(null);

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getById(id).subscribe({
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
}
