import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class InformationFactory {

  create(type: string, data: any) {
    const base = {
      type,
      title: data.title,
      author: data.author,
      slug: data.slug,
      tags: data.tags ?? [],
      categoryId: data.categoryId
    };

    switch (type) {

      case 'ARTICLE':
        return {
          ...base,
          content: data.content
        };

      case 'VIDEO':
        return {
          ...base,
          videoUrl: data.videoUrl
        };

      case 'PDF':
        return {
          ...base,
          pdfUrl: data.pdfUrl
        };

      default:
        throw new Error('Type d’information inconnu');
    }
  }
}
