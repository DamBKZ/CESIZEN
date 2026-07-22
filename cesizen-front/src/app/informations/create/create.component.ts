import { InformationForm } from '../information.models';


import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { InformationFactory } from '../information.factory';

import {
  InformationCategory,
  InformationService,
  InformationType
} from '../information.service';

import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-create',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {
  private readonly factory = inject(InformationFactory);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);

  readonly categories =
    signal<InformationCategory[]>([]);

  readonly type =
    signal<InformationType>('ARTICLE');

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

  updateFormField<K extends keyof InformationForm>(
    field: K,
    value: InformationForm[K]
  ): void {
    this.form.update(current => ({
      ...current,
      [field]: value
    }));
  }

  updateTags(value: string): void {
    const tags = value
      .split(',')
      .map(tag => tag.trim())
      .filter(Boolean);

    this.updateFormField('tags', tags);
  }

  goBack(): void {
    void this.router.navigate([
      '/informations/list'
    ]);
  }

  isFormValid(): boolean {
    const current = this.form();

    if (
      !current.slug.trim()
      || !current.categoryId.trim()
      || !current.title.trim()
    ) {
      return false;
    }

    switch (this.type()) {
      case 'ARTICLE':
        return current.content.trim().length > 0;

      case 'VIDEO':
        return current.videoUrl.trim().length > 0;

      case 'PDF':
        return current.pdfUrl.trim().length > 0;
    }
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

    this.service
      .create(payload)
      .pipe(
        finalize(() => {
          this.ui.setLoading(false);
        })
      )
      .subscribe({
        next: () => {
          this.ui.showSnackbar(
            'Information créée',
            'success'
          );

          void this.router.navigate([
            '/informations/list'
          ]);
        },

        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la création',
            'error'
          );
        }
      });
  }

  private loadCategories(): void {
    this.service.getCategories().subscribe({
      next: categories => {
        this.categories.set(categories);

        const selectedCategoryId =
          this.form().categoryId;

        const categoryStillExists =
          categories.some(
            category =>
              category.categoryId
                === selectedCategoryId
          );

        if (!categoryStillExists) {
          this.updateFormField(
            'categoryId',
            ''
          );
        }
      },

      error: () => {
        this.categories.set([]);
        this.updateFormField('categoryId', '');

        this.ui.showSnackbar(
          'Erreur lors du chargement des catégories',
          'error'
        );
      }
    });
  }
}
