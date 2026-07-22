export interface AdminDiagnostic {
  surveyId: string;
  score: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: string;
  userId?: string;
  userEmail?: string;
  userPseudo?: string;
}
