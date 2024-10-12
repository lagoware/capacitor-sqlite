package com.lagoware.capacitorsqlite;

import static org.junit.Assert.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SqliteInstrumentedTest {

    @Test
    public void runStatements() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        HashMap<Integer, String[]> upgrades = new HashMap<>();

        appContext.deleteDatabase("testDb");

        upgrades.put(1, new String[] {
        """
            CREATE TABLE IF NOT EXISTS event_stream (
                id TEXT PRIMARY KEY,
                metadata TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                namespace TEXT NOT NULL
            );
        """,
            """
            CREATE TABLE IF NOT EXISTS event_payload (
                eventId TEXT PRIMARY KEY,
                eventType TEXT NOT NULL,
                data TEXT NOT NULL,
                metadata TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                positionGlobal INTEGER,
                streamId TEXT NOT NULL,
                version INTEGER NOT NULL,
                namespace TEXT NOT NULL
            );
        """,
        """
        CREATE INDEX event_payload_by_namespace_by_global_position
        ON event_payload (namespace,positionGlobal);
        """,
        """
        CREATE INDEX event_payload_by_namespace_by_stream_id_by_version
        ON event_payload (namespace,streamId,version);
        """,
        """
        CREATE TRIGGER validate_event_payload_version
            BEFORE INSERT ON event_payload
            BEGIN
            SELECT
                CASE
                    WHEN NEW.version IS NOT coalesce((SELECT version FROM event_payload WHERE namespace = NEW.namespace AND streamId = NEW.streamId ORDER BY version DESC LIMIT 1), -1) + 1
                    THEN
                        RAISE (ABORT, 'Invalid version')
                END;
            END;
        """,
        """
        CREATE TRIGGER add_event_payload_global_position
            AFTER INSERT ON event_payload
            BEGIN
                UPDATE event_payload
                SET
                    positionGlobal = coalesce((SELECT positionGlobal FROM event_payload WHERE namespace = NEW.namespace ORDER BY positionGlobal DESC LIMIT 1), -1) + 1
                WHERE eventId = NEW.eventId;
            END;
        """
        });

        CapacitorSqliteDbManager.openHelper(appContext, "testDb", 3, upgrades).getWritableDatabase();

        Sqlite db = new Sqlite();

        JSONArray actual = db.runStatements("testDb", new SqliteStatementSpec[] {
            new SqliteStatementSpec("command", """
                INSERT INTO event_payload (eventId,eventType,data,metadata,streamId,namespace,version)
                VALUES (?1,?2,?3,?4,?5,?6,?7)
            """, new String[] { "e1", "FooEvent", "{\"foo\":\"bar\"}", "{\"bing\":\"bong\"}", "s1", "food", "0" }, true, false),

            new SqliteStatementSpec("query", """
                INSERT INTO event_stream (id,metadata,namespace)
                    VALUES (?1,?2,?3)
                RETURNING timestamp;
            """, new String[] { "s1", "{\"bing\":\"asdasd\"}", "food" }),

            new SqliteStatementSpec("query", """
                INSERT INTO event_payload (eventId,eventType,data,metadata,streamId,namespace,version)
                VALUES (?1,?2,?3,?4,?5,?6,?7)
                RETURNING eventId;
            """, new String[] { "e2", "FahEvent", "{\"foo\":\"sds\"}", "{\"bing\":\"asdf\"}", "s1", "food", "1" }),

            new SqliteStatementSpec("query", """
                INSERT INTO event_payload (eventId,eventType,data,metadata,streamId,namespace,version)
                VALUES (?1,?2,?3,?4,?5,?6,?7)
                RETURNING eventId;
            """, new String[][] {
                { "e3", "FahEvent", "{\"foo\":\"gga\"}", "{\"bing\":\"sas\"}", "s2", "food", "0" },
                { "e4", "BagEvent", "{\"foo\":\"lsa\"}", "{\"bing\":\"baba\"}", "s1", "food", "2" }
            }),

            new SqliteStatementSpec("command", """
                INSERT INTO event_payload (eventId,eventType,data,metadata,streamId,namespace,version)
                VALUES (?1,?2,?3,?4,?5,?6,?7)
            """, new String[][] {
                { "e5", "WASEvent", "{\"foo\":\"asgasg\"}", "{\"bing\":\"asfsa\"}", "s2", "food", "1" },
                { "e6", "BSOEvent", "{\"foo\":\"asgasg\"}", "{\"bing\":\"agasga\"}", "s1", "food", "3" }
            }),

            new SqliteStatementSpec("query", """
                 SELECT '' as eventId, '' as eventType, '' as data, '' as metadata, -1 as positionGlobal, -1 as version, id as streamId, event_stream.timestamp
                 FROM event_stream
                 WHERE id = ?2 AND namespace = ?1
                 UNION ALL
                 SELECT * FROM (SELECT eventId, eventType, data, event_payload.metadata, positionGlobal, version, event_payload.streamId, event_payload.timestamp
                 FROM event_payload
                 WHERE streamId = ?2 AND namespace = ?1
                 ORDER BY streamId, version
                 LIMIT ?4
                 OFFSET ?3)
            """, new String[] { "food", "s1", "0", "2" }, false, true)
        });

        assertTrue(actual.isNull(0));

        JSONArray streamResponses = actual.getJSONArray(1);
        assertEquals(1, streamResponses.length());
        assertEquals(19, streamResponses.getJSONObject(0).getString("timestamp").length());

        JSONArray eventIdResponses = actual.getJSONArray(2);
        assertEquals(1, eventIdResponses.length());
        assertEquals("e2", eventIdResponses.getJSONObject(0).getString("eventId"));

        JSONArray multiResponses = actual.getJSONArray(3);
        assertEquals(2, multiResponses.length());
        assertEquals(1, multiResponses.getJSONArray(0).length());
        assertEquals("e3", multiResponses.getJSONArray(0).getJSONObject(0).getString("eventId"));
        assertEquals(1, multiResponses.getJSONArray(1).length());
        assertEquals("e4", multiResponses.getJSONArray(1).getJSONObject(0).getString("eventId"));

        assertTrue(actual.isNull(4));

        JSONArray queryResult = actual.getJSONArray(5);
        assertEquals(3, queryResult.length());

        assertEquals("", queryResult.getJSONObject(0).getString("eventId"));
        assertEquals("", queryResult.getJSONObject(0).getString("eventType"));
        assertEquals("", queryResult.getJSONObject(0).getString("data"));
        assertEquals("", queryResult.getJSONObject(0).getString("metadata"));
        assertEquals("s1", queryResult.getJSONObject(0).getString("streamId"));
        assertEquals(-1, queryResult.getJSONObject(0).getInt("positionGlobal"));
        assertEquals(-1, queryResult.getJSONObject(0).getInt("version"));
        assertEquals(19, queryResult.getJSONObject(0).getString("timestamp").length());

        assertEquals("e1", queryResult.getJSONObject(1).getString("eventId"));
        assertEquals("FooEvent", queryResult.getJSONObject(1).getString("eventType"));
        assertEquals("{\"foo\":\"bar\"}", queryResult.getJSONObject(1).getString("data"));
        assertEquals("{\"bing\":\"bong\"}", queryResult.getJSONObject(1).getString("metadata"));
        assertEquals("s1", queryResult.getJSONObject(1).getString("streamId"));
        assertEquals(0, queryResult.getJSONObject(1).getInt("positionGlobal"));
        assertEquals(0, queryResult.getJSONObject(1).getInt("version"));
        assertEquals(19, queryResult.getJSONObject(1).getString("timestamp").length());

        assertEquals("e2", queryResult.getJSONObject(2).getString("eventId"));
        assertEquals("FahEvent", queryResult.getJSONObject(2).getString("eventType"));
        assertEquals("{\"foo\":\"sds\"}", queryResult.getJSONObject(2).getString("data"));
        assertEquals("{\"bing\":\"asdf\"}", queryResult.getJSONObject(2).getString("metadata"));
        assertEquals("s1", queryResult.getJSONObject(2).getString("streamId"));
        assertEquals(1, queryResult.getJSONObject(2).getInt("positionGlobal"));
        assertEquals(1, queryResult.getJSONObject(2).getInt("version"));
        assertEquals(19, queryResult.getJSONObject(2).getString("timestamp").length());
    }
}
