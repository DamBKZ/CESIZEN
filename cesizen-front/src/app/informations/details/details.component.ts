import { CommonModule } from '@angular/common';
import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import {
  Information,
  InformationService
} from '../information.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(InformationService);
  private readonly router = inject(Router);
  private readonly ui = inject(UiStore);
  private readonly sanitizer = inject(DomSanitizer);

  readonly info = signal<Information | null>(null);
  readonly videoEmbedUrl = signal<SafeResourceUrl | null>(null);

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug')?.trim();

    if (!slug) {
      this.handleNotFound();
      return;
    }

    this.service.getBySlug(slug).subscribe({
      next: (information) => {
        this.info.set(information);

        if (
          information.type === 'VIDEO' &&
          information.videoUrl
        ) {
          this.videoEmbedUrl.set(
            this.createSafeEmbedUrl(information.videoUrl)
          );
        }
      },
      error: () => this.handleNotFound()
    });
  }

  private createSafeEmbedUrl(
    url: string
  ): SafeResourceUrl | null {
    const embedUrl = this.toYouTubeEmbedUrl(url);

    if (!embedUrl) {
      return null;
    }

    return this.sanitizer.bypassSecurityTrustResourceUrl(
      embedUrl
    );
  }

  private toYouTubeEmbedUrl(url: string): string | null {
    try {
      const parsedUrl = new URL(url);
      let videoId: string | null = null;

      if (
        parsedUrl.hostname === 'youtube.com' ||
        parsedUrl.hostname === 'www.youtube.com'
      ) {
        if (parsedUrl.pathname === '/watch') {
          videoId = parsedUrl.searchParams.get('v');
        } else if (parsedUrl.pathname.startsWith('/embed/')) {
          videoId = parsedUrl.pathname.split('/')[2] ?? null;
        }
      }

      if (
        parsedUrl.hostname === 'youtu.be' ||
        parsedUrl.hostname === 'www.youtu.be'
      ) {
        videoId = parsedUrl.pathname
          .replace(/^\/+/, '')
          .split('/')[0] || null;
      }

      if (!videoId) {
        return null;
      }

      if (!/^[a-zA-Z0-9_-]{11}$/.test(videoId)) {
        return null;
      }

      return `https://www.youtube-nocookie.com/embed/${videoId}`;
    } catch {
      return null;
    }
  }

  private handleNotFound(): void {
    this.info.set(null);
    this.videoEmbedUrl.set(null);

    this.ui.showSnackbar(
      'Information introuvable',
      'error'
    );

    void this.router.navigate(['/informations/list']);
  }

  goBack(): void {
    void this.router.navigate(['/informations/list']);
  }
}
