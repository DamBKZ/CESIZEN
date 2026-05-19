import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { InformationService } from '../information.service';
import { FormsModule } from '@angular/forms';
import { UiStore } from '../../core/stores/ui.store';

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

  id = Number(this.route.snapshot.paramMap.get('id'));

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
    this.service.getById(this.id).subscribe({
      next: (res: any) => {
        this.type.set(res.type);
        this.form.set({
          author: res.author,
          slug: res.slug,
          tags: res.tags ?? [],
          categoryId: res.categoryId,
          title: res.title,
          content: res.content,
          videoUrl: res.videoUrl,
          pdfUrl: res.pdfUrl
        });
      }
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

  submit() {
    this.ui.setLoading(true);

    this.service.update(this.id, this.form()).subscribe({
      next: () => {
        this.ui.showSnackbar('Information mise à jour', 'success');
        this.router.navigate(['/information/list']);
      },
      error: () => this.ui.showSnackbar('Erreur lors de la mise à jour', 'error'),
      complete: () => this.ui.setLoading(false)
    });
  }
}
