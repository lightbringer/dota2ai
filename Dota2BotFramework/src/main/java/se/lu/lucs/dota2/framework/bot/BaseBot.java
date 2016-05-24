package se.lu.lucs.dota2.framework.bot;

import se.lu.lucs.dota2.framework.bot.BotCommands.Attack;
import se.lu.lucs.dota2.framework.bot.BotCommands.Buy;
import se.lu.lucs.dota2.framework.bot.BotCommands.Cast;
import se.lu.lucs.dota2.framework.bot.BotCommands.LevelUp;
import se.lu.lucs.dota2.framework.bot.BotCommands.Move;
import se.lu.lucs.dota2.framework.bot.BotCommands.Noop;
import se.lu.lucs.dota2.framework.bot.BotCommands.Select;
import se.lu.lucs.dota2.framework.bot.BotCommands.Sell;
import se.lu.lucs.dota2.framework.bot.BotCommands.UseItem;
import se.lu.lucs.dota2.framework.game.ChatEvent;

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
