import { WebPlugin } from '@capacitor/core';

import type { SqlitePlugin, StatementSpec } from './definitions';

export class SqliteWeb extends WebPlugin implements SqlitePlugin {
  
  openDb(options: { dbName: string; version: number; upgrades: Record<number, string[]>; }): Promise<void> {
    throw new Error('Method not implemented.');
  }

  runStatements<T = any>(options: { dbName: string; statementSpecs: StatementSpec[]; }): Promise<{ results: (null | T | T[])[]; }> {
    throw new Error('Method not implemented.');
  }
  
}
