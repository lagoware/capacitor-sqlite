# @lagoware/capacitor-sqlite

Capacitor plugin exposing platform-specific sqlite bindings.

* [x] Android
* [ ] Web
* [ ] iOS

## Why Does This Exist?

There is [another](https://github.com/capacitor-community/sqlite) Capacitor SQLite plugin, that seems to have a fair number of happy users. When attempting to use this plugin, however, I was struck by a few things:

- Sprawling API, very difficult to find straight-forward examples for the most basic use case.
- Appears to opt for programmatic alternatives to existing SQLite features
- Buggy. I was getting cryptic errors that appeared to be due to complex transformation of my statements.
- As far as I can tell, the plugin seems to expose methods for transaction handling across asynchronous processes. [This is a bad idea](https://github.com/WiseLibs/better-sqlite3/blob/master/docs/api.md#caveats). 

For these reasons, I sought to create a plugin that:

- Is easy to integrate and understand.
- Nudges developers toward SQLite best practices (no keeping transactions open across async processes).
- Support as much expressiveness as possible given the restrictions imposed by the asynchronous nature of Capacitor calls.

For now, I am only building an Android app, so that is the only implementation available. PRs are welcome.

## Install

```bash
npm install @lagoware/capacitor-sqlite
npx cap sync
```

## API

<docgen-index>

* [`openDb(...)`](#opendb)
* [`runStatements(...)`](#runstatements)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### openDb(...)

```typescript
openDb(options: { dbName: string; version: number; upgrades: Record<number, string[]>; }) => Promise<void>
```

| Param         | Type                                                                                                              |
| ------------- | ----------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ dbName: string; version: number; upgrades: <a href="#record">Record</a>&lt;number, string[]&gt;; }</code> |

--------------------


### runStatements(...)

```typescript
runStatements<T = any>(options: { dbName: string; statementSpecs: StatementSpec[]; }) => Promise<{ results: StatementExecReturnVal<T>[]; }>
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code>{ dbName: string; statementSpecs: StatementSpec[]; }</code> |

**Returns:** <code>Promise&lt;{ results: <a href="#statementexecreturnval">StatementExecReturnVal</a>&lt;T&gt;[]; }&gt;</code>

--------------------


### Type Aliases


#### Record

Construct a type with a set of properties K of type T

<code>{ [P in K]: T; }</code>


#### StatementExecReturnVal

<code>(null|T[]) | (null|T[])[]</code>


#### StatementSpec

<code>{ type: "query"|"command"; statement: string; beginsTransaction?: boolean, commitsTransaction?: boolean, params?: (string | string[])[] }</code>

</docgen-api>
