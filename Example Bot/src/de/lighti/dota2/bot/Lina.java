package de.lighti.dota2.bot;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import de.lighti.dota2.bot.BotCommands.LevelUp;
import de.lighti.dota2.bot.BotCommands.Select;
import de.lighti.dota2.game.Ability;
import de.lighti.dota2.game.BaseBot;
import de.lighti.dota2.game.BaseEntity;
import de.lighti.dota2.game.BaseNPC;
import de.lighti.dota2.game.ChatEvent;
import de.lighti.dota2.game.Hero;
import de.lighti.dota2.game.Tower;
import de.lighti.dota2.game.World;

public class Lina extends BaseBot {
    private enum Mode {
        ENABLED, DISABLED
    }

    private static final String MY_HERO_NAME = "npc_dota_hero_lina";

    private static float distance( BaseEntity a, BaseEntity b ) {
        final float[] posA = a.getOrigin();
        final float[] posB = b.getOrigin();
        return distance( posA, posB );
    }

    private static float distance( float[] posA, float[] posB ) {
        return (float) Math.hypot( posB[0] - posA[0], posB[1] - posA[1] );
    }

    private static Set<BaseEntity> findEntitiesInRange( World world, BaseEntity center, float range, int team ) {
        final Set<BaseEntity> result = world.getEntities().values().stream().filter( e -> ((BaseNPC) e).getTeam() == team )
                        .filter( e -> distance( center, e ) < range ).collect( Collectors.toSet() );
        result.remove( center );
        return result;
    }

    private int[] myLevels;

    private Mode mode = Mode.DISABLED;
    private boolean shouldRetreat;
    private boolean shouldBuyTango;

    public Lina() {
        System.out.println( "Creating Lina" );
        myLevels = new int[5];

    }

    private Command attack( Hero lina, Set<BaseEntity> e, World world ) {
        final BaseEntity target = e.stream().sorted( ( e1, e2 ) -> Integer.compare( ((BaseNPC) e1).getHealth(), ((BaseNPC) e2).getHealth() ) )
                        .filter( f -> ((BaseNPC) f).getTeam() != lina.getTeam() ).findFirst().orElse( null );
        if (target == null) {
            //Nothing in range
            System.out.println( "No enemy in range" );
            return NOOP;
        }

        //If lina has enough mana, there's a 30 % chance that she'll cast a spell
        if (lina.getMana() > lina.getMaxMana() * 0.5 && Math.random() > 0.3) {
            return castSpell( lina, target, world );
        }
        else {
            //Otherwise she just attacks
            final int targetindex = world.indexOf( target );
            ATTACK.setTarget( targetindex );
            System.out.println( "Attacking" );

            return ATTACK;
        }
    }

    private Command buy( String item ) {
        BUY.setItem( item );

        return BUY;
    }

    private Command castSpell( Hero lina, BaseEntity target, World world ) {
        final Random r = new Random();
        final int index = r.nextInt( 4 );
        final Ability a = lina.getAbilities().get( index );
        if (a.getAbilityDamageType() == Ability.DOTA_ABILITY_BEHAVIOR_POINT) {
            return NOOP;
        }
        System.out.println( "will cast a spell" );
        System.out.println( "Will try " + a.getName() );
        if (a.getLevel() < 1) {
            System.out.println( "Not learned yet" );
            return NOOP;
        }
        if (a.getCooldownTimeRemaining() > 0f) {
            System.out.println( "On cooldown" );
            return NOOP;
        }
        CAST.setAbility( index );
        if ((a.getBehavior() & Ability.DOTA_ABILITY_BEHAVIOR_UNIT_TARGET) > 0) {
            CAST.setX( -1 );
            CAST.setY( -1 );
            CAST.setZ( -1 );
            CAST.setTarget( world.indexOf( target ) );
        }
        else {
            CAST.setTarget( -1 );
            final float[] pos = target.getOrigin();
            CAST.setX( pos[0] );
            CAST.setY( pos[1] );
            CAST.setZ( pos[2] );
        }

        return CAST;
    }

