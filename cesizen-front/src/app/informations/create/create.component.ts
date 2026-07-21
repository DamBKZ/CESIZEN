import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { InformationFactory } from '../information.factory';
import {
  InformationCategory,
  InformationService,
  InformationType
} from '../information.service';
import { UiStore } from '../../core/stores/ui.store';

interface InformationForm {
  author: string;
  slug: string;
  tags: string[];
  categoryId: string;
  title: string;
  content: string;
  videoUrl: string;
  pdfUrl: string;
}

@Component({
  selector: 'app-information-create',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {
  private readonly factory = inject(InformationFactory);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  readonly categories = signal<InformationCategory[]>([]);
  readonly type = signal<InformationType>('ARTICLE');

  readonly form = signal<InformationForm>({
    author: '',
    slug: '',
    tags: [],
    categoryId: '',
    title: '',
    content: '',
    videoUrl: '',
    pdfUrl: ''
  });

  ngOnInit(): void {
    this.loadCategories();
  }

  private loadCategories(): void {
    this.service.getCategories().subscribe({
      next: (response) => {
        this.categories.set(
          Array.isArray(response) ? response : []
        );
      },
      error: () => {
        this.categories.set([]);

        this.ui.showSnackbar(
          'Erreur lors du chargement des catégories',
          'error'
        );
      }
    });
  }

  updateFormField<K extends keyof InformationForm>(
    field: K,
    value: InformationForm[K]
  ): void {
    this.form.update((current) => ({
      ...current,
      [field]: value
    }));
  }

  updateTags(value: string): void {
    const tags = value
      .split(',')
      .map((tag) => tag.trim())
      .filter(Boolean);

    this.updateFormField('tags', tags);
  }

  getEmbedUrl(url: string): string {
    if (!url) {
      return '';
    }

    if (url.includes('youtube.com/watch')) {
      const id = url.split('v=')[1]?.split('&')[0];

      return id
        ? `https://www.youtube.com/embed/${id}`
        : url;
    }

    if (url.includes('youtu.be/')) {
      const id = url
        .split('youtu.be/')[1]
        ?.split('?')[0];

      return id
        ? `https://www.youtube.com/embed/${id}`
        : url;
    }

    return url;
  }

  goBack(): void {
    this.router.navigate(['/informations/list']);
  }

  isFormValid(): boolean {
    const current = this.form();

    if (
      !current.slug.trim() ||
      !current.categoryId.trim() ||
      !current.title.trim()
    ) {
      return false;
    }

    if (this.type() === 'ARTICLE') {
      return Boolean(current.content.trim());
    }

    if (this.type() === 'VIDEO') {
      return Boolean(current.videoUrl.trim());
    }

    return Boolean(current.pdfUrl.trim());
  }

  submit(): void {
    if (!this.isFormValid()) {
      this.ui.showSnackbar(
        'Les champs obligatoires ne sont pas remplis',
        'error'
      );

      return;
    }

    const payload = this.factory.create(
      this.type(),
      this.form()
    );

    this.ui.setLoading(true);

    this.service.create(payload).subscribe({
      next: () => {
        this.ui.showSnackbar(
          'Information créée',
          'success'
        );

        this.router.navigate([
          '/informations/list'
        ]);
      },
      error: () => {
        this.ui.setLoading(false);

        this.ui.showSnackbar(
          'Erreur lors de la création',
          'error'
        );
      },
      complete: () => {
        this.ui.setLoading(false);
      }
    });
  }
}
