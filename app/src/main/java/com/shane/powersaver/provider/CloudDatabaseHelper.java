package com.shane.powersaver.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

public class CloudDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = CloudDatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "cloud_configure.db";
    private static final int DATABASE_VERSION = 15;

    public CloudDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Downgrade database from version " +
                oldVersion + " to " + newVersion);
        updateDatabase(db, 0, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int fromVersion, int toVersion) {
        Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);

        // sanity checks
        if (toVersion != DATABASE_VERSION) {
            Log.e(TAG, "Illegal update request. Got " + toVersion + ", expected "
                    + DATABASE_VERSION);
            throw new IllegalArgumentException();
        } else if (fromVersion > toVersion) {
            Log.e(TAG, "Illegal update request: can't downgrade from " + fromVersion + " to "
                    + toVersion + ". Did you forget to wipe data?");
            throw new IllegalArgumentException();
        }

        if (fromVersion < 1) {
            db.beginTransaction();
            try {
                // Drop everything and start over.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion
                        + ", which will destroy all old data");

                db.execSQL("DROP TABLE IF EXISTS " + GlobalFeatureConfigure.TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + CloudAppConfigure.TABLE);

                db.execSQL("CREATE TABLE IF NOT EXISTS " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " TEXT NOT NULL "
                        + ");");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + CloudAppConfigure.TABLE + " ( "
                        + CloudAppConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CloudAppConfigure.Columns.PACKAGE_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, "
                        + CloudAppConfigure.Columns.BG_DATA + " TEXT, "
                        + CloudAppConfigure.Columns.BG_DATA_DELAY_TIME + " INTEGER, "
                        + CloudAppConfigure.Columns.BG_DATA_DELAY_COUNT + " INTEGER, "
                        + CloudAppConfigure.Columns.BG_DATA_MIN_DATA_KB + " INTEGER, "
                        + CloudAppConfigure.Columns.BG_DATA_MAX_INACTIVE_COUNT + " INTEGER, "
                        + CloudAppConfigure.Columns.BG_LOCATION + " TEXT, "
                        + CloudAppConfigure.Columns.BG_LOCATION_DELAY_TIME + " INTEGER "
                        + ");");

                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                ArrayList<String> defaultValues = new ArrayList<String>();
                defaultValues.add("( '" + GlobalFeatureConfigure.FEATURE_STATUS + "', '" + "false" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.USER_CONFIGURE_STATUS + "', '" + "true" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.LAST_UPDATED + "', '" + "0" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_DISABLE_SHORT_TIME + "', '" + "3" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_DISABLE_LONG_TIME + "', '" + "10" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_LOCATION_DISABLE_SHORT_TIME + "', '" + "3" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA + "', '" + "true" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_DELAY_TIME + "', '" + "3" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_DELAY_COUNT + "', '" + "-1" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_MIN_DATA_KB + "', '" + "-1" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_DATA_MAX_INACTIVE_COUNT + "', '" + "-1" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_LOCATION + "', '" + "true" + "' );");
                defaultValues.add("( '" + GlobalFeatureConfigure.BG_LOCATION_DELAY_TIME + "', '" + "-1" + "' );");
                for (String value : defaultValues) {
                    db.execSQL(insertSql + value);
                }
                defaultValues.clear();
                defaultValues = null;

                db.setTransactionSuccessful();
                fromVersion = 1;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 1) {
            db.beginTransaction();
            try {
                // Upgrade from Version 1 to Version 2.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String userConfigureStatus = "normal";
                String selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ?";
                String[] selectionArgs = new String[] { GlobalFeatureConfigure.USER_CONFIGURE_STATUS };
                Cursor cursor = db.query(GlobalFeatureConfigure.TABLE, null, selection, selectionArgs, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int valueIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM);
                    String value = cursor.getString(valueIndex);
                    if (value.equals("false")) {
                        userConfigureStatus = "disable";
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                String insertSql = "INSERT OR REPLACE INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.USER_CONFIGURE_STATUS + "', '" + userConfigureStatus + "' );";
                db.execSQL(insertSql + defaultValue);

                db.setTransactionSuccessful();
                fromVersion = 2;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 2) {
            db.beginTransaction();
            try {
                // Upgrade from Version 2 to Version 3.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.BA_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.FROZEN_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);

                db.setTransactionSuccessful();
                fromVersion = 3;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 3) {
            db.beginTransaction();
            try {
                // Upgrade from Version 3 to Version 4.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.DEVICEIDLE_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS + "', '" + "" + "' );";
                db.execSQL(insertSql + defaultValue);

                db.setTransactionSuccessful();
                fromVersion = 4;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 4) {
            db.beginTransaction();
            try {
                // Upgrade from Version 4 to Version 5.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.MIUI_IDLE_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.MIUI_STANDBY_STATUS + "', '" + "true" + "' );";
                db.execSQL(insertSql + defaultValue);

                db.setTransactionSuccessful();
                fromVersion = 5;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 5) {
            db.beginTransaction();
            try {
                // Upgrade from Version 5 to Version 6.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.MUSIC_APPS + "', '" + "" + "' );";
                db.execSQL(insertSql + defaultValue);
                db.setTransactionSuccessful();
                fromVersion = 6;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 6) {
            db.beginTransaction();
            try {
                // Upgrade from Version 6 to Version 7.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.BG_KILL_DELAY + "', '" + "-2" + "' );";
                db.execSQL(insertSql + defaultValue);
                String alterSql = "ALTER TABLE " + CloudAppConfigure.TABLE + " ADD COLUMN "
                                + CloudAppConfigure.Columns.BG_KILL_DELAY + " INTEGER";
                db.execSQL(alterSql);
                db.setTransactionSuccessful();
                fromVersion = 7;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 7) {
            db.beginTransaction();
            try {
                // Upgrade from Version 7 to Version 8.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.SENSOR_DELAY + "', '" + "-2" + "' );";
                db.execSQL(insertSql + defaultValue);

                defaultValue = "( '" + GlobalFeatureConfigure.SENSORS_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);

                String alterSql = "ALTER TABLE " + CloudAppConfigure.TABLE + " ADD COLUMN "
                        + CloudAppConfigure.Columns.BG_SENSOR_DELAY + " INTEGER";
                db.execSQL(alterSql);

                db.setTransactionSuccessful();
                fromVersion = 8;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 8) {
            db.beginTransaction();
            try {
                // Upgrade from Version 8 to Version 9.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS + "', '" + "" + "' );";
                db.execSQL(insertSql + defaultValue);
                db.setTransactionSuccessful();
                fromVersion = 9;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 9) {
            db.beginTransaction();
            try {
                // Upgrade from Version 9 to Version 10.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                db.setTransactionSuccessful();
                fromVersion = 10;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 10) {
            db.beginTransaction();
            try {
                // Upgrade from Version 10 to Version 11.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);

                String alterSql = "ALTER TABLE " + GlobalFeatureConfigure.TABLE + " RENAME TO "
                        + "tempGlobalFeatureTable";
                db.execSQL(alterSql);

                db.execSQL("DROP TABLE IF EXISTS " + GlobalFeatureConfigure.TABLE);

                db.execSQL("CREATE TABLE IF NOT EXISTS " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + GlobalFeatureConfigure.Columns.USER_ID + " INTEGER NOT NULL DEFAULT 0, "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " TEXT NOT NULL, "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " TEXT NOT NULL, "
                        + "UNIQUE (userId, configureName) ON CONFLICT REPLACE "
                        + ");");

                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + "configureName" + ","
                        + "configureParam" + " ) "
                        + "SELECT " + "configureName" + ", "
                        + "configureParam" + " "
                        + "FROM " + "tempGlobalFeatureTable";
                db.execSQL(insertSql);

                String deleteSql = "DROP TABLE " + "tempGlobalFeatureTable";
                db.execSQL(deleteSql);

                db.setTransactionSuccessful();
                fromVersion = 11;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 11) {
            db.beginTransaction();
            try {
                // Upgrade from Version 10 to Version 11.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);

                db.execSQL("DROP TABLE IF EXISTS " + BroadcastManageGlobalFeatureConfigure.TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + BroadcastManageCloudAppConfigure.TABLE);

                db.execSQL("CREATE TABLE IF NOT EXISTS " + BroadcastManageGlobalFeatureConfigure.TABLE + " ( "
                        + BroadcastManageGlobalFeatureConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, "
                        + BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " TEXT NOT NULL "
                        + ");");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + BroadcastManageCloudAppConfigure.TABLE + " ( "
                        + BroadcastManageCloudAppConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BroadcastManageCloudAppConfigure.Columns.PACKAGE_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_BROADCAST_IDS + " TEXT, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_EXCEPT_PKGS + " TEXT, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_DELAY + " INTEGER "
                        + ");");

                String insertSql = "INSERT INTO " + BroadcastManageGlobalFeatureConfigure.TABLE + " ( "
                        + BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                ArrayList<String> defaultValues = new ArrayList<String>();
                defaultValues.add("( '" + BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS + "', '" + "false" + "' );");
                defaultValues.add("( '" + BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY + "', '" + "-2" + "' );");
                for (String value : defaultValues) {
                    db.execSQL(insertSql + value);
                }
                defaultValues.clear();
                defaultValues = null;

                db.setTransactionSuccessful();

                fromVersion = 12;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 12) {
            db.beginTransaction();
            try {
                // Upgrade from Version 12 to Version 13.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM + "', '" + "" + "' );";
                db.execSQL(insertSql + defaultValue);
                db.setTransactionSuccessful();
                fromVersion = 13;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 13) {
            db.beginTransaction();
            try {
                // Upgrade from Version 13 to Version 14.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);
                String insertSql = "INSERT INTO " + GlobalFeatureConfigure.TABLE + " ( "
                        + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + ","
                        + GlobalFeatureConfigure.Columns.CONFIGURE_PARAM + " ) "
                        + "VALUES ";
                String defaultValue = "( '" + GlobalFeatureConfigure.BG_LOCATION_DELAY_HOT + "', '" + "-1" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.BG_KILL_DELAY_HOT + "', '" + "-2" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                defaultValue = "( '" + GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE + "', '" + "false" + "' );";
                db.execSQL(insertSql + defaultValue);
                String alterSql = "ALTER TABLE " + CloudAppConfigure.TABLE + " ADD COLUMN "
                                + CloudAppConfigure.Columns.BG_LOCATION_DELAY_HOT + " INTEGER";
                db.execSQL(alterSql);
                alterSql = "ALTER TABLE " + CloudAppConfigure.TABLE + " ADD COLUMN "
                        + CloudAppConfigure.Columns.BG_KILL_DELAY_HOT + " INTEGER";
                db.execSQL(alterSql);
                db.setTransactionSuccessful();
                fromVersion = 14;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (fromVersion == 14) {
            db.beginTransaction();
            try {
                // Upgrade from Version 14 to Version 15.
                Log.i(TAG, "Upgrading database from version " + fromVersion + " to " + toVersion);

                String alterSql = "ALTER TABLE " + BroadcastManageCloudAppConfigure.TABLE + " RENAME TO "
                        + "tempBroadcastManageGlobalFeatureTable";
                db.execSQL(alterSql);

                db.execSQL("DROP TABLE IF EXISTS " + BroadcastManageCloudAppConfigure.TABLE);

                db.execSQL("CREATE TABLE IF NOT EXISTS " + BroadcastManageCloudAppConfigure.TABLE + " ( "
                        + BroadcastManageCloudAppConfigure.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BroadcastManageCloudAppConfigure.Columns.PACKAGE_NAME + " TEXT, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_BROADCAST_IDS + " TEXT, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_EXCEPT_PKGS + " TEXT, "
                        + BroadcastManageCloudAppConfigure.Columns.BG_DELAY + " INTEGER, "
                        + "UNIQUE (pkgName, bgBroadcastIds, except_pkg) ON CONFLICT REPLACE "
                        + ");");

                String insertSql = "INSERT INTO " + BroadcastManageCloudAppConfigure.TABLE + " ( "
                        + "pkgName" + ","
                        + "bgBroadcastIds" + ","
                        + "except_pkg" + ","
                        + "b_delay" + " ) "
                        + "SELECT " + "pkgName" + ", "
                        + "bgBroadcastIds" + ", "
                        + "except_pkg" + ", "
                        + "b_delay" + " "
                        + "FROM " + "tempBroadcastManageGlobalFeatureTable";
                db.execSQL(insertSql);

                String deleteSql = "DROP TABLE " + "tempBroadcastManageGlobalFeatureTable";
                db.execSQL(deleteSql);

                db.setTransactionSuccessful();
                fromVersion = 15;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        if (toVersion != fromVersion) {
            Log.e(TAG, "Upgrade database to version " + toVersion + " fails");
        } else {
            Log.i(TAG, "Upgrade database to version " + toVersion + " success");
        }
    }
}
