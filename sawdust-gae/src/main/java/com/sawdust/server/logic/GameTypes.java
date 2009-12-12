package com.sawdust.server.logic;

import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.euchre.EuchreGameType;
import com.sawdust.engine.game.go.GoGameType;
import com.sawdust.engine.game.poker.PokerGameType;
import com.sawdust.engine.game.stop.StopGameType;
import com.sawdust.engine.game.wordHunt.WordHuntGameType;

public class GameTypes
{
    public static GameType<?> findById(final String gameId)
    {
        for (final GameType<?> game : values())
        {
            if (game.getID().equals(gameId)) return game;
        }
        return null;
    }

    public static GameType<?>[] values()
    {
        return new GameType[]
        {
                BlackjackGameType.INSTANCE, 
                EuchreGameType.INSTANCE, 
                PokerGameType.INSTANCE, 
                WordHuntGameType.INSTANCE, 
                StopGameType.INSTANCE, 
                GoGameType.INSTANCE
        };
    }
}
