package se.lu.lucs.dota2.framework.game;

public class ChatEvent {
    private boolean teamOnly;
    private int player;
    private String text;

    public int getPlayer() {
        return player;
    }

    public String getText() {
        return text;
    }

    public boolean isTeamOnly() {
        return teamOnly;
    }

    public void setPlayer( int player ) {
        this.player = player;
    }

    public void setTeamOnly( boolean teamOnly ) {
        this.teamOnly = teamOnly;
    }

    public void setText( String text ) {
        this.text = text;
    }

}
