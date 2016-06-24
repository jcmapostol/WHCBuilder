package com.tests.apostol.conquest.databases;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabasePopulatorTask extends AsyncTask<Void, Integer, Integer> {
    private Context _context;
    private ConquestDbHelper _dbHelper;
    private NotificationManager _nm;
    private long _startTime = 0;
    private long _elapsedTime = 0;

    private int _insertId = 0;
    private String _currentSet = "";
    private String _currentCycle = "";
    private int _total;
    private boolean _deleteDatabase;

    public DatabasePopulatorTask(boolean deleteDatabase, Context c, NotificationManager nm, ConquestDbHelper dbh) {
        _deleteDatabase = deleteDatabase;
        _context = c;
        _dbHelper = dbh;
        _nm = nm;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int count;

        try {
            if (_deleteDatabase)
                _context.deleteDatabase(ConquestDbHelper.DATABASE_NAME);

            File db = _context.getDatabasePath(ConquestDbHelper.DATABASE_NAME);
            SQLiteDatabase actual = SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            actual.close();
            count = -1;
        } catch (SQLiteException e) {
            ((Activity) _context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(_context, "Populating database.", Toast.LENGTH_SHORT).show();
                }
            });

            NotificationCompat.Builder nb = new NotificationCompat.Builder(_context)
                    .setSmallIcon(R.drawable.icon_cards)
                    .setContentText("Populating card database.")
                    .setContentTitle(_context.getString(R.string.app_name));
            _nm.notify(0, nb.build());

            _startTime = System.currentTimeMillis();
            count = populateDatabase();
        }

        return count;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (_elapsedTime > 500) {
            NotificationCompat.Builder nb = new NotificationCompat.Builder(_context)
                    .setSmallIcon(R.drawable.icon_cards)
                    .setContentText("Downloading " + values[0] + " out of  " + values[1] + " cards from " + _currentSet + ".")
                    .setContentTitle(_context.getString(R.string.app_name))
                    .setProgress(values[1], values[0], false);

            _nm.notify(1, nb.build());
            _startTime = System.currentTimeMillis();
            _elapsedTime = 0;
        } else if (values[0] != values[1]){
            _elapsedTime = new Date().getTime() - _startTime;
        } else {
            _elapsedTime = 0;
            NotificationCompat.Builder nb = new NotificationCompat.Builder(_context)
                    .setSmallIcon(R.drawable.icon_cards)
                    .setContentText("Downloaded all cards from " + _currentSet + ".")
                    .setContentTitle(_context.getString(R.string.app_name))
                    .setProgress(0, 0, false);

            _nm.notify(1, nb.build());
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer != -1) {
            Toast.makeText(_context, "Downloaded " + integer + " out of " + _total + " cards.", Toast.LENGTH_SHORT).show();

            NotificationCompat.Builder nb = new NotificationCompat.Builder(_context)
                    .setSmallIcon(R.drawable.icon_cards)
                    .setContentText("Downloads successful.")
                    .setContentTitle(_context.getString(R.string.app_name))
                    .setProgress(0, 0, false);

            _nm.notify(0, nb.build());
        }
    }

    private int populateDatabase() {
        int cardCount = 0;

        try {
            InputStream stream = _context.getResources().openRawResource(R.raw.cards);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ctr = stream.read();

            while (ctr != -1) {
                baos.write(ctr);
                ctr = stream.read();
            }

            stream.close();

            JSONObject json = new JSONObject(baos.toString());
            Card.CardSets[] setNames = Card.CardSets.values();

            for (final Card.CardSets set : setNames) {
                String setDbName = set.getDatabaseName();

                ((Activity) _context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!_currentCycle.equals(set.getCycle().getFullName())) {
                            _currentCycle = set.getCycle().getFullName();
                            Toast.makeText(_context, "Downloading cards from " + _currentCycle + ".", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cardCount += parseSet(setDbName, json.getJSONArray(setDbName));
            }

            _dbHelper.getWritableDatabase().close();
        } catch (FileNotFoundException fe) {
            Log.e("FileNotFoundException", fe.getMessage());
        } catch (IOException ioe) {
            Log.e("IOException", ioe.getMessage());
        } catch (JSONException jse) {
            Log.e("JSONException", jse.getMessage());
        }

        return cardCount;
    }
    private int parseSet(String setDbKey, JSONArray jSetArray) {
        List<Integer[]> warlordIds = new ArrayList<>();
        List<JSONObject> warlordFields = new ArrayList<>();
        Card.CardSets set = Card.CardSets.getByName(setDbKey);
        _currentSet = set.getName();
        _total += jSetArray.length();
        int parsed = 0;

        try {
            for (int i = 0; i < jSetArray.length(); i++) {
                parsed++;
                JSONObject jCard = jSetArray.getJSONObject(i);
                _insertId++;

                if (parseCard(setDbKey, jCard)) {
                    int number = jCard.getInt(DbContract.Cards.NUMBER);
                    warlordIds.add(new Integer[]{_insertId, number});

                    JSONObject jWar = jCard.getJSONObject("warlord");
                    warlordFields.add(jWar);
                }

                publishProgress(parsed, jSetArray.length());
            }
        } catch (JSONException jse) {
            Log.e("parseSet " + _insertId, jse.getMessage());
        }

        for (int i = 0; i < warlordIds.size(); i++) {
            Integer autoId = warlordIds.get(i)[0];
            Integer cardId = warlordIds.get(i)[1];
            parseWarlord(autoId, cardId, warlordFields.get(i));
        }

        return parsed;
    }
    private boolean parseCard(String cardSet, JSONObject jCard) {
        try {
            SQLiteDatabase db = _dbHelper.getWritableDatabase();
            int cardId = _insertId;

            String name = jCard.getString(DbContract.Cards.NAME);
            String fac = jCard.getString(DbContract.Cards.FACTION);
            String loyal = jCard.getString(DbContract.Cards.LOYAL);
            String type = jCard.getString(DbContract.Cards.TYPE);
            String traits = jCard.getString(DbContract.Cards.TRAITS);
            String cost = jCard.optString(DbContract.Cards.COST, "-1");
            String text = jCard.getString(DbContract.Cards.TEXT);
            Integer number = jCard.getInt(DbContract.Cards.NUMBER);
            Integer shields = jCard.optInt(DbContract.Shields.COL_SHIELDS, 0);

            ContentValues in = new ContentValues();
            in.put(DbContract.Cards.NAME, name);
            in.put(DbContract.Cards.FACTION, fac);
            in.put(DbContract.Cards.LOYAL, loyal);
            in.put(DbContract.Cards.TYPE, type);
            in.put(DbContract.Cards.COST, cost);
            in.put(DbContract.Cards.TRAITS, traits);
            in.put(DbContract.Cards.TEXT, text);
            in.put(DbContract.Cards.SET, cardSet);
            in.put(DbContract.Cards.NUMBER, number);
            db.insert(DbContract.Cards.TABLE, null, in);

            if (shields != 0) {
                ContentValues shieldCard = new ContentValues();
                shieldCard.put(DbContract.Shields.COL_CARDID, cardId);
                shieldCard.put(DbContract.Shields.COL_SHIELDS, shields);
                db.insert(DbContract.Shields.TABLE_NAME, null, shieldCard);
            }

            if (type.equals("SY") || type.equals("AR")) {
                JSONArray jUnit = jCard.getJSONArray("unit");
                Integer atk = jUnit.getInt(0);
                Integer life = jUnit.getInt(1);
                Integer com = jUnit.getInt(2);

                ContentValues unitCard = new ContentValues();
                unitCard.put(DbContract.Units.COL_CARDID, cardId);
                unitCard.put(DbContract.Units.ATK, atk);
                unitCard.put(DbContract.Units.LIFE, life);
                unitCard.put(DbContract.Units.COM, com);
                db.insert(DbContract.Units.TABLE_NAME, null, unitCard);
            }

            return (type.equals("WR"));
        } catch (JSONException je) {
            Log.e("parseCard " + _insertId, je.getMessage());
        }

        return false;
    }
    private void parseWarlord(Integer autoId, Integer cardId, JSONObject jFields) {
        try {
            SQLiteDatabase db = _dbHelper.getWritableDatabase();
            JSONArray jStarts = jFields.getJSONArray("start");
            JSONArray jHale = jFields.getJSONArray("hale");
            JSONArray jBloody = jFields.getJSONArray("bloody");
            JSONArray jSquad = jFields.getJSONArray("squad");
            String jBtxt = jFields.optString("bloodytext", "");

            ContentValues warlord = new ContentValues();
            warlord.put(DbContract.Warlords.COL_CARDID, autoId);
            warlord.put(DbContract.Warlords.HAND, jStarts.getInt(0));
            warlord.put(DbContract.Warlords.RES, jStarts.getInt(1));
            warlord.put(DbContract.Warlords.ATK, jHale.getInt(0));
            warlord.put(DbContract.Warlords.LIFE, jHale.getInt(1));
            warlord.put(DbContract.Warlords.BATK, jBloody.getInt(0));
            warlord.put(DbContract.Warlords.BLIFE, jBloody.getInt(1));
            warlord.put(DbContract.Warlords.BTXT, jBtxt);
            db.insert(DbContract.Warlords.TABLE, null, warlord);

            for (int j = 0; j < jSquad.length(); j++) {
                JSONArray pair = jSquad.getJSONArray(j);

                ContentValues in = new ContentValues();
                int squadId = pair.getInt(0) + (autoId - cardId);
                int squadQty = pair.getInt(1);

                in.put(DbContract.Squads.WARID, autoId);
                in.put(DbContract.Squads.CARDID, squadId);
                in.put(DbContract.Squads.QTY, squadQty);
                db.insert(DbContract.Squads.TABLE, null, in);
            }
        } catch (JSONException jse) {
            Log.e("parseWarlord " + _insertId, jse.getMessage());
        }
    }
}
