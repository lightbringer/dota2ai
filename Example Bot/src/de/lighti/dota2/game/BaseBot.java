package de.lighti.dota2.game;

import de.lighti.dota2.bot.BotCommands.Attack;
import de.lighti.dota2.bot.BotCommands.Buy;
import de.lighti.dota2.bot.BotCommands.Cast;
import de.lighti.dota2.bot.BotCommands.LevelUp;
import de.lighti.dota2.bot.BotCommands.Move;
import de.lighti.dota2.bot.BotCommands.Noop;
import de.lighti.dota2.bot.BotCommands.Select;
import de.lighti.dota2.bot.BotCommands.Sell;
import de.lighti.dota2.bot.BotCommands.UseItem;

public abstract class BaseBot implements Bot {
    protected Buy BUY = new Buy();
    protected Sell SELL = new Sell();
    protected Select SELECT = new Select();
    protected UseItem USE_ITEM = new UseItem();
    protected Move MOVE = new Move();
    protected Noop NOOP = new Noop();
    protected Attack ATTACK = new Attack();
    protected LevelUp LEVELUP = new LevelUp();
    protected Cast CAST = new Cast();

    @Override
    public void onChat( ChatEvent e ) {
        System.out.println( e.getPlayer() + " said: " + e.getText() );

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

}
