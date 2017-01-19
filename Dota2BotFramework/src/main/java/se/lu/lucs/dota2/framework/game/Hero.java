package se.lu.lucs.dota2.framework.game;

public class Hero extends BaseNPC {

    private final static String TYPE = "Hero";

    private int xp;
    private int deaths;
    private int denies;
    private int gold;

    public int getDeaths() {
        return deaths;
    }

    public int getDenies() {
        return denies;
    }

    public int getGold() {
        return gold;
    }

    public String getType() {
        return TYPE;
    }

    public int getXp() {
        return xp;
    }

    public void setDeaths( int deaths ) {
        this.deaths = deaths;
    }

    public void setDenies( int denies ) {
        this.denies = denies;
    }

    public void setGold( int gold ) {
        this.gold = gold;
    }

    public void setXp( int xp ) {
        this.xp = xp;
    }

}
