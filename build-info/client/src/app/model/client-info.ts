export interface ClientInfo {
  version: string | null;
  buildTimestamp: number | null;
  shortCommitId: string | null;
  commitId: string | null;
  commitTime: number | null;
}
