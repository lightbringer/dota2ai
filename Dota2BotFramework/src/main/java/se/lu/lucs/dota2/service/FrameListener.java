package se.lu.lucs.dota2.service;

import se.lu.lucs.dota2.framework.bot.Bot.Command;
import se.lu.lucs.dota2.framework.game.World;

@FunctionalInterface
public interface FrameListener {
    Command update( World w );
}
