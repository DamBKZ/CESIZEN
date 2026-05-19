import { Component, inject, signal } from '@angular/core';
import { InformationFactory } from '../information.factory';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-create',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent {
  private readonly factory = inject(InformationFactory);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  type = signal<'ARTICLE' | 'VIDEO' | 'PDF'>('ARTICLE');

  form = signal<any>({
    author: '',
    slug: '',
    tags: [],
    categoryId: '',
    title: '',
    content: '',
    videoUrl: '',
    pdfUrl: ''
  });

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

  submit() {
    const payload = this.factory.create(this.type(), this.form());

    this.ui.setLoading(true);

    this.service.create(payload).subscribe({
      next: () => {
        this.ui.showSnackbar('Information créée', 'success');
        this.router.navigate(['/information/list']);
      },
      error: () => this.ui.showSnackbar('Erreur lors de la création', 'error'),
      complete: () => this.ui.setLoading(false)
    });
  }
}
