package de.lighti.dota2.game;

import de.lighti.dota2.bot.BotCommands.LevelUp;
import de.lighti.dota2.bot.BotCommands.Select;

public interface Bot {
    public interface Command {
        enum COMMAND_CODE {
            NOOP, MOVE, ATTACK, CAST, BUY, SELL, USE_ITEM, SELECT
        }

        COMMAND_CODE getCommand();
    }

    LevelUp levelUp();

    void onChat( ChatEvent e );

    void reset();

    Select select();

    Command update( World world );
}
