package de.lighti.dota2.bot;

import de.lighti.dota2.game.Bot.Command;

@SuppressWarnings( "unused" ) //Setters are only resolved via reflection by the JSON converter
public final class BotCommands {
    public static class Attack implements Command {
        private int target;
        private COMMAND_CODE command = COMMAND_CODE.ATTACK;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public int getTarget() {
            return target;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setTarget( int target ) {
            this.target = target;
        }
    }

    public static class Buy implements Command {
        private COMMAND_CODE command = COMMAND_CODE.BUY;
        private String item;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public String getItem() {
            return item;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setItem( String item ) {
            this.item = item;
        }

    }

    public static class Cast implements Command {
        private COMMAND_CODE command = COMMAND_CODE.CAST;
        private float x;
        private float y;
        private float z;
        private int target;
        private int ability;

        public int getAbility() {
            return ability;
        }

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public int getTarget() {
            return target;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public void setAbility( int ability ) {
            this.ability = ability;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setTarget( int target ) {
            this.target = target;
        }

        public void setX( float x ) {
            this.x = x;
        }

        public void setY( float y ) {
            this.y = y;
        }

        public void setZ( float z ) {
            this.z = z;
        }

    }

    public static class LevelUp {
        private int abilityIndex;

        public int getAbilityIndex() {
            return abilityIndex;
        }

        public void setAbilityIndex( int abilityIndex ) {
            this.abilityIndex = abilityIndex;
        }

    }

    public static class Move implements Command {
        private float x;
        private float y;
        private float z;

        private COMMAND_CODE command = COMMAND_CODE.MOVE;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setX( float x ) {
            this.x = x;
        }

        public void setY( float y ) {
            this.y = y;
        }

        public void setZ( float z ) {
            this.z = z;
        }

    }

    public static class Noop implements Command {

        private COMMAND_CODE command = COMMAND_CODE.NOOP;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

    }

    public static class Select implements Command {
        private String hero;
        private COMMAND_CODE command = COMMAND_CODE.SELECT;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public String getHero() {
            return hero;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setHero( String hero ) {
            this.hero = hero;
        }

    }

    public static class Sell implements Command {
        private COMMAND_CODE command = COMMAND_CODE.SELL;
        private int slot;

        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public int getSlot() {
            return slot;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setSlot( int slot ) {
            this.slot = slot;
        }
    }

    public static class UseItem implements Command {
        private COMMAND_CODE command = COMMAND_CODE.USE_ITEM;
        private int slot;

        //TODO implement items that can be used on something, somewhere or somebody
        @Override
        public COMMAND_CODE getCommand() {
            return command;
        }

        public int getSlot() {
            return slot;
        }

        private void setCommand( COMMAND_CODE command ) {
            this.command = command;
        }

        public void setSlot( int slot ) {
            this.slot = slot;
        }

    }
}
