package com.tests.apostol.conquest.databases;

import android.provider.BaseColumns;

public final class DbContract {
    public DbContract() { }
    public static final String _ID = "_id";

    public static abstract class Cards implements BaseColumns {
        public static final String TABLE = "cardlist";
        public static final String NAME = "name";
        public static final String FACTION = "faction";
        public static final String LOYAL = "loyalty";
        public static final String TYPE = "cardtype";
        public static final String COST = "cost";
        public static final String TRAITS = "traits";
        public static final String TEXT = "cardtext";
        public static final String SET = "cardset";
        public static final String NUMBER = "cardnumber";
    }

    public static abstract class Units implements BaseColumns {
        public static final String TABLE_NAME = "unitdefs";
        public static final String COL_CARDID = "cardid";
        public static final String ATK = "attack";
        public static final String LIFE = "life";
        public static final String COM = "command";
    }

    public static abstract class Shields implements BaseColumns {
        public static final String TABLE_NAME = "shielddefs";
        public static final String COL_CARDID = "cardid";
        public static final String COL_SHIELDS = "shields";
    }

    public static abstract class Warlords implements BaseColumns {
        public static final String TABLE = "warlorddefs";
        public static final String COL_CARDID = "cardid";
        public static final String HAND = "hand";
        public static final String RES = "resources";
        public static final String ATK = "haleattack";
        public static final String LIFE = "halelife";
        public static final String BATK = "bloodyattack";
        public static final String BLIFE = "bloodylife";
        public static final String BTXT = "bloodytext";
    }

    public static abstract class Squads implements BaseColumns {
        public static final String TABLE = "squaddefs";
        public static final String WARID = "warlordid";
        public static final String CARDID = "cardid";
        public static final String QTY = "quantity";
    }

    public static abstract class Preambles implements BaseColumns {
        public static final String TABLE = "deckpreambles";
        public static final String NAME = "deckname";
        public static final String DESC = "deckdesc";
        public static final String WARID = "warlordid";
    }

    public static abstract class Contents implements BaseColumns {
        public static final String TABLE = "deckcontents";
        public static final String NAME = "deckname";
        public static final String CARDID = "cardid";
        public static final String QTY = "qty";
    }
}
