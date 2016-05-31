package com.tests.apostol.testapp;

import android.provider.BaseColumns;

/**
 * Created by user on 5/27/2016.
 */
public final class CardDbContract {
    public CardDbContract() { }
    public static final String COL_ID = "_id";

    public static abstract class CardListEntry implements BaseColumns {
        public static final String TABLE_NAME = "cardlist";
        public static final String COL_NAME = "name";
        public static final String COL_FACTION = "faction";
        public static final String COL_LOYAL = "loyalty";
        public static final String COL_TYPE = "cardtype";
        public static final String COL_COST = "cost";
        public static final String COL_TEXT = "cardtext";
        public static final String COL_SET = "cardset";
        public static final String COL_NUMBER = "cardnumber";
    }

    public static abstract class UnitDefEntry implements BaseColumns {
        public static final String TABLE_NAME = "unitdefs";
        public static final String COL_CARDID = "cardid";
        public static final String COL_ATK = "attack";
        public static final String COL_LIFE = "life";
        public static final String COL_COM = "command";
    }

    public static abstract class ShieldsDefEntry implements BaseColumns {
        public static final String TABLE_NAME = "shielddefs";
        public static final String COL_CARDID = "cardid";
        public static final String COL_SHIELDS = "shields";
    }

    public static abstract class WarlordDefEntry implements BaseColumns {
        public static final String TABLE_NAME = "warlorddefs";
        public static final String COL_WARID = "cardid";
        public static final String COL_HAND = "hand";
        public static final String COL_RES = "resources";
        public static final String COL_ATK = "haleattack";
        public static final String COL_LIFE = "halelife";
        public static final String COL_BATK = "bloodyattack";
        public static final String COL_BLIFE = "bloodylife";
        public static final String COL_BTXT = "bloodytext";
    }

    public static abstract class PlanetDefEntry implements BaseColumns {
        public static final String TABLE_NAME = "planetdefs";
        public static final String COL_CARDID = "cardid";
        public static final String COL_ICON = "icons";
    }

    public static abstract class SquadDefEntry implements BaseColumns {
        public static final String TABLE_NAME = "squaddefs";
        public static final String COL_WARID = "warlordid";
        public static final String COL_CARDID = "cardid";
        public static final String COL_QTY = "quantity";
    }
}
