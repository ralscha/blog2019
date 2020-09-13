export interface Env {
  production: boolean;
  serverURL: string;
  version: string;
  buildTimestamp: number | null;
  shortCommitId: string | null;
  commitId: string | null;
  commitTime: number | null;
}
