import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { InformationService } from '../information.service';
import { FormsModule } from '@angular/forms';
import { UiStore } from '../../core/stores/ui.store';
import { INFORMATION_CATEGORIES } from '../information.categories';

@Component({
  selector: 'app-information-edit',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  categories = INFORMATION_CATEGORIES;

  slug = this.route.snapshot.paramMap.get('slug') ?? '';

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

  constructor() {
    this.service.getById(this.slug).subscribe({
      next: (res: any) => {
        if (!res) {
          return;
        }

        this.type.set(res.type);
        this.form.set({
          author: res.author ?? '',
          slug: res.slug ?? '',
          tags: res.tags ?? [],
          categoryId: res.categoryId ?? '',
          title: res.title ?? '',
          content: res.content ?? '',
          videoUrl: res.videoUrl ?? '',
          pdfUrl: res.pdfUrl ?? ''
        });
      },
      error: () => {
        this.ui.showSnackbar('Information introuvable', 'error');
        this.router.navigate(['/informations/list']);
      }
    });
  }

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

    this.ui.setLoading(true);

    this.service.update(this.slug, this.form()).subscribe({
      next: () => {
        this.ui.showSnackbar('Information mise à jour', 'success');
        this.router.navigate(['/informations/list']);
      },
      error: () => this.ui.showSnackbar('Erreur lors de la mise à jour', 'error'),
      complete: () => this.ui.setLoading(false)
    });
  }
}
