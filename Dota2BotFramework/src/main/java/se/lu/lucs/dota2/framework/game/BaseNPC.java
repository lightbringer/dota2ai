package se.lu.lucs.dota2.framework.game;

import java.util.Arrays;
import java.util.Map;

public class BaseNPC extends BaseEntity {
    protected int level;

//                    --unit.absOrigin = VectorToString(eunit:GetAbsOrigin())
//                    --unit.center = VectorToString(eunit:GetCenter())

    protected boolean alive;
    protected boolean blind;
    protected boolean dominated;
    protected boolean deniable;
    protected boolean disarmed;
    protected boolean rooted;

    protected int team;
    protected float attackRange;
    protected int attackTarget;
    protected float mana;
    protected float maxMana;

    protected Map<Integer, Ability> abilities;

    protected BaseNPC() {
        origin = new float[3];
    }

    public Map<Integer, Ability> getAbilities() {
        return abilities;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public int getAttackTarget() {
        return attackTarget;
    }

    public int getLevel() {
        return level;
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public int getTeam() {
        return team;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isBlind() {
        return blind;
    }

    public boolean isDeniable() {
        return deniable;
    }

    public boolean isDisarmed() {
        return disarmed;
    }

    public boolean isDominated() {
        return dominated;
    }

    public boolean isRooted() {
        return rooted;
    }

    public void setAbilities( Map<Integer, Ability> abilities ) {
        this.abilities = abilities;
    }

    public void setAlive( boolean alive ) {
        this.alive = alive;
    }

    public void setAttackRange( float attackRange ) {
        this.attackRange = attackRange;
    }

    public void setAttackTarget( int attackTarget ) {
        this.attackTarget = attackTarget;
    }

    public void setBlind( boolean blind ) {
        this.blind = blind;
    }

    public void setDeniable( boolean deniable ) {
        this.deniable = deniable;
    }

    public void setDisarmed( boolean disarmed ) {
        this.disarmed = disarmed;
    }

    public void setDominated( boolean dominated ) {
        this.dominated = dominated;
    }

    public void setLevel( int level ) {
        this.level = level;
    }

    public void setMana( float mana ) {
        this.mana = mana;
    }

    public void setMaxMana( float maxMana ) {
        this.maxMana = maxMana;
    }

    public void setRooted( boolean rooted ) {
        this.rooted = rooted;
    }

    public void setTeam( int team ) {
        this.team = team;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "BaseNPC [level=" );
        builder.append( level );
        builder.append( ", origin=" );
        builder.append( Arrays.toString( origin ) );
        builder.append( ", alive=" );
        builder.append( alive );
        builder.append( ", blind=" );
        builder.append( blind );
        builder.append( ", dominated=" );
        builder.append( dominated );
        builder.append( ", deniable=" );
        builder.append( deniable );
        builder.append( ", disarmed=" );
        builder.append( disarmed );
        builder.append( ", rooted=" );
        builder.append( rooted );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( ", team=" );
        builder.append( team );
        builder.append( ", attackRange=" );
        builder.append( attackRange );
        builder.append( ", attackTarget=" );
        builder.append( attackTarget );
        builder.append( ", mana=" );
        builder.append( mana );
        builder.append( ", abilities=" );
        builder.append( abilities );
        builder.append( "]" );
        return builder.toString();
    }

}
