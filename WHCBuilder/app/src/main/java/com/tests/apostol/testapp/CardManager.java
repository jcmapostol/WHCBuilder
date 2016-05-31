package com.tests.apostol.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager {
    HashMap<String, Card> _cardDatabase;
    HashMap<Integer, Card> _cardByIdDatabase;
    HashMap<String, List<Pair<Card, Integer>>> _squadDatabase;

    String[] _cardCols;
    String _cardTableName;
    String _squadTableName;
    String[] _squadCols;

    public boolean isReady;
    private static CardManager _instance;

    public static CardManager getInstance() {
        return _instance;
    }
    public static CardManager createAndGetInstance(Context c) {
        if (_instance == null)
            _instance = new CardManager(c);

        return _instance;
    }

    private CardManager(Context c) {
        _cardDatabase = new HashMap<>();
        _cardByIdDatabase = new HashMap<>();
        _squadDatabase = new HashMap<>();

        _cardTableName = CardDbContract.CardListEntry.TABLE_NAME;
        _squadTableName = CardDbContract.SquadDefEntry.TABLE_NAME;

        _squadCols = new String[]
        {
            CardDbContract.COL_ID,
            CardDbContract.SquadDefEntry.COL_WARID,
            CardDbContract.SquadDefEntry.COL_CARDID,
            CardDbContract.SquadDefEntry.COL_QTY
        };

        _cardCols = new String[]
        {
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

        populateDatabase(c);
        isReady = true;
    }

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
                    int wi = queryByName(cName).getInt(0);
                    warlordIds.add(wi);

                    JSONObject jWar = jCard.getJSONObject("warlord");
                    JSONArray jStarts = jWar.getJSONArray("start");
                    JSONArray jHale = jWar.getJSONArray("hale");
                    JSONArray jBloody = jWar.getJSONArray("bloody");
                    JSONArray jSquad = jWar.getJSONArray("squad");
                    String jBloodyText = jCard.optString("bloodytext", "");

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

            Card c = new Card(name, fac, loyal, type, cost, text, cardSet, number);
            _cardDatabase.put(name, c);
            _cardByIdDatabase.put(queryByName(name).getInt(0), c);

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
        specVals.put(CardDbContract.WarlordDefEntry.COL_WARID, warlordId);
        specVals.put(CardDbContract.WarlordDefEntry.COL_HAND, warlordStats.get(0)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_RES, warlordStats.get(0)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_ATK, warlordStats.get(1)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_LIFE, warlordStats.get(1)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BATK, warlordStats.get(2)[0]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BLIFE, warlordStats.get(2)[1]);
        specVals.put(CardDbContract.WarlordDefEntry.COL_BTXT, bloodyText);
        db.insert(CardDbContract.WarlordDefEntry.TABLE_NAME, null, specVals);
    }

    public Cursor queryAllCards() {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(_cardTableName, _cardCols, null, null, null, null, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }
    private Cursor queryByName(String name) {
        SQLiteDatabase db = CardDatabaseHelper.getInstance().getReadableDatabase();
        Cursor c = db.query(_cardTableName, _cardCols, CardDbContract.CardListEntry.COL_NAME + " = \"" + name + "\"", null, null, null, null);

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
