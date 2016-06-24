package com.tests.apostol.conquest.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConquestDbHelper extends SQLiteOpenHelper {
    public ConquestDbHelper(Context c) {
        super(c, DATABASE_NAME, null, 1);
    }

    //<editor-fold desc="Database Constants">
    public static final String DATABASE_NAME = "WH40kConquest.db";

    private static final String INT_TYPE = " INTEGER";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOT_NULL = " NOT NULL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String LOYALTY_CHECK = " IN ('NO', 'LO', 'SG'))";
    private static final String FACTION_CHECK = " IN ('AM', 'CH', 'DE', 'EL', 'NE', 'OS', 'SM', 'TA', 'TY', 'NA'))";
    private static final String TYPE_CHECK = " IN ('WR', 'AR', 'AT', 'SU', 'EV', 'SY'))";
    private static final String SQL_CREATE_CARD_LIST =
            "CREATE TABLE " + DbContract.Cards.TABLE
                            + " ("
                            + DbContract._ID + INT_TYPE + " PRIMARY KEY, "
                            + DbContract.Cards.NAME + TEXT_TYPE + UNIQUE + NOT_NULL + COMMA
                            + DbContract.Cards.FACTION + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + DbContract.Cards.FACTION + FACTION_CHECK + COMMA
                            + DbContract.Cards.LOYAL + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + DbContract.Cards.LOYAL + LOYALTY_CHECK + COMMA
                            + DbContract.Cards.TYPE + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + DbContract.Cards.TYPE + TYPE_CHECK + COMMA
                            + DbContract.Cards.COST + INT_TYPE + COMMA
                            + DbContract.Cards.TRAITS + TEXT_TYPE + NOT_NULL + COMMA
                            + DbContract.Cards.TEXT + TEXT_TYPE + COMMA
                            + DbContract.Cards.SET + TEXT_TYPE + NOT_NULL + COMMA
                            + DbContract.Cards.NUMBER + INT_TYPE + NOT_NULL
                            + ")";


    private static final String SQL_CREATE_UNITDEFS =
            "CREATE TABLE " + DbContract.Units.TABLE_NAME
                    + " ("
                    + DbContract._ID + INT_TYPE + " PRIMARY KEY, "
                    + DbContract.Units.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + DbContract.Units.ATK + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Units.LIFE + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Units.COM + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + DbContract.Units.COL_CARDID + ") REFERENCES "
                        + DbContract.Cards.TABLE + "(" + DbContract._ID + ")"
                    + ")";

    private static final String SQL_CREATE_WARDEFS =
            "CREATE TABLE " + DbContract.Warlords.TABLE
                    + " ("
                    + DbContract._ID + INT_TYPE + " PRIMARY KEY, "
                    + DbContract.Warlords.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + DbContract.Warlords.HAND + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.RES + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.ATK + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.LIFE + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.BATK + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.BLIFE + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Warlords.BTXT + TEXT_TYPE + COMMA
                    + " FOREIGN KEY (" + DbContract.Warlords.COL_CARDID + ") REFERENCES "
                    + DbContract.Cards.TABLE + "(" + DbContract._ID + ")"
                    + ")";

    private static final String SQL_CREATE_SHIELDDEFS =
            "CREATE TABLE " + DbContract.Shields.TABLE_NAME
                    + " ("
                    + DbContract._ID + INT_TYPE + " PRIMARY KEY, "
                    + DbContract.Shields.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + DbContract.Shields.COL_SHIELDS + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + DbContract.Shields.COL_CARDID + ") REFERENCES "
                    + DbContract.Cards.TABLE + "(" + DbContract._ID + ")"
                    + ")";

    private static final String SQL_CREATE_SQUADDEFS =
            "CREATE TABLE " + DbContract.Squads.TABLE
                    + " ("
                    + DbContract._ID + INT_TYPE + " PRIMARY KEY, "
                    + DbContract.Squads.WARID + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Squads.CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + DbContract.Squads.QTY + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + DbContract.Squads.WARID + ") REFERENCES " + DbContract.Warlords.TABLE + "(" + DbContract.Warlords.COL_CARDID + ")"
                    + ","
                    + " FOREIGN KEY (" + DbContract.Squads.CARDID + ") REFERENCES " + DbContract.Cards.TABLE + "(" + DbContract._ID + ")"
                    + ")";

    private static final String SQL_CREATE_PREAMBLES =
            "CREATE TABLE " + DbContract.Preambles.TABLE
                    + " ("
                    + DbContract.Preambles.NAME + TEXT_TYPE + " PRIMARY KEY, "
                    + DbContract.Preambles.DESC + TEXT_TYPE + COMMA
                    + DbContract.Preambles.WARID + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + DbContract.Preambles.WARID + ") REFERENCES " + DbContract.Warlords.TABLE + "(" + DbContract.Warlords.COL_CARDID + ")"
                    + ")";

    private static final String SQL_CREATE_CONTENTS =
            "CREATE TABLE " + DbContract.Contents.TABLE
                    + " ("
                    + DbContract.Contents.NAME + TEXT_TYPE + NOT_NULL + COMMA
                    + DbContract.Contents.CARDID + INT_TYPE + NOT_NULL + COMMA
                    + DbContract.Contents.QTY + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + DbContract.Contents.NAME + ") REFERENCES "
                    + DbContract.Preambles.TABLE + "(" + DbContract.Preambles.NAME + ")"
                    + ")";
    //</editor-fold>

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CARD_LIST);
        db.execSQL(SQL_CREATE_SHIELDDEFS);
        db.execSQL(SQL_CREATE_UNITDEFS);
        db.execSQL(SQL_CREATE_WARDEFS);
        db.execSQL(SQL_CREATE_SQUADDEFS);
        db.execSQL(SQL_CREATE_PREAMBLES);
        db.execSQL(SQL_CREATE_CONTENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Contents.TABLE);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Preambles.TABLE);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Squads.TABLE);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Shields.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Warlords.TABLE);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Units.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + DbContract.Cards.TABLE);
        onCreate(db);
    }
}
