import { WebPlugin } from '@capacitor/core';

import type { SqlitePlugin } from './definitions';

export class SqliteWeb extends WebPlugin implements SqlitePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
