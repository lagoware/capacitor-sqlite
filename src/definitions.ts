export type StatementSpec = {
  type: "query"|"command";
  statement: string;
  beginsTransaction?: boolean,
  commitsTransaction?: boolean,
  rollsBackTransaction?: boolean,
  params?: (string | string[])[]
};
export type StatementExecReturnVal<T = any> = (null|T[])|(null|T[])[];

export interface SqlitePlugin {
  openDb(options: { dbName: string, version: number, upgrades: Record<number,string[]> }): Promise<void>;
  runStatements<T = any>(options: { dbName: string, statementSpecs: StatementSpec[] }): Promise<{ results: StatementExecReturnVal<T>[] }>;
}