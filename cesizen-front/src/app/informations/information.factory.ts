import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class InformationFactory {

  create(type: 'ARTICLE' | 'VIDEO' | 'PDF', data: any) {
    const base = {
      type,
      title: data.title?.trim(),
      author: data.author?.trim() || undefined,
      slug: data.slug?.trim(),
      tags: Array.isArray(data.tags) ? data.tags : [],
      categoryId: data.categoryId?.trim()
    };

    switch (type) {
      case 'ARTICLE':
        return {
          ...base,
          content: data.content?.trim()
        };

      case 'VIDEO':
        return {
          ...base,
          videoUrl: data.videoUrl?.trim()
        };

      case 'PDF':
        return {
          ...base,
          pdfUrl: data.pdfUrl?.trim()
        };
    }
  }
}
