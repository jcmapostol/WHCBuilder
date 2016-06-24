package com.tests.apostol.conquest;

public class CardBuilder {
    private int _id;
    private String _name;
    private String _faction;
    private String _loyalty;
    private String _type;
    private String _traits;
    private String _text;
    private String _set;
    private int _number;
    private int _hand = -1;
    private int _res = -1;
    private int _atk = -1;
    private int _life = -1;
    private int _batk = -1;
    private int _blife = -1;
    private String _btxt = "";
    private int _com = -1;
    private String _cost = "-1";
    private int _shields = 0;
    private int _qty = 0;
    private int _cardId = 0;

    public CardBuilder(int id, String name, String faction, String loyalty, String type, String traits, String text, String set, int number, int imageId) {
        _id = id;
        _name = name;
        _faction = faction;
        _loyalty = loyalty;
        _type = type;
        _traits = traits;
        _text = text;
        _set = set;
        _number = number;
        _cardId = imageId;
    }
    public CardBuilder setCost(String val) {
        _cost = val;
        return this;
    }
    public CardBuilder setQuantity(int val) {
        _qty = val;
        return this;
    }

    public Card buildGenericCard() {
        return new Card(_id,
                _cardId, _name,
                _faction,
                _loyalty,
                _type,
                _cost,
                _traits,
                _text,
                _set,
                _number,
                _shields,
                _atk,
                _life,
                _com,
                _hand,
                _res,
                _batk,
                _blife,
                _btxt,
                _qty
        );
    }
    public Card buildShields(int shields) {
        _shields = shields;
        return new Card(_id,
                _cardId, _name,
                _faction,
                _loyalty,
                _type,
                _cost,
                _traits,
                _text,
                _set,
                _number,
                shields,
                _atk,
                _life,
                _com,
                _hand,
                _res,
                _batk,
                _blife,
                _btxt,
                _qty
        );
    }
    public Card buildWarlord(int hand, int res, int atk, int life, int batk, int blife, String btxt) {
        _hand = hand;
        _res = res;
        _atk = atk;
        _life = life;
        _batk = batk;
        _blife = blife;
        _btxt = btxt;
        return new Card(_id,
                _cardId, _name,
                _faction,
                _loyalty,
                _type,
                _cost,
                _traits,
                _text,
                _set,
                _number,
                _shields,
                atk,
                life,
                _com,
                hand,
                res,
                batk,
                blife,
                btxt,
                _qty
        );
    }
    public Card buildUnit(int atk, int life, int com) {
        _atk = atk;
        _life = life;
        _com = com;
        return new Card(_id,
                _cardId, _name,
                _faction,
                _loyalty,
                _type,
                _cost,
                _traits,
                _text,
                _set,
                _number,
                _shields,
                atk,
                life,
                com,
                _hand,
                _res,
                _batk,
                _blife,
                _btxt,
                _qty
        );
    }
}
