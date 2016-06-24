package com.tests.apostol.conquest;

import android.graphics.Color;

import java.util.ArrayList;

public class Card {
    public enum Factions {
        AM ("Astra Militarum", Color.GRAY, Color.WHITE, R.drawable.icon_astra_militarum, R.drawable.icon_astra_militarum_dark),
        CH ("Chaos", Color.parseColor("#B27300"), Color.WHITE, R.drawable.icon_chaos, R.drawable.icon_chaos_dark),
        DE ("Dark Eldar", Color.parseColor("#3C1264"), Color.WHITE, R.drawable.icon_dark_eldar, R.drawable.icon_dark_eldar_dark),
        EL ("Eldar", Color.parseColor("#CCCC00"), Color.BLACK, R.drawable.icon_eldar, R.drawable.icon_eldar_dark),
        OS ("Orks", Color.parseColor("#009900"), Color.WHITE, R.drawable.icon_orks, R.drawable.icon_orks_dark),
        NE ("Necrons", Color.parseColor("#004C00"), Color.WHITE, R.drawable.icon_necrons, R.drawable.icon_necrons_dark),
        NA ("Neutrals", Color.WHITE, Color.BLACK, R.drawable.icon_neutrals, R.drawable.icon_neutrals),
        SM ("Space Marines", Color.parseColor("#00007F"), Color.WHITE, R.drawable.icon_space_marines, R.drawable.icon_space_marines_dark),
        TA ("Tau", Color.parseColor("#009999"), Color.WHITE, R.drawable.icon_tau, R.drawable.icon_tau_dark),
        TY ("Tyranids", Color.parseColor("#660000"), Color.WHITE, R.drawable.icon_tyranids, R.drawable.icon_tyranids_dark);

        private String _name;
        private int _color;
        private int _textColor;
        private int _icon;
        private int _darkIcon;

        Factions(String name, int colorHex, int textColor, int iconId, int darkIcon) {
            _name = name;
            _color = colorHex;
            _textColor = textColor;
            _icon = iconId;
            _darkIcon = darkIcon;
        }

        public String getName() {
            return _name;
        }
        public int getColor() { return _color; }
        public int getTextColor() { return _textColor; }
        public int getIconId() { return _icon; }
        public int getDarkIcon() { return _darkIcon; }
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
    public enum Types {
        WR ("Warlord Unit"),
        AR ("Army Unit"),
        AT ("Attachment"),
        EV ("Event"),
        SU ("Support"),
        SY ("Synapse Unit");

        private String _name;

        Types(String name) {
            _name = name;
        }

        public String getFullName() {
            return _name;
        }
    }
    public enum CardCycles {
        CO ("Core Set"),
        WR ("Warlord Cycle"),
        GD ("The Great Devourer"),
        PF ("Planetfall Cycle"),
        LD ("Legions of Death"),
        DW ("Death World Cycle");

        private String _fullName;

        CardCycles(String fullName) {
            _fullName = fullName;
        }

        public String getFullName() { return _fullName; }
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
        AG ("Against the Great Enemy", "against", CardCycles.DW),
        WP ("The Warp Unleashed", "warp", CardCycles.DW);

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
                case "warp":
                    return WP;
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
                    sets.add(WP);
                    break;
            }
            return sets;
        }
    }

    private int _id;
    private String _name;
    private Factions _faction;
    private Loyalties _loyalty;
    private Types _type;
    private String _cost;
    private String _traits;
    private String _text;
    private CardSets _set;
    private int _number;
    private int _shields;
    private int _startingResources;
    private int _startingHand;
    private int _bloodyAttack;
    private int _bloodyLife;
    private String _bloodyText;
    private int _attack;
    private int _life;
    private int _command;
    private int _quantity;
    private int _imageId;

    public int getDatabaseId() { return _id; }
    public String getName() {
        return _name;
    }
    public Factions getFaction() {
        return _faction;
    }
    public Loyalties getLoyalty() {
        return _loyalty;
    }
    public Types getType() {
        return _type;
    }
    public String getCost() {
        return _cost;
    }
    public String getTraits() {
        return _traits;
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
    public int getShields() {
        return _shields;
    }
    public int getStartingResources() { return _startingResources; }
    public int getStartingHand() { return _startingHand; }
    public String getBloodyText() { return _bloodyText; }
    public int getBloodyAttack() { return _bloodyAttack; }
    public int getBloodyLife() { return _bloodyLife; }
    public int getAttack() { return _attack; }
    public int getLife() { return _life; }
    public int getCommand() { return _command; }
    public int getQuantity() { return _quantity; }
    public void setQuantity(int val) { _quantity = val; }
    public int getImageId() { return _imageId; }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            return _name.equals(((Card) o).getName());
        }

        return super.equals(o);
    }

    public Card(int id, int imageId, String name, String faction, String loyal, String type, String cost, String traits, String text, String set, int number, int shields, int atk, int life, int com, int hand, int res, int batk, int blife, String btxt, int qty) {
        _id = id;
        _name = name;
        _faction = Factions.valueOf(faction);
        _loyalty = Loyalties.valueOf(loyal);
        _type = Types.valueOf(type);
        _cost = cost;
        _traits = traits;
        _text = text;
        _set = CardSets.getByName(set);
        _number = number;
        _shields = shields;
        _startingResources = res;
        _startingHand = hand;
        _bloodyAttack = batk;
        _bloodyLife = blife;
        _attack = atk;
        _life = life;
        _command = com;
        _bloodyText = btxt;
        _quantity = qty;
        _imageId = imageId;
    }

    public String getBreakdown() {
        String breakdownText = getName() + "\n";

        if (getType() != Types.WR && (getFaction() != Factions.NE && getFaction() != Factions.TY && getFaction() != Factions.NA))
            breakdownText += getLoyalty().getName() + " ";

        breakdownText += getFaction().getName() + " "
                + getType().getFullName() + "\n";

        switch (getType()) {
            case WR:
                breakdownText
                        += "\nHand / Resources: " + getStartingHand() + " / " + getStartingResources() + "\n"
                        + "Attack / Life: " + getAttack() + " / " + getLife() + "\n"
                        + getTraits() + "\n"
                        + getText() + "\n\n";

                if (getBloodyText().equals(""))
                    breakdownText += "Bloody: " + getBloodyAttack() + " / " + getBloodyLife() + "\n\n";
                else
                    breakdownText += "Bloody: " + getBloodyAttack() + " / " + getBloodyLife() + "\n"
                            + getBloodyText() + "\n\n";
                break;
            case AR:
                breakdownText
                        += "Cost: " + getCost() + "\n\n"
                        + "ATK / HP / Command: ["
                        + getAttack()
                        + "/" + getLife()
                        + "/" + getCommand() + "]\n"
                        + getTraits() + "\n"
                        + getText() + "\n\n";
                break;
            case SY:
                breakdownText
                        += "\nATK / HP / Command: ["
                        + getAttack()
                        + "/" + getLife()
                        + "/" + getCommand() + "]\n"
                        + getTraits() + "\n"
                        + getText() + "\n\n";
                break;
            case EV:
            case AT:
                breakdownText
                        += "Cost: " + getCost() + "\n\n"
                        + "Shields: " + getShields() + "\n"
                        + getTraits() + "\n"
                        + getText() + "\n\n";
                break;
            default:
                breakdownText
                        += "Cost: " + getCost() + "\n\n"
                        + getTraits() + "\n"
                        + getText() + "\n\n";
                break;
        }

        breakdownText += "Set: " + getSet().getName() + " #" + getCardNumber();
        return breakdownText;
    }
}

