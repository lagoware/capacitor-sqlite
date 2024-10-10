import { registerPlugin } from '@capacitor/core';

import type { SqlitePlugin } from './definitions';

const Sqlite = registerPlugin<SqlitePlugin>('Sqlite', {
  web: () => import('./web').then((m) => new m.SqliteWeb()),
});

export * from './definitions';
export { Sqlite };
