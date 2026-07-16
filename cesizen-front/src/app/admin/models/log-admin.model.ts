export interface AdminLog {
  logId: string;
  content: string;
  createdAt: string;
  userId?: string;
  userEmail?: string;
  userPseudo?: string;
}
