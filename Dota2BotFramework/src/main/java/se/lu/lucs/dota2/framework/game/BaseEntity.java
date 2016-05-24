package se.lu.lucs.dota2.framework.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type" )
@JsonSubTypes( { @Type( name = "Hero", value = Hero.class ), @Type( name = "BaseNPC", value = BaseNPC.class ), @Type( name = "Tower", value = Tower.class ),
                @Type( name = "Building", value = Building.class ), @Type( name = "Tree", value = Tree.class ),
                @Type( name = "Ability", value = Ability.class ) })
public abstract class BaseEntity {
    protected float[] origin;

    protected String name;
    protected int health;
    protected int maxHealth;

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getName() {
        return name;
    }

    public float[] getOrigin() {
        return origin;
    }

    public void setHealth( int health ) {
        this.health = health;
    }

    public void setMaxHealth( int maxHealth ) {
        this.maxHealth = maxHealth;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setOrigin( float[] origin ) {
        this.origin = origin;
    }

}