    @Override
    public LevelUp levelUp() {
        LEVELUP.setAbilityIndex( -1 );

        if (myLevels[0] < 4) {
            LEVELUP.setAbilityIndex( 0 );
        }
        else if (myLevels[1] < 4) {
            LEVELUP.setAbilityIndex( 1 );
        }
        else if (myLevels[2] < 4) {
            LEVELUP.setAbilityIndex( 2 );
        }
        else if (myLevels[3] < 3) {
            LEVELUP.setAbilityIndex( 3 );
        }
        else if (myLevels[4] < 10) {
            LEVELUP.setAbilityIndex( 4 );
        }
        System.out.println( "LevelUp " + LEVELUP.getAbilityIndex() );
        return LEVELUP;
    }

    private Command move( Hero lina, World world ) {
        //Walk up to the nearest enemy
        final Set<BaseEntity> en = findEntitiesInRange( world, lina, Float.POSITIVE_INFINITY, 3 );
        final BaseEntity target = en.stream().sorted( ( e1, e2 ) -> Float.compare( distance( lina, e1 ), distance( lina, e2 ) ) )
                        .filter( f -> f.getClass() != Tower.class ).findFirst().orElse( null );
        if (target == null) {
            //Nothing in range
            System.out.println( "No enemy in sight" );
            return NOOP;
        }
        final BaseNPC targetEntity = (BaseNPC) target;
        final float[] targetPos = targetEntity.getOrigin();
        MOVE.setX( targetPos[0] );
        MOVE.setY( targetPos[1] );
        MOVE.setZ( targetPos[2] );

        System.out.println( "Moving" );

        return MOVE;
    }

    @Override
    public void onChat( ChatEvent e ) {
        switch (e.getText()) {
            case "lina go":
                mode = Mode.ENABLED;
                break;
            case "lina stop":
                shouldRetreat = true;
                mode = Mode.DISABLED;
                break;
            case "lina buy tango":
                shouldBuyTango = true;
                break;
        }
    }

    @Override
    public void reset() {
        System.out.println( "Resetting" );
        myLevels = new int[5];
    }

    private Command retreat( World world ) {
        //Retreat at 30% health
        System.out.println( "Lina is retreating" );
        final BaseNPC fountain = (BaseNPC) world.getEntities().entrySet().stream().filter( p -> p.getValue().getName().equals( "ent_dota_fountain_good" ) )
                        .findAny().get().getValue();
        final float[] targetPos = fountain.getOrigin();
        MOVE.setX( targetPos[0] );
        MOVE.setY( targetPos[1] );
        MOVE.setZ( targetPos[2] );

        return MOVE;
    }

    @Override
    public Select select() {
        SELECT.setHero( MY_HERO_NAME );
        return SELECT;
    }

    @Override
    public Command update( World world ) {
        if (shouldBuyTango) {
            shouldBuyTango = false;
            return buy( "item_tango" );
        }
        if (mode == Mode.DISABLED) {
            if (shouldRetreat) {
                shouldRetreat = false;
                return retreat( world );
            }

            return NOOP;
        }

//        System.out.println( world.getEntities().size() + " present" );
//        world.getEntities().values().stream().filter( e -> e.getClass() == Building.class ).forEach( e -> System.out.println( e ) );
//        world.getEntities().values().stream().filter( e -> e.getClass() == Tower.class ).forEach( e -> System.out.println( e ) );
        final int myIndex = world.searchIndexByName( MY_HERO_NAME );
        if (myIndex < 0) {
            //I'm probably dead
            System.out.println( "I'm dead?" );
            reset();
            return NOOP;
        }

        final Hero lina = (Hero) world.getEntities().get( myIndex );
//        for (final Ability a : lina.getAbilities().values()) {
//            myLevels[a.getAbilityIndex()] = a.getLevel();
//            System.out.println( a );
//        }

        if (lina.getHealth() <= lina.getMaxHealth() * 0.4) {
            return retreat( world );
        }

        final float range = lina.getAttackRange();
        final Set<BaseEntity> e = findEntitiesInRange( world, lina, range, 3 );
        if (!e.isEmpty()) {
            return attack( lina, e, world );
        }
        else {
            return move( lina, world );
        }
    }
}
