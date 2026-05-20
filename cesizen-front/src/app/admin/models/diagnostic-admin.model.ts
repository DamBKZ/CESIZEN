export interface AdminDiagnostic {
  id: string;
  score: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: string;
  user: {
    firstname: string;
    lastname: string;
  } | null;
}
