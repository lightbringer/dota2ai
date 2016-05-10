package de.lighti.dota2.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class World {
    private Map<Integer, BaseEntity> entities;

    public World() {
        entities = new HashMap<Integer, BaseEntity>( 1024 );
    }

    public Map<Integer, BaseEntity> getEntities() {
        return entities;
    }

    public int indexOf( BaseEntity target ) {
        return entities.entrySet().stream().filter( p -> p.getValue() == target ).findFirst().get().getKey();
    }

    public int searchIndexByName( String string ) {
        final Entry<Integer, BaseEntity> e = entities.entrySet().stream().filter( p -> p.getValue().getName().equals( string ) ).findAny().orElse( null );
        if (e == null) {
            return -1;
        }
        else {
            return e.getKey();
        }
    }

    public void setEntities( Map<Integer, BaseEntity> entities ) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "World [entities=" );
        for (final Map.Entry<Integer, BaseEntity> e : entities.entrySet()) {
            builder.append( e.getKey() );
            builder.append( " = " );
            builder.append( e.getValue() );
            builder.append( "\n" );
        }
        builder.append( "]" );
        return builder.toString();
    }

}
