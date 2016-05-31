package com.tests.apostol.testapp;

import java.util.ArrayList;

public class Card {
    public enum Factions {
        AM ("Astra Militarum"),
        CH ("Chaos"),
        DE ("Dark Eldar"),
        EL ("Eldar"),
        OR ("Orks"),
        NE ("Necrons"),
        NA ("Neutrals"),
        SM ("Space Marines"),
        TA ("Tau"),
        TY ("Tyranids");

        private String _name;

        Factions(String name) {
            _name = name;
        }

        public String getFullName() {
            return _name;
        }
    }
    public enum Loyalties {
        LO ("Loyal"),
        NO ("Nonloyal"),
        SG ("Signature");

        private String _name;

        Loyalties(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }
    }
    public enum CardTypes {
        WR ("Warlord Unit"),
        AR ("Army Unit"),
        AT ("Attachment"),
        EV ("Event"),
        SU ("Support"),
        SY ("Synapse Unit");

        private String _name;

        CardTypes (String name) {
            _name = name;
        }

        public String getFullName() {
            return _name;
        }
    }
    public enum CardCycles {
        CO,
        WR,
        GD,
        PF,
        LD,
        DW
    }
    public enum CardSets {
        CO ("Core Set", "core", CardCycles.CO),
        HW ("The Howl of Blackmane", "howl", CardCycles.WR),
        TS ("The Scourge", "scourge", CardCycles.WR),
        GE ("Gift of the Ethereals", "ethereals", CardCycles.WR),
        ZC ("Zogwort's Curse", "curse", CardCycles.WR),
        TB ("The Threat Beyond", "threat", CardCycles.WR),
        DI ("Descendants of Isha", "isha", CardCycles.WR),
        GD ("The Great Devourer", "devourer", CardCycles.GD),
        DR ("Decree of Ruin", "decree", CardCycles.PF),
        BH ("Boundless Hate", "hate", CardCycles.PF),
        DS ("Deadly Salvage", "salvage", CardCycles.PF),
        LB ("What Lurks Below", "lurks", CardCycles.PF),
        WC ("Wrath of the Crusaders", "crusaders", CardCycles.PF),
        FG ("The Final Gambit", "gambit", CardCycles.PF),
        LD ("Legions of Death", "legions", CardCycles.PF),
        JN ("Jungles of Nectavus", "jungles", CardCycles.DW),
        UF ("Unforgiven", "unforgiven", CardCycles.DW),
        SB ("Slash and Burn", "slash", CardCycles.DW),
        ST ("Searching for Truth", "searching", CardCycles.DW),
        AG ("Against the Great Enemy", "against", CardCycles.DW);

        private String _name;
        private String _databaseName;
        private CardCycles _cycle;

        CardSets (String name, String databaseName, CardCycles cc) {
            _name = name;
            _databaseName = databaseName;
            _cycle = cc;
        }

        public String getName() {
            return _name;
        }
        public String getDatabaseName() {
            return _databaseName;
        }
        public CardCycles getCycle() {
            return _cycle;
        }

        public static CardSets getByName(String name) {
            switch (name) {
                case "core":
                    return CO;
                case "howl":
                    return HW;
                case "scourge":
                    return TS;
                case "ethereals":
                    return GE;
                case "curse":
                    return ZC;
                case "threat":
                    return TB;
                case "isha":
                    return DI;
                case "devourer":
                    return GD;
                case "decree":
                    return DR;
                case "hate":
                    return BH;
                case "salvage":
                    return DS;
                case "lurks":
                    return LB;
                case "crusaders":
                    return WC;
                case "gambit":
                    return FG;
                case "legions":
                    return LD;
                case "jungles":
                    return JN;
                case "unforgiven":
                    return UF;
                case "slash":
                    return SB;
                case "searching":
                    return ST;
                case "against":
                    return AG;
                default:
                    return CO;
            }
        }
        public static ArrayList<CardSets> getAllByCycle(CardCycles cycle) {
            ArrayList<CardSets> sets = new ArrayList<>();

            switch (cycle) {
                case CO:
                    sets.add(CO);
                    break;
                case WR:
                    sets.add(HW);
                    sets.add(TS);
                    sets.add(GE);
                    sets.add(ZC);
                    sets.add(TB);
                    sets.add(DI);
                    break;
                case GD:
                    sets.add(GD);
                    break;
                case PF:
                    sets.add(DR);
                    sets.add(BH);
                    sets.add(DS);
                    sets.add(LB);
                    sets.add(WC);
                    sets.add(FG);
                    break;
                case LD:
                    sets.add(LD);
                    break;
                case DW:
                    sets.add(JN);
                    sets.add(UF);
                    sets.add(SB);
                    sets.add(ST);
                    sets.add(AG);
                    break;
            }
            return sets;
        }
    }

    private int _databaseId;
    private String _name;
    private Factions _faction;
    private Loyalties _loyalty;
    private CardTypes _type;
    private String _cost;
    private String _text;
    private CardSets  _set;
    private int _number;

    public int getDatabaseId() { return _databaseId; }
    public String getName() {
        return _name;
    }
    public Factions getFaction() {
        return _faction;
    }
    public Loyalties getLoyalty() {
        return _loyalty;
    }
    public CardTypes getType() {
        return _type;
    }
    public String getCost() {
        return _cost;
    }
    public String getText() {
        return _text;
    }
    public CardSets getSet() {
        return _set;
    }
    public int getCardNumber() {
        return _number;
    }

    public Card(String name, String faction, String loyal, String type, String cost, String text, String set, int number) {
        _name = name;
        _faction = Factions.valueOf(faction);
        _loyalty = Loyalties.valueOf(loyal);
        _type = CardTypes.valueOf(type);
        _cost = cost;
        _text = text;
        _set = CardSets.getByName(set);
        _number = number;
    }
}
