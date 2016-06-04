package com.tests.apostol.testapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 5/27/2016.
 */
public class CardDatabaseHelper extends SQLiteOpenHelper {
    private static CardDatabaseHelper _instance;
    public static CardDatabaseHelper createAndGetInstance(Context c) {
        if (_instance == null)
            _instance = new CardDatabaseHelper(c);

        return _instance;
    }
    public static CardDatabaseHelper getInstance() {
        return _instance;
    }

    //<editor-fold desc="Database Constants">
    public static final String DATABASE_NAME = "WH40kConquest.db";
    private static final String INT_TYPE = " INTEGER";
    private static final String UNIQUE = " UNIQUE";
    private static final String NOT_NULL = " NOT NULL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String LOYALTY_CHECK = " IN ('NO', 'LO', 'SG'))";
    private static final String FACTION_CHECK = " IN ('AM', 'CH', 'DE', 'EL', 'NE', 'OR', 'SM', 'TA', 'TY', 'NA'))";
    private static final String TYPE_CHECK = " IN ('WR', 'AR', 'AT', 'SU', 'EV', 'SY'))";
    private static final String SQL_CREATE_CARD_LIST =
            "CREATE TABLE " + CardDbContract.CardListEntry.TABLE_NAME
                            + " ("
                            + CardDbContract.COL_ID + INT_TYPE + " PRIMARY KEY, "
                            + CardDbContract.CardListEntry.COL_NAME + TEXT_TYPE + UNIQUE + NOT_NULL + COMMA
                            + CardDbContract.CardListEntry.COL_FACTION + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + CardDbContract.CardListEntry.COL_FACTION + FACTION_CHECK + COMMA
                            + CardDbContract.CardListEntry.COL_LOYAL + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + CardDbContract.CardListEntry.COL_LOYAL + LOYALTY_CHECK + COMMA
                            + CardDbContract.CardListEntry.COL_TYPE + TEXT_TYPE + NOT_NULL
                                + " CHECK (" + CardDbContract.CardListEntry.COL_TYPE + TYPE_CHECK + COMMA
                            + CardDbContract.CardListEntry.COL_COST + INT_TYPE + COMMA
                            + CardDbContract.CardListEntry.COL_TEXT + TEXT_TYPE + COMMA
                            + CardDbContract.CardListEntry.COL_SET + TEXT_TYPE + NOT_NULL + COMMA
                            + CardDbContract.CardListEntry.COL_NUMBER + INT_TYPE + NOT_NULL
                            + ")";


    private static final String SQL_CREATE_UNITDEFS =
            "CREATE TABLE " + CardDbContract.UnitDefEntry.TABLE_NAME
                    + " ("
                    + CardDbContract.COL_ID + INT_TYPE + " PRIMARY KEY, "
                    + CardDbContract.UnitDefEntry.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + CardDbContract.UnitDefEntry.COL_ATK + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.UnitDefEntry.COL_LIFE + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.UnitDefEntry.COL_COM + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + CardDbContract.UnitDefEntry.COL_CARDID + ") REFERENCES "
                        + CardDbContract.CardListEntry.TABLE_NAME + "(" + CardDbContract.COL_ID + ")"
                    + ")";

    private static final String SQL_CREATE_WARDEFS =
            "CREATE TABLE " + CardDbContract.WarlordDefEntry.TABLE_NAME
                    + " ("
                    + CardDbContract.COL_ID + INT_TYPE + " PRIMARY KEY, "
                    + CardDbContract.WarlordDefEntry.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_HAND + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_RES + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_ATK + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_BATK + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_LIFE + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_BLIFE + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.WarlordDefEntry.COL_BTXT + TEXT_TYPE + COMMA
                    + " FOREIGN KEY (" + CardDbContract.WarlordDefEntry.COL_CARDID + ") REFERENCES "
                    + CardDbContract.CardListEntry.TABLE_NAME + "(" + CardDbContract.COL_ID + ")"
                    + ")";

    private static final String SQL_CREATE_SHIELDDEFS =
            "CREATE TABLE " + CardDbContract.ShieldsDefEntry.TABLE_NAME
                    + " ("
                    + CardDbContract.COL_ID + INT_TYPE + " PRIMARY KEY, "
                    + CardDbContract.ShieldsDefEntry.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + CardDbContract.ShieldsDefEntry.COL_SHIELDS + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + CardDbContract.ShieldsDefEntry.COL_CARDID + ") REFERENCES "
                    + CardDbContract.CardListEntry.TABLE_NAME + "(" + CardDbContract.COL_ID + ")"
                    + ")";

    private static final String SQL_CREATE_SQUADDEFS =
            "CREATE TABLE " + CardDbContract.SquadDefEntry.TABLE_NAME
                    + " ("
                    + CardDbContract.COL_ID + INT_TYPE + " PRIMARY KEY, "
                    + CardDbContract.SquadDefEntry.COL_WARID + INT_TYPE + NOT_NULL + COMMA
                    + CardDbContract.SquadDefEntry.COL_CARDID + INT_TYPE + UNIQUE + NOT_NULL + COMMA
                    + CardDbContract.SquadDefEntry.COL_QTY + INT_TYPE + NOT_NULL + COMMA
                    + " FOREIGN KEY (" + CardDbContract.SquadDefEntry.COL_WARID + ") REFERENCES " + CardDbContract.WarlordDefEntry.TABLE_NAME + "(" + CardDbContract.WarlordDefEntry.COL_CARDID + ")"
                    + ","
                    + " FOREIGN KEY (" + CardDbContract.SquadDefEntry.COL_CARDID + ") REFERENCES " + CardDbContract.CardListEntry.TABLE_NAME + "(" + CardDbContract.COL_ID + ")"
                    + ")";
    //</editor-fold>

    private CardDatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CARD_LIST);
        db.execSQL(SQL_CREATE_SHIELDDEFS);
        db.execSQL(SQL_CREATE_UNITDEFS);
        db.execSQL(SQL_CREATE_WARDEFS);
        db.execSQL(SQL_CREATE_SQUADDEFS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + CardDbContract.SquadDefEntry.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + CardDbContract.ShieldsDefEntry.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + CardDbContract.WarlordDefEntry.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + CardDbContract.UnitDefEntry.TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + CardDbContract.CardListEntry.TABLE_NAME);
        onCreate(db);
    }
}
