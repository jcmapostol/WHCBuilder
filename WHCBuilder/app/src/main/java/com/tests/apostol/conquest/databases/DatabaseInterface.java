package com.tests.apostol.conquest.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.CardBuilder;
import com.tests.apostol.conquest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseInterface {
    private static final int CARD_COUNT = 621;
    private String _querySource;
    private String _factionClause;
    private ConquestDbHelper _cdh;
    private Map<Integer, List<Card>> _warlordSquads;
    private Context _context;

    public DatabaseInterface(Context c, ConquestDbHelper cdh) {
        _cdh = cdh;
        _warlordSquads = new HashMap<>();
        _context = c;
    }

    public int getCount() {
        try {
            File dbFile = _context.getDatabasePath(ConquestDbHelper.DATABASE_NAME);
            SQLiteDatabase actual = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            actual.close();
            SQLiteDatabase db = _cdh.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DbContract.Cards.TABLE;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int res = c.getInt(0);
            c.close();
            return res;
        } catch (SQLiteException sqe) {
            return 0;
        }
    }
    public boolean tryQuery() {
        return getCount() == CARD_COUNT;
    }

    public List<Card> getCards() {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "";

        if (_querySource != null)
            query = "SELECT * FROM (" + _querySource + ")";
        else
            query = "SELECT * FROM " + DbContract.Cards.TABLE;

        if (_factionClause != null && !_factionClause.equals("")) {
            query += " WHERE " + _factionClause + " ORDER BY " + DbContract.Cards.FACTION;
        }

        Cursor c = db.rawQuery(query, null);
        List<Card> result = buildCardsFromCursor(c);
        c.close();
        return result;
    }
    public List<Card> getCardsByType(Card.Types type) {
        if (type == null)
            return getCards();

        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = _querySource != null
                ? "SELECT * FROM (" + _querySource + ") WHERE "
                : "SELECT * FROM " + DbContract.Cards.TABLE + " WHERE ";

        query += DbContract.Cards.TYPE + "='" + type.toString() + "' AND "
                + DbContract.Cards.LOYAL + " != 'SG'"
                + (_factionClause != null && !_factionClause.equals("") ? " AND " + _factionClause : "")
                + " ORDER BY " + DbContract.Cards.FACTION + ", " + DbContract.Cards.NAME;

        Cursor c = db.rawQuery(query, null);
        List<Card> cards = buildCardsFromCursor(c);
        c.close();
        return cards;
    }
    public List<Card> getCardsBySet(Card.CardSets set) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "SELECT * FROM " + DbContract.Cards.TABLE
                + " WHERE " + DbContract.Cards.SET + " =\'" + set.getDatabaseName() + "\'";
        Cursor c = db.rawQuery(query, null);
        List<Card> result = buildCardsFromCursor(c);
        c.close();
        return result;
    }
    public List<Card> getSquad(int warlordId) {
        if (!_warlordSquads.containsKey(warlordId)) {
            SQLiteDatabase db = _cdh.getReadableDatabase();
            String query = "SELECT " + DbContract.Cards.TABLE + ".*, "
                    + DbContract.Squads.TABLE + "." + DbContract.Squads.QTY
                    + " FROM " + DbContract.Cards.TABLE
                    + " JOIN " + DbContract.Squads.TABLE
                    + " ON " + DbContract.Squads.TABLE + "." + DbContract.Squads.CARDID + " = "
                    + DbContract.Cards.TABLE + "." + DbContract._ID
                    + " WHERE " + DbContract.Squads.TABLE + "." + DbContract.Squads.WARID + " = " + warlordId
                    + " ORDER BY " + DbContract.Squads.TABLE + "." + DbContract.Squads.QTY + " DESC";

            Cursor c = db.rawQuery(query, null);
            _warlordSquads.put(warlordId, buildCardsFromCursor(c));
            c.close();
        }

        return _warlordSquads.get(warlordId);
    }

    public List<Card> getCardsByClause(String likeClause) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "";

        if (_querySource != null) {
            query = "SELECT * FROM (" + _querySource + ") "
                    + " WHERE " + DbContract.Cards.NAME + " LIKE \"%" + likeClause + "%\""
                    + " OR " + DbContract.Cards.TRAITS + " LIKE \"%" + likeClause + "%\""
                    + " OR " + DbContract.Cards.TEXT + " LIKE \"%" + likeClause + "%\""
                    + " ORDER BY " + DbContract.Cards.FACTION + ", " + DbContract.Cards.TYPE + " DESC, " + DbContract.Cards.NAME;
        } else {
            query = "SELECT * FROM " + DbContract.Cards.TABLE
                    + " WHERE " + DbContract.Cards.NAME + " LIKE \"%" + likeClause + "%\""
                    + " OR " + DbContract.Cards.TRAITS + " LIKE \"%" + likeClause + "%\""
                    + " OR " + DbContract.Cards.TEXT + " LIKE \"%" + likeClause + "%\""
                    + " ORDER BY " + DbContract.Cards.FACTION + ", " + DbContract.Cards.TYPE + " DESC, " + DbContract.Cards.NAME;
        }

        Cursor c = db.rawQuery(query, null);
        List<Card> cards = buildCardsFromCursor(c);
        c.close();
        return cards;
    }

    public void setFactionFilter(List<Card.Factions> filter) {
        if (filter.size() > 0) {
            _factionClause = DbContract.Cards.FACTION + " IN (";

            for (int i = 0; i < filter.size(); i++) {
                Card.Factions f = filter.get(i);
                _factionClause += "'" + f.toString() + ((i != filter.size() - 1) ? "', " : "')");
            }
        } else {
            _factionClause = "";
        }
    }
    public void setDeckbuildingRestrictions(Card warlord) {
        String bqs = "SELECT * FROM " + DbContract.Cards.TABLE + " WHERE ";

        switch (warlord.getName()) {
            case "• Gorzod":
                bqs += DbContract.Cards.FACTION + " IN ('OS', 'NA') OR ("
                + DbContract.Cards.FACTION + " IN ('AM', 'SM') AND "
                + DbContract.Cards.TRAITS + " LIKE \"%Vehicle%\" AND "
                + DbContract.Cards.LOYAL + " = 'NO')"
                + " ORDER BY " + DbContract.Cards.FACTION;
                _querySource = bqs;
                return;
            case "• Commander Starblaze":
                bqs += DbContract.Cards.FACTION + " IN ('TA', 'NA') OR ("
                        + DbContract.Cards.FACTION + " = 'AM' AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                _querySource = bqs;
                return;
            default:
                break;
        }

        switch (warlord.getFaction()) {
            case AM:
                bqs += DbContract.Cards.FACTION + " IN ('AM', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('OS', 'SM') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case CH:
                bqs += DbContract.Cards.FACTION + " IN ('CH', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('OS', 'DE') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case DE:
                bqs += DbContract.Cards.FACTION + " IN ('DE', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('EL', 'CH') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case EL:
                bqs += DbContract.Cards.FACTION + " IN ('EL', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('DE', 'TA') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case NE:
                bqs += DbContract.Cards.FACTION + " IN ('NE', 'NA') OR ("
                        + DbContract.Cards.FACTION + " != 'TY' AND "
                        + DbContract.Cards.TYPE + " = 'AR' AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case OS:
                bqs += DbContract.Cards.FACTION + " IN ('OS', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('CH', 'AM') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case SM:
                bqs += DbContract.Cards.FACTION + " IN ('SM', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('AM', 'TA') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case TA:
                bqs += DbContract.Cards.FACTION + " IN ('TA', 'NA') OR ("
                        + DbContract.Cards.FACTION + " IN ('SM', 'EL') AND "
                        + DbContract.Cards.LOYAL + " = 'NO')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            case TY:
                bqs += DbContract.Cards.FACTION + " = 'TY' OR ("
                        + DbContract.Cards.FACTION + " = 'NA' AND "
                        + DbContract.Cards.TYPE + " != 'AR')"
                        + " ORDER BY " + DbContract.Cards.FACTION;
                break;
            default:
                break;
        }

        _querySource = bqs;
    }
    public void saveDeck(String name, String desc, Card warlord, List<Card> nonSquadCards) {
        SQLiteDatabase db = _cdh.getWritableDatabase();
        ContentValues in = new ContentValues();
        in.put(DbContract.Preambles.NAME, name);
        in.put(DbContract.Preambles.DESC, desc);
        in.put(DbContract.Preambles.WARID, warlord.getDatabaseId());
        db.insert(DbContract.Preambles.TABLE, null, in);

        for (Card card : nonSquadCards) {
            ContentValues con = new ContentValues();
            con.put(DbContract.Contents.NAME, name);
            con.put(DbContract.Contents.CARDID, card.getDatabaseId());
            con.put(DbContract.Contents.QTY, card.getQuantity());
            db.insert(DbContract.Contents.TABLE, null, con);
        }
    }
    public List<Card> loadDeckPortion(String name, Card.Types type) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "SELECT " + DbContract.Cards.TABLE + ".*, "
                + DbContract.Contents.TABLE + "." + DbContract.Contents.QTY
                + " FROM " + DbContract.Cards.TABLE
                + " JOIN " + DbContract.Contents.TABLE
                + " ON " + DbContract.Cards.TABLE + "." + DbContract._ID + " = "
                + DbContract.Contents.TABLE + "." + DbContract.Contents.CARDID
                + " WHERE " + DbContract.Contents.NAME + " = \'" + name + "\'"
                + " AND " + DbContract.Cards.TYPE + " = \'" + type + "\'"
                + " ORDER BY " + DbContract.Cards.TABLE + "." + DbContract.Cards.FACTION + ", "
                + DbContract.Contents.TABLE + "." + DbContract.Contents.QTY + " DESC" + ", "
                + DbContract.Cards.TABLE + "." + DbContract.Cards.NAME;

        return buildCardsFromCursor(db.rawQuery(query, null));
    }
    public boolean checkDeckExists(String name) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "SELECT * FROM " + DbContract.Preambles.TABLE + " "
                + " WHERE " + DbContract.Preambles.NAME + " = \'" + name + "\'";

        return db.rawQuery(query, null).getCount() > 0;
    }
    public Cursor loadAllDecks() {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "SELECT " + DbContract.Preambles.TABLE + ".*, "
                + DbContract.Cards.TABLE + "." + DbContract.Cards.NAME + ", "
                + DbContract.Cards.TABLE + "." + DbContract.Cards.FACTION
                + " FROM " + DbContract.Preambles.TABLE
                + " JOIN " + DbContract.Cards.TABLE
                + " ON " + DbContract.Cards.TABLE + "." + DbContract._ID + " = "
                + DbContract.Preambles.TABLE + "." + DbContract.Preambles.WARID
                + " ORDER BY " + DbContract.Cards.FACTION + ", " + DbContract.Cards.NAME;

        return db.rawQuery(query, null);
    }
    public void deleteDeck(String name) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String table = DbContract.Contents.TABLE;
        String whereClause = DbContract.Contents.NAME + " = \'" + name + "\'";
        db.delete(table, whereClause, null);
        table = DbContract.Preambles.TABLE;
        whereClause = DbContract.Preambles.NAME + " = \'" + name + "\'";
        db.delete(table, whereClause, null);
    }

    private List<Card> buildCardsFromCursor(Cursor c) {
        if (c.getCount() > 0) {
            c.moveToFirst();

            List<Card> cards = new ArrayList<>();

            while (!c.isAfterLast()) {
                int id = c.getInt(c.getColumnIndexOrThrow(DbContract._ID));
                String name = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.NAME));
                String fac = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.FACTION));
                String loy = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.LOYAL));
                String type = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.TYPE));
                String cost = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.COST));
                String traits = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.TRAITS));
                String text = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.TEXT));
                String set = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.SET));
                int number = c.getInt(c.getColumnIndexOrThrow(DbContract.Cards.NUMBER));
                int cardId = _context.getResources().getIdentifier(set + number, "drawable", _context.getPackageName());
                if (cardId == 0) {
                    cardId = R.drawable.card_back;
                }

                Card card;
                CardBuilder cb = new CardBuilder(id, name, fac, loy, type, traits, text, set, number, cardId);
                cb.setCost(cost);

                if (loy.equals(Card.Loyalties.SG.toString())) {
                    Cursor sqc = querySquadCard(id);
                    int qty = sqc.getInt(sqc.getColumnIndexOrThrow(DbContract.Squads.QTY));
                    cb.setQuantity(qty);
                }

                if (c.getColumnIndex(DbContract.Contents.QTY) != -1) {
                    int qty = c.getInt(c.getColumnIndex(DbContract.Contents.QTY));
                    cb.setQuantity(qty);
                }

                //<editor-fold desc="Get Cardtype-specific fields">
                switch (type) {
                    case "EV":
                    case "AT":
                        Cursor sc = queryShieldsById(id);
                        card = cb.buildShields(sc.getInt(0));
                        sc.close();
                        break;
                    case "AR":
                    case "SY":
                        Cursor uc = queryUnitById(id);
                        card = cb.buildUnit(
                                uc.getInt(uc.getColumnIndexOrThrow(DbContract.Units.ATK)),
                                uc.getInt(uc.getColumnIndexOrThrow(DbContract.Units.LIFE)),
                                uc.getInt(uc.getColumnIndexOrThrow(DbContract.Units.COM))
                        );
                        uc.close();
                        break;
                    case "WR":
                        Cursor wc = queryWarlordById(id);
                        card = cb.buildWarlord(
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.HAND)),
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.RES)),
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.ATK)),
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.LIFE)),
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.BATK)),
                                wc.getInt(wc.getColumnIndexOrThrow(DbContract.Warlords.BLIFE)),
                                wc.getString(wc.getColumnIndexOrThrow(DbContract.Warlords.BTXT))
                        );
                        wc.close();
                        break;
                    default:
                        card = cb.buildGenericCard();
                        break;
                }
                //</editor-fold>

                cards.add(card);
                c.moveToNext();
            }

            return cards;
        }

        return new ArrayList<>();
    }
    private Cursor queryShieldsById(Integer id) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        Cursor c = db.query(DbContract.Shields.TABLE_NAME, new String[] { DbContract.Shields.COL_SHIELDS }, DbContract.Shields.COL_CARDID + " = " + id + "", null, null, null, null);
        c.moveToFirst();
        return c;
    }
    private Cursor queryUnitById(Integer id) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String[] unitCols = new String[] {
                DbContract.Units.ATK,
                DbContract.Units.LIFE,
                DbContract.Units.COM
        };
        Cursor c = db.query(DbContract.Units.TABLE_NAME, unitCols, DbContract.Units.COL_CARDID + "=" + id, null, null, null, null);
        c.moveToFirst();
        return c;
    }
    private Cursor queryWarlordById(Integer id) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String[] warCols = new String[] {
                DbContract.Warlords.HAND,
                DbContract.Warlords.RES,
                DbContract.Warlords.ATK,
                DbContract.Warlords.LIFE,
                DbContract.Warlords.BATK,
                DbContract.Warlords.BLIFE,
                DbContract.Warlords.BTXT
        };

        Cursor c = db.query(DbContract.Warlords.TABLE, warCols, DbContract.Warlords.COL_CARDID + "=" + id, null, null, null, null);
        c.moveToFirst();
        return c;
    }
    private Cursor querySquadCard(int cardId) {
        SQLiteDatabase db = _cdh.getReadableDatabase();
        String query = "SELECT " + DbContract.Cards.TABLE + ".*, "
                + DbContract.Squads.TABLE + "." + DbContract.Squads.QTY
                + " FROM " + DbContract.Cards.TABLE
                + " JOIN " + DbContract.Squads.TABLE
                + " ON " + DbContract.Squads.TABLE + "." + DbContract.Squads.CARDID + " = "
                + DbContract.Cards.TABLE + "." + DbContract._ID
                + " WHERE " + DbContract.Cards.TABLE + "." + DbContract._ID + " = " + cardId;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c;
    }
}
