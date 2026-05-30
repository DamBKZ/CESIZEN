export type InformationType = 'ARTICLE' | 'VIDEO' | 'PDF';

export interface InformationMock {
  informationId: string;
  title: string;
  type: InformationType;
  author: string;
  slug: string;
  categoryId: string;
  categoryName: string;
  tags: string[];
  createdAt: string;
  content?: string;
  videoUrl?: string;
  pdfUrl?: string;
}

export const INFORMATION_MOCKS: InformationMock[] = [
  {
    informationId: 'info-1',
    title: 'Comprendre le stress au quotidien',
    type: 'ARTICLE',
    author: 'Damien',
    slug: 'premier-article',
    categoryId: 'cat-stress',
    categoryName: 'Stress',
    tags: ['stress', 'santé mentale', 'bien-être'],
    createdAt: '2026-05-28T08:30:00.000Z',
    content: 'Le stress est une réaction normale du corps. Apprendre à le reconnaître tôt permet d\'éviter la surcharge et de protéger son bien-être.'
  },
  {
    informationId: 'info-2',
    title: 'Respiration guidée pour se recentrer',
    type: 'VIDEO',
    author: 'Admin CESIZEN',
    slug: 'deuxieme-article',
    categoryId: 'cat-relaxation',
    categoryName: 'Relaxation',
    tags: ['respiration', 'pause', 'concentration'],
    createdAt: '2026-05-27T10:00:00.000Z',
    videoUrl: 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'
  },
  {
    informationId: 'info-3',
    title: 'Sommeil et récupération mentale',
    type: 'ARTICLE',
    author: 'Équipe CESIZEN',
    slug: 'sommeil-et-recuperation-mentale',
    categoryId: 'cat-bien-etre',
    categoryName: 'Bien-être',
    tags: ['sommeil', 'stress', 'récupération'],
    createdAt: '2026-05-26T14:20:00.000Z',
    content: 'Un sommeil régulier aide le système nerveux à revenir à l\'équilibre. Cette routine réduit les tensions et améliore la concentration.'
  },
  {
    informationId: 'info-4',
    title: 'Mini routine anti-stress de 5 minutes',
    type: 'ARTICLE',
    author: 'Coach CESIZEN',
    slug: 'mini-routine-anti-stress-5-minutes',
    categoryId: 'cat-stress',
    categoryName: 'Stress',
    tags: ['respiration', 'ancrage', 'calme'],
    createdAt: '2026-05-25T09:00:00.000Z',
    content: 'Cette routine combine respiration, relâchement des épaules et recentrage. Elle est conçue pour faire redescendre la pression rapidement.'
  },
  {
    informationId: 'info-5',
    title: 'Bien-être au travail: 4 micro-pauses utiles',
    type: 'PDF',
    author: 'Équipe prévention',
    slug: 'bien-etre-au-travail-4-micro-pauses',
    categoryId: 'cat-bien-etre',
    categoryName: 'Bien-être',
    tags: ['travail', 'bien-être', 'fatigue mentale'],
    createdAt: '2026-05-24T13:45:00.000Z',
    pdfUrl: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf'
  }
];
