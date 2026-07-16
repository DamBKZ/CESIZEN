import { Component, inject, signal } from '@angular/core';
import { InformationFactory } from '../information.factory';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UiStore } from '../../core/stores/ui.store';
import { INFORMATION_CATEGORIES } from '../information.categories';

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

  categories = INFORMATION_CATEGORIES;

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
    if (!url) {
      return '';
    }

    if (url.includes('youtube.com/watch')) {
      const id = url.split('v=')[1]?.split('&')[0];
      return id ? `https://www.youtube.com/embed/${id}` : url;
    }

    if (url.includes('youtu.be/')) {
      const id = url.split('youtu.be/')[1]?.split('?')[0];
      return id ? `https://www.youtube.com/embed/${id}` : url;
    }

    return url;
  }

  goBack(): void {
    this.router.navigate(['/informations/list']);
  }

  isFormValid(): boolean {
    const current = this.form();

    if (!current.slug?.trim() || !current.categoryId?.trim() || !current.title?.trim()) {
      return false;
    }

    if (this.type() === 'ARTICLE') {
      return Boolean(current.content?.trim());
    }

    if (this.type() === 'VIDEO') {
      return Boolean(current.videoUrl?.trim());
    }

    return Boolean(current.pdfUrl?.trim());
  }

  submit(): void {
    if (!this.isFormValid()) {
      this.ui.showSnackbar('Les champs obligatoires ne sont pas remplis', 'error');
      return;
    }

    const payload = this.factory.create(this.type(), this.form());

    this.ui.setLoading(true);

    this.service.create(payload).subscribe({
      next: () => {
        this.ui.showSnackbar('Information créée', 'success');
        this.router.navigate(['/informations/list']);
      },
      error: () => this.ui.showSnackbar('Erreur lors de la création', 'error'),
      complete: () => this.ui.setLoading(false)
    });
  }
}
