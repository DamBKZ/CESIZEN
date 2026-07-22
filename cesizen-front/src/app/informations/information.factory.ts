


import {
  Injectable
} from '@angular/core';

import {
  InformationRequest,
  InformationType
} from './information.service';
import { InformationForm } from './information.models';

@Injectable({
  providedIn: 'root'
})
export class InformationFactory {
  create(
    type: InformationType,
    data: InformationForm
  ): InformationRequest {
    const base = {
      type,
      title: data.title.trim(),
      author: data.author.trim() || undefined,
      slug: data.slug.trim(),
      tags: data.tags
        .map(tag => tag.trim())
        .filter(Boolean),
      categoryId: data.categoryId.trim()
    };

    switch (type) {
      case 'ARTICLE':
        return {
          ...base,
          content: data.content.trim()
        };

      case 'VIDEO':
        return {
          ...base,
          videoUrl: data.videoUrl.trim()
        };

      case 'PDF':
        return {
          ...base,
          pdfUrl: data.pdfUrl.trim()
        };
    }
  }
}
