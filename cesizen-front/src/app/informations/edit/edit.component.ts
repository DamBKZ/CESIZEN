import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';

import {
  Information,
  InformationCategory,
  InformationService,
  InformationType
} from '../information.service';
import { InformationFactory } from '../information.factory';
import { InformationForm } from '../information.models';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-edit',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(InformationService);
  private readonly factory = inject(InformationFactory);
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

  private informationId = '';
  private routeSlug = '';

  ngOnInit(): void {
    this.routeSlug =
      this.route.snapshot.paramMap.get('slug') ?? '';

    if (!this.routeSlug) {
      this.handleNotFound();
      return;
    }

    this.loadCategories();
    this.loadInformation();
  }

  private loadCategories(): void {
    this.service.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
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

  private loadInformation(): void {
    this.service.getBySlug(this.routeSlug).subscribe({
      next: (information: Information) => {
        this.informationId = information.informationId;
        this.type.set(information.type);

        this.form.set({
          author: information.author ?? '',
          slug: information.slug ?? '',
          tags: information.tags ?? [],
          categoryId: information.categoryId ?? '',
          title: information.title ?? '',
          content: information.content ?? '',
          videoUrl: information.videoUrl ?? '',
          pdfUrl: information.pdfUrl ?? ''
        });
      },
      error: () => this.handleNotFound()
    });
  }

  private handleNotFound(): void {
    this.ui.showSnackbar(
      'Information introuvable',
      'error'
    );

    this.router.navigate(['/informations/list']);
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
      .map((tag: string) => tag.trim())
      .filter(Boolean);

    this.updateFormField('tags', tags);
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

    switch (this.type()) {
      case 'ARTICLE':
        return Boolean(current.content.trim());

      case 'VIDEO':
        return Boolean(current.videoUrl.trim());

      case 'PDF':
        return Boolean(current.pdfUrl.trim());
    }
  }

  submit(): void {
    if (!this.informationId) {
      this.handleNotFound();
      return;
    }

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

    this.service
      .update(this.informationId, payload)
      .pipe(
        finalize(() => this.ui.setLoading(false))
      )
      .subscribe({
        next: () => {
          this.ui.showSnackbar(
            'Information mise à jour',
            'success'
          );

          this.router.navigate(['/informations/list']);
        },
        error: () => {
          this.ui.showSnackbar(
            'Erreur lors de la mise à jour',
            'error'
          );
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/informations/list']);
  }
}
