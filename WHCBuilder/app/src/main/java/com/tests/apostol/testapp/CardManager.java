package com.tests.apostol.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager {
    private static CardManager _instance;
    public static CardManager createAndGetInstance(Context c) {
        if (_instance == null)
            _instance = new CardManager(c);

        return _instance;
    }
    public static CardManager getInstance() {
        return _instance;
    }

    private HashMap<String, Card> _cardDatabase;
    private HashMap<Integer, Card> _cardByIdDatabase;
    private HashMap<String, List<Pair<Card, Integer>>> _squadDatabase;

    private String[] _cardCols;
    private String[] _unitCols;
    private String[] _warlordCols;
    private String[] _squadCols;

    private CardManager(Context c) {
        _cardDatabase = new HashMap<>();
        _cardByIdDatabase = new HashMap<>();
        _squadDatabase = new HashMap<>();
        _cardCols = new String[] {
                CardDbContract.COL_ID,
                CardDbContract.CardListEntry.COL_NAME,
                CardDbContract.CardListEntry.COL_FACTION,
                CardDbContract.CardListEntry.COL_LOYAL,
                CardDbContract.CardListEntry.COL_TYPE,
                CardDbContract.CardListEntry.COL_COST,
                CardDbContract.CardListEntry.COL_TEXT,
                CardDbContract.CardListEntry.COL_SET,
                CardDbContract.CardListEntry.COL_NUMBER
        };
        _warlordCols = new String[] {
                CardDbContract.WarlordDefEntry.COL_HAND,
                CardDbContract.WarlordDefEntry.COL_RES,
                CardDbContract.WarlordDefEntry.COL_ATK,
                CardDbContract.WarlordDefEntry.COL_BATK,
                CardDbContract.WarlordDefEntry.COL_LIFE,
                CardDbContract.WarlordDefEntry.COL_BLIFE,
                CardDbContract.WarlordDefEntry.COL_BTXT
        };

        _unitCols = new String[] {
                CardDbContract.UnitDefEntry.COL_ATK,
                CardDbContract.UnitDefEntry.COL_LIFE,
                CardDbContract.UnitDefEntry.COL_COM
        };
        _squadCols = new String[] {
                CardDbContract.SquadDefEntry.COL_WARID,
                CardDbContract.SquadDefEntry.COL_CARDID,
                CardDbContract.SquadDefEntry.COL_QTY
        };

        File dbPath = c.getDatabasePath(CardDatabaseHelper.DATABASE_NAME);

        if (dbPath == null)
            populateDatabase(c);
        else
            loadFromDatabase(c);
    }

    //<editor-fold desc="Create Database">
    private void populateDatabase(Context c) {
        try {
            SQLiteDatabase db = CardDatabaseHelper.createAndGetInstance(c).getWritableDatabase();
            InputStream stream = c.getResources().openRawResource(R.raw.cards);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ctr = stream.read();

            while (ctr != -1)
            {
                baos.write(ctr);
                ctr = stream.read();
            }

            stream.close();

            JSONObject json = new JSONObject(baos.toString());
            Card.CardSets[] setNames = Card.CardSets.values();

            for (Card.CardSets set : setNames) {
                String setDbName = set.getDatabaseName();
                parseSet(db, setDbName, json.getJSONArray(setDbName));
            }

            db.close();
        } catch (FileNotFoundException fe) {
            Log.e("FileNotFoundException", fe.getMessage());
        } catch (IOException ioe) {
            Log.e("IOException", ioe.getMessage());
        } catch (JSONException jse) {
            Log.e("JSONException", jse.getMessage());
        }
    }
    private void parseSet(SQLiteDatabase db, String setDbKey, JSONArray jSetArray) {
        List<Integer> warlordIds = new ArrayList<>();
        Map<Integer, List<Integer[]>> statMap = new HashMap<>();
        Map<Integer, List<Integer[]>> squadMap = new HashMap<>();
        Map<Integer, String> bloodyTexts = new HashMap<>();

        for (int i = 0; i < jSetArray.length(); i++) {
            try {
                JSONObject jCard = jSetArray.getJSONObject(i);

                if (parseCard(db, setDbKey, jCard)) {
                    String cName = jCard.getString(CardDbContract.CardListEntry.COL_NAME);
                    int cNumber = jCard.getInt(CardDbContract.CardListEntry.COL_NUMBER);
                    Cursor c = queryByExactName(cName);
                    int wi = c.getInt(0);
                    warlordIds.add(wi);
                    c.close();

                    JSONObject jWar = jCard.getJSONObject("warlord");
                    JSONArray jStarts = jWar.getJSONArray("start");
                    JSONArray jHale = jWar.getJSONArray("hale");
                    JSONArray jBloody = jWar.getJSONArray("bloody");
                    JSONArray jSquad = jWar.getJSONArray("squad");
                    String jBloodyText = jWar.optString("bloodytext", "");

                    Integer[] startVals = new Integer[] { jStarts.getInt(0), jStarts.getInt(1)};
                    Integer[] haleVals = new Integer[] { jHale.getInt(0), jHale.getInt(1)};
                    Integer[] bloodyVals = new Integer[] { jBloody.getInt(0), jBloody.getInt(1)};

                    List<Integer[]> warlordStats = new ArrayList<>();
                    warlordStats.add(startVals);
                    warlordStats.add(haleVals);
                    warlordStats.add(bloodyVals);

                    List<Integer[]> squadPairs = new ArrayList<>();

                    for (int j = 0; j < jSquad.length(); j++) {
                        JSONArray pair = jSquad.getJSONArray(j);
                        int sqNum = pair.getInt(0) + wi - cNumber;
                        int sqQty = pair.getInt(1);
                        squadPairs.add(new Integer[] { sqNum, sqQty });
                    }

                    bloodyTexts.put(wi, jBloodyText);
                    squadMap.put(wi, squadPairs);
                    statMap.put(wi, warlordStats);
                }
            } catch (JSONException jse) {

            }
        }

        for (int i = 0; i < warlordIds.size(); i++) {
            Integer id = warlordIds.get(i);
            List<Integer[]> squad = squadMap.get(id);
            List<Integer[]> stats = statMap.get(id);
            parseWarlord(db, id, stats, squad, bloodyTexts.get(id));
        }
    }
    private boolean parseCard(SQLiteDatabase db, String cardSet, JSONObject jCard) {
        try {
            String name = jCard.getString(CardDbContract.CardListEntry.COL_NAME);
            String fac = jCard.getString(CardDbContract.CardListEntry.COL_FACTION);
            String loyal = jCard.getString(CardDbContract.CardListEntry.COL_LOYAL);
            String type = jCard.getString(CardDbContract.CardListEntry.COL_TYPE);
            String cost = jCard.optString(CardDbContract.CardListEntry.COL_COST, "-1");
            String text = jCard.getString(CardDbContract.CardListEntry.COL_TEXT);
            Integer number = jCard.getInt(CardDbContract.CardListEntry.COL_NUMBER);
            Integer shields = jCard.optInt(CardDbContract.ShieldsDefEntry.COL_SHIELDS, 0);
            Integer attack = -1;
            Integer life = -1;
            Integer command = -1;
            Integer bloodyAtk = -1;
            Integer bloodyLife = -1;
            Integer hand = -1;
            Integer res = -1;
            String btxt = "";

            ContentValues inVal = new ContentValues();
            inVal.put(CardDbContract.CardListEntry.COL_NAME, name);
            inVal.put(CardDbContract.CardListEntry.COL_FACTION, fac);
            inVal.put(CardDbContract.CardListEntry.COL_LOYAL, loyal);
            inVal.put(CardDbContract.CardListEntry.COL_TYPE, type);
            inVal.put(CardDbContract.CardListEntry.COL_COST, cost);
            inVal.put(CardDbContract.CardListEntry.COL_TEXT, text);
            inVal.put(CardDbContract.CardListEntry.COL_SET, cardSet);
            inVal.put(CardDbContract.CardListEntry.COL_NUMBER, number);
            db.insert(CardDbContract.CardListEntry.TABLE_NAME, null, inVal);

            Cursor c =  queryByExactName(name);
            int cardId = c.getInt(0);
            c.close();

            if (shields != 0) {
                ContentValues sVals = new ContentValues();
                sVals.put(CardDbContract.ShieldsDefEntry.COL_CARDID, cardId);
                sVals.put(CardDbContract.ShieldsDefEntry.COL_SHIELDS, shields);
                db.insert(CardDbContract.ShieldsDefEntry.TABLE_NAME, null, sVals);
            }

            if (type.equals("SY") || type.equals("AR")) {
                JSONArray jUnit = jCard.getJSONArray("unit");
                attack = jUnit.getInt(0);
                life = jUnit.getInt(1);
                command = jUnit.getInt(2);

                ContentValues uVals = new ContentValues();
                uVals.put(CardDbContract.UnitDefEntry.COL_CARDID, cardId);
                uVals.put(CardDbContract.UnitDefEntry.COL_ATK, attack);
                uVals.put(CardDbContract.UnitDefEntry.COL_LIFE, life);
                uVals.put(CardDbContract.UnitDefEntry.COL_COM, command);
                db.insert(CardDbContract.UnitDefEntry.TABLE_NAME, null, uVals);
            }

            if (type.equals("WR")) {
                JSONObject jWar = jCard.getJSONObject("warlord");
                hand = jWar.getJSONArray("start").getInt(0);
                res = jWar.getJSONArray("start").getInt(1);
                attack = jWar.getJSONArray("hale").getInt(0);
                life = jWar.getJSONArray("hale").getInt(1);
                bloodyAtk = jWar.getJSONArray("bloody").getInt(0);
                bloodyLife = jWar.getJSONArray("bloody").getInt(1);
                btxt = jWar.optString("bloodytext", "");
            }

            Card card = new Card(name, fac, loyal, type, cost, text, cardSet, number, shields, attack, life, command, hand, res, bloodyAtk, bloodyLife, btxt);
            _cardDatabase.put(name, card);
            _cardByIdDatabase.put(queryByExactName(name).getInt(0), card);

            return (type.equals("WR"));
        } catch (JSONException je) {
            Log.e(getClass().getName(), je.getMessage());
        }

        return false;
    }
    private void parseWarlord(SQLiteDatabase db, Integer warlordId, List<Integer[]> warlordStats, List<Integer[]> squadPairs, String bloodyText) {
        List<Pair<Card, Integer>> squad = new ArrayList<>();

        for (int j = 0; j < squadPairs.size(); j++) {
            ContentValues inVals = new ContentValues();
            int squadId = squadPairs.get(j)[0];
            int squadQty = squadPairs.get(j)[1];

            inVals.put(CardDbContract.SquadDefEntry.COL_WARID, warlordId);
            inVals.put(CardDbContract.SquadDefEntry.COL_CARDID, squadId);
            inVals.put(CardDbContract.SquadDefEntry.COL_QTY, squadQty);
            db.insert(CardDbContract.SquadDefEntry.TABLE_NAME, null, inVals);

            Pair<Card, Integer> p = new Pair<>(selectCardById(squadId), squadQty);
            squad.add(p);
        }

        Card wc = selectCardById(warlordId);
        _squadDatabase.put(wc.getName(), squad);

        ContentValues specVals = new ContentValues();
        specVals.put(CardDbContract.WarlordDefEntry.COL_CARDID, warlordId);
        specVals.put(CardDbContract.WarlordDefEntry.COL_HAND, warlordStats.get(0)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_RES, warlordStats.get(0)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_ATK, warlordStats.get(1)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_LIFE, warlordStats.get(1)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BATK, warlordStats.get(2)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BLIFE, warlordStats.get(2)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BTXT, bloodyText);
        db.insert(CardDbContract.WarlordDefEntry.TABLE_NAME, null, specVals);
    }
    //</editor-fold>

    private void loadFromDatabase(Context c) {
        SQLiteDatabase db = CardDatabaseHelper.createAndGetInstance(c).getReadableDatabase();
        Cursor cur = queryAllCards();
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            Integer cardId = cur.getInt(0);
            String name = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_NAME));
            String fac = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_FACTION));
            String loyal = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_LOYAL));
            String type = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_TYPE));
            String cost = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_COST));
            String text = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_TEXT));
            String cardSet = cur.getString(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_SET));
            Integer number = cur.getInt(cur.getColumnIndexOrThrow(CardDbContract.CardListEntry.COL_NUMBER));
            Integer shields = -1;
            Integer attack = -1;
            Integer life = -1;
            Integer command = -1;
            Integer bloodyAtk = -1;
            Integer bloodyLife = -1;
            Integer hand = -1;
            Integer res = -1;
            String btxt = "";

            if (type.equals("EV") || type.equals("AT")) {
                Cursor sc = queryShieldsById(cardId);
                sc.moveToFirst();
                shields = sc.getInt(0);
                sc.close();
            }

            if (type.equals("AR") || type.equals("SY")) {
                Cursor uc = queryUnitById(cardId);
                uc.moveToFirst();
                attack = uc.getInt(0);
                life = uc.getInt(1);
                command = uc.getInt(2);
                uc.close();
            }

            if (type.equals("WR")) {
                Cursor wc = queryWarlordById(cardId);
                wc.moveToFirst();
                hand = wc.getInt(0);
                res = wc.getInt(1);
                attack = wc.getInt(2);
                bloodyAtk = wc.getInt(3);
                life = wc.getInt(4);
                bloodyLife = wc.getInt(5);
                btxt = wc.getString(6);
                wc.close();
                _squadDatabase.put(name, new ArrayList<Pair<Card, Integer>>());
            }

            Card card = new Card(name, fac, loyal, type, cost, text, cardSet, number, shields, attack, life, command, hand, res, bloodyAtk, bloodyLife, btxt);

            if (loyal.equals("SG")) {
                Cursor sc = querySquadById(cardId);
                int warlordId = sc.getInt(0);
                int qty = sc.getInt(2);
                sc.close();
                _squadDatabase.get(selectCardById(warlordId).getName()).add(new Pair<>(card, qty));
            }

            _cardDatabase.put(name, card);
            _cardByIdDatabase.put(cardId, card);
            cur.moveToNext();
        }

        cur.close();
    }

    public Cursor queryAllCards() {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.CardListEntry.TABLE_NAME, _cardCols, null, null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    public Cursor queryByExactName(String name) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.CardListEntry.TABLE_NAME, _cardCols, CardDbContract.CardListEntry.COL_NAME + " = \"" + name + "\"", null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    public Cursor queryShieldsById(Integer id) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.ShieldsDefEntry.TABLE_NAME, new String[] { CardDbContract.ShieldsDefEntry.COL_SHIELDS }, CardDbContract.ShieldsDefEntry.COL_CARDID + " = " + id + "", null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    public Cursor queryUnitById(Integer id) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.UnitDefEntry.TABLE_NAME, _unitCols, CardDbContract.UnitDefEntry.COL_CARDID + "=" + id, null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    public Cursor queryWarlordById(Integer id) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.WarlordDefEntry.TABLE_NAME, _warlordCols, CardDbContract.WarlordDefEntry.COL_CARDID + "=" + id, null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    public Cursor querySquadById(Integer id) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(CardDbContract.SquadDefEntry.TABLE_NAME, _squadCols, CardDbContract.SquadDefEntry.COL_CARDID + "=" + id, null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }

    public Card selectCardByName(String name) {
        if (_cardDatabase.containsKey(name))
            return _cardDatabase.get(name);

        return null;
    }
    public Card selectCardById(Integer id) {
        if (_cardByIdDatabase.containsKey(id))
            return _cardByIdDatabase.get(id);

        return null;
    }
    public List<Pair<Card, Integer>> selectSquadByWarlordName(String warlordName) {
        if (_squadDatabase.containsKey(warlordName))
            return _squadDatabase.get(warlordName);

        return null;
    }
}
