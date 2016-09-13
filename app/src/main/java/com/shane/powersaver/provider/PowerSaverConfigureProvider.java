package com.shane.powersaver.provider;

import java.io.File;

import java.util.HashMap;
import java.util.List;


import com.shane.powersaver.AppContext;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.Log;

public class PowerSaverConfigureProvider extends ContentProvider {
    private static final String TAG = PowerSaverConfigureProvider.class.getSimpleName();
    private static final boolean DEBUG = AppContext.DEBUG;

    private CloudDatabaseHelper mCloudHelper;
    private Context mContext;


    private static final int TYPE_GLOBAL = 0x1;
    private static final int TYPE_GLOBAL_ID = 0x2;
    private static final int TYPE_CLOUD_APP = 0x3;
    private static final int TYPE_CLOUD_APP_ID = 0x4;
    private static final int TYPE_CLOUD_APP_PKG = 0x5;
    private static final int TYPE_USER_APP = 0x6;
    private static final int TYPE_USER_APP_ID = 0x7;
    private static final int TYPE_APP_ACTIVE = 0x9;
    private static final int TYPE_APP_ACTIVE_UID = 0xA;
    private static final int TYPE_BROADCAST_MANAGE_GLOBAL = 0x13;
    private static final int TYPE_BROADCAST_MANAGE_GLOBAL_ID = 0x14;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP = 0x15;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP_ID = 0x16;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP_PKG = 0x17;

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, GlobalFeatureConfigure.TABLE, TYPE_GLOBAL);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, GlobalFeatureConfigure.TABLE+"/#", TYPE_GLOBAL_ID);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, CloudAppConfigure.TABLE, TYPE_CLOUD_APP);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, CloudAppConfigure.TABLE+"/#", TYPE_CLOUD_APP_ID);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, CloudAppConfigure.TABLE+"/*", TYPE_CLOUD_APP_PKG);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, BroadcastManageGlobalFeatureConfigure.TABLE, TYPE_BROADCAST_MANAGE_GLOBAL);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, BroadcastManageGlobalFeatureConfigure.TABLE+"/#", TYPE_BROADCAST_MANAGE_GLOBAL_ID);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, BroadcastManageCloudAppConfigure.TABLE, TYPE_BROADCAST_MANAGE_CLOUD_APP);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, BroadcastManageCloudAppConfigure.TABLE+"/#", TYPE_BROADCAST_MANAGE_CLOUD_APP_ID);
        sMatcher.addURI(PowerSaverConfigure.AUTHORITY, BroadcastManageCloudAppConfigure.TABLE+"/*", TYPE_BROADCAST_MANAGE_CLOUD_APP_PKG);
    }

    @Override
    public boolean onCreate() {
        removeObsoleteDatabase();
        mContext = getContext();
        mCloudHelper = new CloudDatabaseHelper(getContext());

        return true;
    }

    public PowerSaverConfigureProvider() {
        super();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        Cursor c = null;
        SQLiteDatabase db = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
                selection = DatabaseUtils.concatenateWhere(
                        GlobalFeatureConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
            case TYPE_CLOUD_APP_PKG:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.PACKAGE_NAME + " = '" + uri.getLastPathSegment() + "'", selection);
                break;
            case TYPE_CLOUD_APP_ID:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
//            case TYPE_USER_APP_ID:
//                selection = DatabaseUtils.concatenateWhere(
//                        UserConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
//                break;
//            case TYPE_APP_ACTIVE_UID:
//                selection = DatabaseUtils.concatenateWhere(
//                        AppActiveConfigure.Columns.UID + " = " + ContentUris.parseId(uri), selection);
//                break;
            case TYPE_BROADCAST_MANAGE_GLOBAL_ID:
                selection = DatabaseUtils.concatenateWhere(
                        BroadcastManageGlobalFeatureConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
            case TYPE_BROADCAST_MANAGE_CLOUD_APP_PKG:
                selection = DatabaseUtils.concatenateWhere(
                        BroadcastManageCloudAppConfigure.Columns.PACKAGE_NAME + " = '" + uri.getLastPathSegment() + "'", selection);
                break;
            case TYPE_BROADCAST_MANAGE_CLOUD_APP_ID:
                selection = DatabaseUtils.concatenateWhere(
                        BroadcastManageCloudAppConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
            default:
                break;
        }

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
            case TYPE_GLOBAL:
                db = mCloudHelper.getReadableDatabase();
                qb.setTables(GlobalFeatureConfigure.TABLE);
                break;
            case TYPE_CLOUD_APP_PKG:
            case TYPE_CLOUD_APP_ID:
            case TYPE_CLOUD_APP:
                db = mCloudHelper.getReadableDatabase();
                qb.setTables(CloudAppConfigure.TABLE);
                break;
            case TYPE_USER_APP_ID:
//            case TYPE_USER_APP:
//                db = mUserHelper.getReadableDatabase();
//                qb.setTables(UserConfigure.TABLE);
//                break;
//            case TYPE_APP_ACTIVE_UID:
//            case TYPE_APP_ACTIVE:
//                db = mInterfaceDatabaseHelper.getReadableDatabase();
//                qb.setTables(AppActiveConfigure.TABLE);
//                break;
            case TYPE_BROADCAST_MANAGE_GLOBAL:
            case TYPE_BROADCAST_MANAGE_GLOBAL_ID:
                db = mCloudHelper.getReadableDatabase();
                qb.setTables(BroadcastManageGlobalFeatureConfigure.TABLE);
                break;
            case TYPE_BROADCAST_MANAGE_CLOUD_APP:
            case TYPE_BROADCAST_MANAGE_CLOUD_APP_PKG:
            case TYPE_BROADCAST_MANAGE_CLOUD_APP_ID:
                db = mCloudHelper.getReadableDatabase();
                qb.setTables(BroadcastManageCloudAppConfigure.TABLE);
                break;
            default:
                return null;
        }
        c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = null;
        long insertedId = 0;
        Uri notifyUri = uri;
        String pkg = null;
        ContentValues timeVal = null;
        String timeSelection = null;

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update global table
                insertedId = db.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (DEBUG) Log.d(TAG, GlobalFeatureConfigure.TABLE + " insert " + insertedId);
                notifyUri = ContentUris.withAppendedId(uri, insertedId);
                break;
            case TYPE_CLOUD_APP:
                db = mCloudHelper.getWritableDatabase();
                pkg = values.getAsString(CloudAppConfigure.Columns.PACKAGE_NAME);
                if (pkg == null) {
                    throw new IllegalArgumentException("Missed " + CloudAppConfigure.Columns.PACKAGE_NAME);
                }
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update app table
                insertedId = db.insertWithOnConflict(CloudAppConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (DEBUG) Log.d(TAG, CloudAppConfigure.TABLE + " insert " + insertedId);
                notifyUri = Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, pkg);
                break;
//            case TYPE_USER_APP:
//                db = mUserHelper.getWritableDatabase();
//                Integer userId = values.getAsInteger(UserConfigure.Columns.USER_ID);
//                if (userId == null) {
//                    throw new IllegalArgumentException("Missed " + UserConfigure.Columns.USER_ID);
//                }
//                pkg = values.getAsString(UserConfigure.Columns.PACKAGE_NAME);
//                if (pkg == null) {
//                    throw new IllegalArgumentException("Missed " + UserConfigure.Columns.PACKAGE_NAME);
//                }
//                values.put(UserConfigure.Columns.LAST_CONFIGURED, System.currentTimeMillis());
//                insertedId = db.insertWithOnConflict(UserConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                if (DEBUG) Log.d(TAG, UserConfigure.TABLE + " insert " + insertedId);
//                notifyUri = ContentUris.withAppendedId(UserConfigure.CONTENT_URI, insertedId);
//                break;
//            case TYPE_APP_ACTIVE:
//                db = mInterfaceDatabaseHelper.getWritableDatabase();
//                Integer uid = values.getAsInteger(AppActiveConfigure.Columns.UID);
//                if (uid == null) {
//                    throw new IllegalArgumentException("Missed " + AppActiveConfigure.Columns.UID);
//                }
//                insertedId = db.insertWithOnConflict(AppActiveConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                if (DEBUG) Log.d(TAG, AppActiveConfigure.TABLE + " insert " + insertedId);
//                notifyUri = ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, uid);
//                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(notifyUri, null);
        return notifyUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesArray) {

        SQLiteDatabase db = null;
        Uri notifyUri = uri;
        ContentValues timeVal = null;
        String timeSelection = null;
        int numValues = valuesArray.length;

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update global table
                db.beginTransaction();
                try {
                    for (int i = 0; i < numValues; i++) {
                        db.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (DEBUG) Log.d(TAG, GlobalFeatureConfigure.TABLE + " bulkInsert");
                break;
            case TYPE_CLOUD_APP:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update app table
                db.beginTransaction();
                try {
                    for (int i = 0; i < numValues; i++) {
                        db.insertWithOnConflict(CloudAppConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (DEBUG) Log.d(TAG, CloudAppConfigure.TABLE + " bulkInsert");
                break;
//            case TYPE_USER_APP:
//                db = mUserHelper.getWritableDatabase();
//                db.beginTransaction();
//                try {
//                    for (int i = 0; i < numValues; i++) {
//                        valuesArray[i].put(UserConfigure.Columns.LAST_CONFIGURED, System.currentTimeMillis());
//                        db.insertWithOnConflict(UserConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
//                    }
//                    db.setTransactionSuccessful();
//                } finally {
//                    db.endTransaction();
//                }
//                if (DEBUG) Log.d(TAG, UserConfigure.TABLE + " bulkInsert");
//                break;
//            case TYPE_APP_ACTIVE:
//                db = mInterfaceDatabaseHelper.getWritableDatabase();
//                db.beginTransaction();
//                try {
//                    for (int i = 0; i < numValues; i++) {
//                        db.insertWithOnConflict(AppActiveConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
//                    }
//                    db.setTransactionSuccessful();
//                } finally {
//                    db.endTransaction();
//                }
//                if (DEBUG) Log.d(TAG, AppActiveConfigure.TABLE + " bulkInsert");
//                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(notifyUri, null);
        return numValues;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        String[] rowPackageList = null;
        int rowsAffected = 0;
        SQLiteDatabase db = null;
        ContentValues timeVal = null;
        String timeSelection = null;
        Cursor cursor = null;

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
                selection = DatabaseUtils.concatenateWhere(
                        GlobalFeatureConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
            case TYPE_CLOUD_APP_PKG:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.PACKAGE_NAME + " = '" + uri.getLastPathSegment() + "'", selection);
                break;
            case TYPE_CLOUD_APP_ID:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
//            case TYPE_USER_APP_ID:
//                selection = DatabaseUtils.concatenateWhere(
//                        UserConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
//                break;
//            case TYPE_APP_ACTIVE_UID:
//                selection = DatabaseUtils.concatenateWhere(
//                        AppActiveConfigure.Columns.UID + " = " + ContentUris.parseId(uri), selection);
//                break;
            default:
                break;
        }

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
            case TYPE_GLOBAL:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update global table
                rowsAffected = db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
                getContext().getContentResolver().notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
                break;
            case TYPE_CLOUD_APP_PKG:
            case TYPE_CLOUD_APP_ID:
            case TYPE_CLOUD_APP:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update app table
                cursor = db.query(CloudAppConfigure.TABLE, new String[] {
                        CloudAppConfigure.Columns.PACKAGE_NAME},
                        selection, selectionArgs, null, null, null);
                if (cursor != null) {
                    rowPackageList = getPackageList(cursor, CloudAppConfigure.Columns.PACKAGE_NAME);
                    cursor.close();
                }
                rowsAffected = db.updateWithOnConflict(CloudAppConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowPackageList != null) {
                    for (String pkg : rowPackageList) {
                        getContext().getContentResolver().notifyChange(
                                Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, pkg), null);
                    }
                }
                break;
//            case TYPE_USER_APP_ID:
//            case TYPE_USER_APP:
//                // hook: for info collect
//                String pkgn = values.getAsString(UserConfigure.Columns.PACKAGE_NAME);
//                String oldControl = UserConfigureHelper.getUserConfigureHelperByPkg(getContext(), pkgn).getBgControl();
//                String bgControl = values.getAsString(UserConfigure.Columns.BG_CONTROL);
//                InfoCollectManager.getInstance(getContext()).appSettingsChanged(pkgn, oldControl, bgControl);
//                Log.d(TAG, "hook: for info collect pkg = " + pkgn + ", oldControl = " + oldControl + ", bgControl = " + bgControl);
//                db = mUserHelper.getWritableDatabase();
//                cursor = db.query(UserConfigure.TABLE, new String[] {
//                        UserConfigure.Columns.ID}, selection, selectionArgs, null, null, null);
//                long[] rowIdList = null;
//                if (cursor != null) {
//                    rowIdList = getIdList(cursor, UserConfigure.Columns.ID);
//                    cursor.close();
//                }
//                values.put(UserConfigure.Columns.LAST_CONFIGURED, System.currentTimeMillis());
//                rowsAffected = db.updateWithOnConflict(UserConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
//                if (rowIdList != null) {
//                    for (long id : rowIdList) {
//                        getContext().getContentResolver().notifyChange(
//                                ContentUris.withAppendedId(UserConfigure.CONTENT_URI, id), null);
//                    }
//                }
//                break;
//            case TYPE_APP_ACTIVE_UID:
//            case TYPE_APP_ACTIVE:
//                db = mInterfaceDatabaseHelper.getWritableDatabase();
//                cursor = db.query(AppActiveConfigure.TABLE, new String[] {
//                        AppActiveConfigure.Columns.UID}, selection, selectionArgs, null, null, null);
//                int[] rowUidList = null;
//                if (cursor != null) {
//                    rowUidList = new int[cursor.getCount()];
//                    int uidIndex = cursor.getColumnIndex(AppActiveConfigure.Columns.UID);
//                    int i = 0;
//                    while (cursor.moveToNext()) {
//                        rowUidList[i++] = cursor.getInt(uidIndex);
//                    }
//                    cursor.close();
//                }
//                rowsAffected = db.updateWithOnConflict(AppActiveConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
//                if (rowUidList != null) {
//                    for (int uid : rowUidList) {
//                        getContext().getContentResolver().notifyChange(
//                                ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, uid), null);
//                    }
//                }
//                break;
            default:
                break;
        }
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String[] rowPackageList = null;
        int rowsAffected = 0;
        SQLiteDatabase db = null;
        ContentValues timeVal = null;
        Cursor cursor = null;

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
                selection = DatabaseUtils.concatenateWhere(
                        GlobalFeatureConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
            case TYPE_CLOUD_APP_PKG:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.PACKAGE_NAME + " = '" + uri.getLastPathSegment() + "'", selection);
                break;
            case TYPE_CLOUD_APP_ID:
                selection = DatabaseUtils.concatenateWhere(
                        CloudAppConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
                break;
//            case TYPE_USER_APP_ID:
//                selection = DatabaseUtils.concatenateWhere(
//                        UserConfigure.Columns.ID + " = " + ContentUris.parseId(uri), selection);
//                break;
//            case TYPE_APP_ACTIVE_UID:
//                selection = DatabaseUtils.concatenateWhere(
//                        AppActiveConfigure.Columns.UID + " = " + ContentUris.parseId(uri), selection);
//                break;
            default:
                break;
        }

        switch (sMatcher.match(uri)) {
            case TYPE_GLOBAL_ID:
            case TYPE_GLOBAL:
                db = mCloudHelper.getWritableDatabase();
                // Update global table
                db.delete(GlobalFeatureConfigure.TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
                break;
            case TYPE_CLOUD_APP_PKG:
            case TYPE_CLOUD_APP_ID:
            case TYPE_CLOUD_APP:
                db = mCloudHelper.getWritableDatabase();
                // Update LAST_UPDATED column
                timeVal = new ContentValues();
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                String timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
                // Update app table
                cursor = db.query(CloudAppConfigure.TABLE, new String[] {
                        CloudAppConfigure.Columns.PACKAGE_NAME},
                        selection, selectionArgs, null, null, null);
                if (cursor != null) {
                    rowPackageList = getPackageList(cursor, CloudAppConfigure.Columns.PACKAGE_NAME);
                    cursor.close();
                }
                rowsAffected = db.delete(CloudAppConfigure.TABLE, selection, selectionArgs);
                if (rowPackageList != null) {
                    for (String pkg : rowPackageList) {
                        getContext().getContentResolver().notifyChange(
                                Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, pkg), null);
                    }
                }
                break;
//            case TYPE_USER_APP_ID:
//            case TYPE_USER_APP:
//                db = mUserHelper.getWritableDatabase();
//                cursor = db.query(UserConfigure.TABLE, new String[] {
//                        UserConfigure.Columns.ID}, selection, selectionArgs, null, null, null);
//                long[] rowIdList = null;
//                if (cursor != null) {
//                    rowIdList = getIdList(cursor, UserConfigure.Columns.ID);
//                    cursor.close();
//                }
//                rowsAffected = db.delete(UserConfigure.TABLE, selection, selectionArgs);
//                if (rowIdList != null) {
//                    for (long id : rowIdList) {
//                        getContext().getContentResolver().notifyChange(
//                                ContentUris.withAppendedId(UserConfigure.CONTENT_URI, id), null);
//                    }
//                }
//                break;
//            case TYPE_APP_ACTIVE_UID:
//            case TYPE_APP_ACTIVE:
//                db = mInterfaceDatabaseHelper.getWritableDatabase();
//                cursor = db.query(AppActiveConfigure.TABLE, new String[] {
//                        AppActiveConfigure.Columns.UID}, selection, selectionArgs, null, null, null);
//                int[] rowUidList = null;
//                if (cursor != null) {
//                    rowUidList = new int[cursor.getCount()];
//                    int uidIndex = cursor.getColumnIndex(AppActiveConfigure.Columns.UID);
//                    int i = 0;
//                    while (cursor.moveToNext()) {
//                        rowUidList[i++] = cursor.getInt(uidIndex);
//                    }
//                    cursor.close();
//                }
//                rowsAffected = db.delete(AppActiveConfigure.TABLE, selection, selectionArgs);
//                if (rowUidList != null) {
//                    for (int uid : rowUidList) {
//                        getContext().getContentResolver().notifyChange(
//                                ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, uid), null);
//                    }
//                }
//                break;
            default:
                break;
        }
        return rowsAffected;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {

        Bundle bundle = null;
        SQLiteDatabase db = null;
        if (method.equals(GlobalFeatureConfigure.Method.METHOD_QUERY)) {
            db = mCloudHelper.getReadableDatabase();
            int callingUserId;
            bundle = new Bundle();
            Cursor cursor = db.query(GlobalFeatureConfigure.TABLE, null, null, null, null, null, null);
            if (cursor != null) {
                int userIdIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.USER_ID);
                int keyIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_NAME);
                int valueIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM);
                while (cursor.moveToNext()) {
                    String key = cursor.getString(keyIndex);
                    String value = cursor.getString(valueIndex);
                    if (key.equals(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                    } else {
                        bundle.putString(key, value);
                    }
                }
                cursor.close();
            }
        } else if (method.equals(GlobalFeatureConfigure.Method.METHOD_INSERT)) {
            if (extras == null) return null;
            db = mCloudHelper.getWritableDatabase();
            extras.remove(GlobalFeatureConfigure.LAST_UPDATED);
            ContentValues values = new ContentValues();
            if (extras.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS));
                db.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                for (String key : extras.keySet()) {
                    values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, key);
                    values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(key));
                    db.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                String selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, null, SQLiteDatabase.CONFLICT_REPLACE);
            }
            getContext().getContentResolver().notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (method.equals(GlobalFeatureConfigure.Method.METHOD_UPDATE)) {
            if (extras == null) return null;
            db = mCloudHelper.getWritableDatabase();
            extras.remove(GlobalFeatureConfigure.LAST_UPDATED);
            ContentValues values = new ContentValues();
            String selection = null;
            String[] selectionArgs = null;
            if (extras.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ? AND " +
                        GlobalFeatureConfigure.Columns.USER_ID + " = ?";
                selectionArgs = new String[] {
                        GlobalFeatureConfigure.USER_CONFIGURE_STATUS,
                        Integer.toString(1)
                };

                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS));
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                for (String key : extras.keySet()) {
                    selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + key + "'";
                    values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, key);
                    values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(key));
                    db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, null, SQLiteDatabase.CONFLICT_REPLACE);
                }
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, null, SQLiteDatabase.CONFLICT_REPLACE);
            }
            getContext().getContentResolver().notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (method.equals(GlobalFeatureConfigure.Method.METHOD_DELETE)) {
            if (extras == null) return null;
            db = mCloudHelper.getWritableDatabase();

            extras.remove(GlobalFeatureConfigure.LAST_UPDATED);
            ContentValues values = new ContentValues();
            String selection = null;
            String[] selectionArgs = null;
            if (extras.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ? AND " +
                        GlobalFeatureConfigure.Columns.USER_ID + " = ?";
                selectionArgs = new String[] {
                        GlobalFeatureConfigure.USER_CONFIGURE_STATUS,
                        Integer.toString(1)
                };
                db.delete(GlobalFeatureConfigure.TABLE, selection, selectionArgs);
            } else {
                for (String key : extras.keySet()) {
                    selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + key + "'";
                    db.delete(GlobalFeatureConfigure.TABLE, selection, selectionArgs);
                }
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
                values.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
                selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
                db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, values, selection, null, SQLiteDatabase.CONFLICT_REPLACE);
            }
            getContext().getContentResolver().notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (method.equals(CloudAppConfigure.Method.METHOD_OVERRIDE)) {
            db = mCloudHelper.getWritableDatabase();
            // Update LAST_UPDATED column
            ContentValues timeVal = new ContentValues();
            timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_NAME, GlobalFeatureConfigure.LAST_UPDATED);
            timeVal.put(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM, String.valueOf(System.currentTimeMillis()));
            String timeSelection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = '" + GlobalFeatureConfigure.LAST_UPDATED + "'";
            db.updateWithOnConflict(GlobalFeatureConfigure.TABLE, timeVal, timeSelection, null, SQLiteDatabase.CONFLICT_REPLACE);
            // Clear App Table
            db.delete(CloudAppConfigure.TABLE, null, null);
            // Fill App Table
            if (extras == null) {
                getContext().getContentResolver().notifyChange(CloudAppConfigure.CONTENT_URI, null);
                return null;
            }
            Parcelable[] parcelableArray = extras.getParcelableArray(CloudAppConfigure.Method.METHOD_OVERRIDE);
            if (parcelableArray != null) {
                ContentValues[] valuesArray = new ContentValues[parcelableArray.length];
                for (int index = 0; index < parcelableArray.length; index ++) {
                    valuesArray[index] = (ContentValues) parcelableArray[index];
                }
                int numValues = valuesArray.length;
                // Update app table
                db.beginTransaction();
                try {
                    for (int i = 0; i < numValues; i++) {
                        db.insertWithOnConflict(CloudAppConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
            getContext().getContentResolver().notifyChange(CloudAppConfigure.CONTENT_URI, null);
        }
//        else if (method.equals(UserConfigure.Method.METHOD_UPDATE)) {
//            long id = -1;
//            int callingUserId;
//            if (extras != null && extras.containsKey(UserConfigure.Columns.USER_ID)) {
//                callingUserId = extras.getInt(UserConfigure.Columns.USER_ID);
//                extras.remove(UserConfigure.Columns.USER_ID);
//            } else {
//                callingUserId = UserHandle.getCallingUserId();
//            }
//            if (DEBUG) Log.d(TAG, "call, callingUserId = " + callingUserId);
//            db = mUserHelper.getWritableDatabase();
//            if (extras == null) {
//                return null;
//            }
//            ContentValues values = new ContentValues();
//            values.put(UserConfigure.Columns.USER_ID, callingUserId);
//            String pkg = extras.getString(UserConfigure.Columns.PACKAGE_NAME);
//            if (pkg == null) {
//                throw new IllegalArgumentException("Missed " + UserConfigure.Columns.PACKAGE_NAME);
//            }
//            values.put(UserConfigure.Columns.PACKAGE_NAME, pkg);
//            String bgControl = extras.getString(UserConfigure.Columns.BG_CONTROL);
//            if (bgControl == null) {
//                throw new IllegalArgumentException("Missed " + UserConfigure.Columns.BG_CONTROL);
//            }
//            values.put(UserConfigure.Columns.BG_CONTROL, bgControl);
//            {
//                String selection =
//                        UserConfigure.Columns.USER_ID + " = ? AND " +
//                        UserConfigure.Columns.PACKAGE_NAME + " = ?";
//                String[] selectionArgs = new String[] {
//                        Integer.toString(callingUserId),
//                        pkg
//                };
//                Cursor cursor = db.query(UserConfigure.TABLE, new String[] {
//                        UserConfigure.Columns.ID}, selection, selectionArgs, null, null, null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    int idIndex = cursor.getColumnIndex(UserConfigure.Columns.ID);
//                    if (! cursor.isNull(idIndex)) {
//                        id = cursor.getLong(idIndex);
//                    }
//                }
//                if (cursor != null) {
//                    cursor.close();
//                }
//                if (id == -1) {
//                    Log.e(TAG, "Missed " + UserConfigure.Columns.ID + ", callingUserId = " + callingUserId + ", pkgName = " + pkg);
//                    return null;
//                }
//            }
//            String bgLocation = extras.getString(UserConfigure.Columns.BG_LOCATION);
//            if (bgLocation == null) {
//                String[] columns = {UserConfigure.Columns.BG_LOCATION};
//                String selection =
//                        UserConfigure.Columns.USER_ID + " = ? AND " +
//                        UserConfigure.Columns.PACKAGE_NAME + " = ?";
//                String[] selectionArgs = new String[] {
//                        Integer.toString(callingUserId),
//                        pkg
//                };
//                Cursor cursor = db.query(UserConfigure.TABLE, columns, selection, selectionArgs, null, null, null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    int bgLocationIndex = cursor.getColumnIndex(UserConfigure.Columns.BG_LOCATION);
//                    if (! cursor.isNull(bgLocationIndex)) {
//                        bgLocation = cursor.getString(bgLocationIndex);
//                    } else {
//                        if (DEBUG) Log.d(TAG, "User Table: packageName = " + pkg + ", bgLocation = null");
//                    }
//                }
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//            if (bgLocation != null) {
//                values.put(UserConfigure.Columns.BG_LOCATION, bgLocation);
//            } else if (bgControl.equals(UserConfigure.BG_CONTROL_NO_RESTRICT)) {
//                if (PackageUtil.isSystemApp(getContext(), pkg, callingUserId)) {
//                    bgLocation = UserConfigure.BG_LOCATION_ENABLE;
//                } else {
//                    SQLiteDatabase cloudDb = mCloudHelper.getWritableDatabase();
//                    String[] columns = {CloudAppConfigure.Columns.BG_LOCATION, CloudAppConfigure.Columns.BG_LOCATION_DELAY_TIME};
//                    String selection = CloudAppConfigure.Columns.PACKAGE_NAME + " = ? ";
//                    String[] selectionArgs = {pkg};
//                    Cursor cursor = cloudDb.query(CloudAppConfigure.TABLE, columns, selection, selectionArgs, null, null, null);
//                    if (cursor != null && cursor.getCount() > 0) {
//                        cursor.moveToFirst();
//                        int bgLocationIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_LOCATION);
//                        int bgLocationDelayTimeIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_LOCATION_DELAY_TIME);
//                        if (! cursor.isNull(bgLocationIndex)) {
//                            if (cursor.getString(bgLocationIndex).equals(CloudAppConfigure.BG_LOCATION_ENABLE)) {
//                                bgLocation = UserConfigure.BG_LOCATION_ENABLE;
//                            } else if (! cursor.isNull(bgLocationDelayTimeIndex)) {
//                                if (cursor.getInt(bgLocationDelayTimeIndex) == -1) {
//                                    bgLocation = UserConfigure.BG_LOCATION_ENABLE;
//                                } else {
//                                    bgLocation = UserConfigure.BG_LOCATION_DISABLE;
//                                }
//                            } else {
//                                bgLocation = UserConfigure.BG_LOCATION_DISABLE;
//                            }
//                            if (DEBUG) Log.d(TAG, "CloudApp Table: packageName = " + pkg + ", bgLocation = " + bgLocation);
//                        }
//                    }
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                    if (bgLocation == null) {
//                        String sel = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ? OR " + GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ? ";
//                        String selArgs[] = {GlobalFeatureConfigure.BG_LOCATION,
//                                GlobalFeatureConfigure.BG_LOCATION_DELAY_TIME};
//                        Cursor c = cloudDb.query(GlobalFeatureConfigure.TABLE, null, sel, selArgs, null, null, null);
//                        while (c != null && c.moveToNext()) {
//                            int keyIndex = c.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_NAME);
//                            int valueIndex = c.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM);
//                            String key = c.getString(keyIndex);
//                            String value = c.getString(valueIndex);
//                            if (key.equals(GlobalFeatureConfigure.BG_LOCATION)) {
//                                if (value.equals(GlobalFeatureConfigure.BG_LOCATION_ENABLE)) {
//                                    bgLocation = UserConfigure.BG_LOCATION_ENABLE;
//                                } else {
//                                    bgLocation = UserConfigure.BG_LOCATION_DISABLE;
//                                }
//                                if (DEBUG) Log.d(TAG, "CloudGlobal Table: packageName = " + pkg + ", bgLocation = " + bgLocation);
//                                break;
//                            }
//                        }
//                        if (c != null) {
//                            c.close();
//                        }
//                    }
//                    if (bgLocation == null) {
//                        bgLocation = UserConfigure.BG_LOCATION_ENABLE;
//                    }
//                }
//                values.put(UserConfigure.Columns.BG_LOCATION, bgLocation);
//            }
//            values.put(UserConfigure.Columns.LAST_CONFIGURED, System.currentTimeMillis());
//            String whereClause =
//                    UserConfigure.Columns.USER_ID + " = ? AND " +
//                    UserConfigure.Columns.PACKAGE_NAME + " = ?";
//            String[] whereArgs = new String[] {
//                    Integer.toString(callingUserId),
//                    pkg
//            };
//            db.update(UserConfigure.TABLE, values, whereClause, whereArgs);
//            getContext().getContentResolver().notifyChange(
//                    ContentUris.withAppendedId(UserConfigure.CONTENT_URI, id), null);
//        }
        else if (method.equals(BroadcastManageCloudAppConfigure.Method.METHOD_OVERRIDE)) {
            db = mCloudHelper.getWritableDatabase();
            // Clear BC Manage Cloud App Table
            db.delete(BroadcastManageCloudAppConfigure.TABLE, null, null);
            // Fill App Table
            if (extras == null) {
                getContext().getContentResolver().notifyChange(BroadcastManageCloudAppConfigure.CONTENT_URI, null);
                return null;
            }
            Parcelable[] parcelableArray = extras.getParcelableArray(BroadcastManageCloudAppConfigure.Method.METHOD_OVERRIDE);
            if (parcelableArray != null) {
                ContentValues[] valuesArray = new ContentValues[parcelableArray.length];
                for (int index = 0; index < parcelableArray.length; index ++) {
                    valuesArray[index] = (ContentValues) parcelableArray[index];
                }
                int numValues = valuesArray.length;
                // Update app table
                db.beginTransaction();
                try {
                    for (int i = 0; i < numValues; i++) {
                        db.insertWithOnConflict(BroadcastManageCloudAppConfigure.TABLE, null, valuesArray[i], SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
            getContext().getContentResolver().notifyChange(BroadcastManageCloudAppConfigure.CONTENT_URI, null);
        } else if (method.equals(BroadcastManageGlobalFeatureConfigure.Method.METHOD_UPDATE)) {
            if (extras == null) return null;
            db = mCloudHelper.getWritableDatabase();

            //extras.remove(BroadcastManageGlobalFeatureConfigure.LAST_UPDATED);
            ContentValues values = new ContentValues();
            String selection = null;
            String[] selectionArgs = null;

            if (extras.containsKey(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS)) {
                selection = BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ?";
                selectionArgs = new String[] {
                        BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS
                };
                values.put(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME, BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS);
                values.put(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS));
                db.updateWithOnConflict(BroadcastManageGlobalFeatureConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
            }
            if (extras.containsKey(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY)) {
                selection = BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ?";
                selectionArgs = new String[] {
                        BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY
                };
                values.put(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME, BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY);
                values.put(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_PARAM, extras.getString(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY));
                db.updateWithOnConflict(BroadcastManageGlobalFeatureConfigure.TABLE, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
            }
            getContext().getContentResolver().notifyChange(BroadcastManageGlobalFeatureConfigure.CONTENT_URI, null);
        } else if (method.equals(BroadcastManageGlobalFeatureConfigure.Method.METHOD_QUERY)) {
            db = mCloudHelper.getReadableDatabase();
            bundle = new Bundle();
            Cursor cursor = db.query(BroadcastManageGlobalFeatureConfigure.TABLE, null, null, null, null, null, null);
            if (cursor != null) {
                int keyIndex = cursor.getColumnIndex(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_NAME);
                int valueIndex = cursor.getColumnIndex(BroadcastManageGlobalFeatureConfigure.Columns.CONFIGURE_PARAM);
                while (cursor.moveToNext()) {
                    String key = cursor.getString(keyIndex);
                    if (key.equals(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS)) {
                        String status = cursor.getString(valueIndex);
                        bundle.putString(key, status);
                    }
                    if (key.equals(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY)) {
                        int delay = cursor.getInt(valueIndex);
                        bundle.putInt(key, delay);
                    }
                }
                cursor.close();
            }
        }
        return bundle;
    }

    private String getPackage(Cursor cursor, String packageColumn) {
        int packageIndex = cursor.getColumnIndex(packageColumn);
        String result = cursor.getString(packageIndex);
        return result;
    }

    private long[] getIdList(Cursor cursor, String idColumn) {
        long[] result = new long[cursor.getCount()];
        int idIndex = cursor.getColumnIndex(idColumn);
        int i = 0;
        while (cursor.moveToNext()) {
            result[i++] = cursor.getLong(idIndex);
        }
        return result;
    }

    private String[] getPackageList(Cursor cursor, String packageColumn) {
        String[] result = new String[cursor.getCount()];
        int packageIndex = cursor.getColumnIndex(packageColumn);
        int i = 0;
        while (cursor.moveToNext()) {
            result[i++] = cursor.getString(packageIndex);
        }
        return result;
    }

    private void removeObsoleteDatabase() {
//        String obsoleteFilePath = "/data/data/com.shane.powersaver/databases/hidden_mode_cloud.db";
//        String obsoleteFileJournalPath = "/data/data/com.shane.powersaver/databases/hidden_mode_cloud.db-journal";
//        File obsoleteFile = new File(obsoleteFilePath);
//        if (obsoleteFile.exists()) {
//            if (obsoleteFile.delete()) {
//                Log.e(TAG, obsoleteFilePath + " delete fail");
//            }
//            if ((new File(obsoleteFileJournalPath)).delete()) {
//                Log.e(TAG, obsoleteFileJournalPath + " delete fail");
//            }
//        }
//        obsoleteFilePath = "/data/data/com.shane.powersaver/databases/powerkeeper_interface.db";
//        obsoleteFileJournalPath = "/data/data/com.shane.powersaver/databases/powerkeeper_interface.db-journal";
//        obsoleteFile = new File(obsoleteFilePath);
//        if (obsoleteFile.exists()) {
//            if (obsoleteFile.delete()) {
//                Log.e(TAG, obsoleteFilePath + " delete fail");
//            }
//            if ((new File(obsoleteFileJournalPath)).delete()) {
//                Log.e(TAG, obsoleteFileJournalPath + " delete fail");
//            }
//        }
    }
}
