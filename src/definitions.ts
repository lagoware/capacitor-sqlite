export interface SqlitePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
