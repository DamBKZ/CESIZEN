import { Injectable } from '@angular/core';
import { getInformationCategoryLabel } from './information.categories';

@Injectable({ providedIn: 'root' })
export class InformationFactory {

  create(type: string, data: any) {
    const base = {
      type,
      title: data.title?.trim(),
      author: data.author?.trim(),
      slug: data.slug?.trim(),
      tags: data.tags ?? [],
      categoryId: data.categoryId?.trim(),
      categoryName: getInformationCategoryLabel(data.categoryId?.trim())
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
