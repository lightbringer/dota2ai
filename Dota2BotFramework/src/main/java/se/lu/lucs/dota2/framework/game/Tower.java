package se.lu.lucs.dota2.framework.game;

import java.util.Arrays;

public class Tower extends Building {

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "Tower [level=" );
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
        builder.append( ", health=" );
        builder.append( health );
        builder.append( ", maxHealth=" );
        builder.append( maxHealth );
        builder.append( "]" );
        return builder.toString();
    }

}
