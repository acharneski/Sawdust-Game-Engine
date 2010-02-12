package com.sawdust.gae.logic;

import com.sawdust.engine.model.GameType;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.games.euchre.EuchreGameType;
import com.sawdust.games.go.GoGameType;
import com.sawdust.games.poker.PokerGameType;
import com.sawdust.games.stop.StopGameType;
import com.sawdust.games.wordHunt.WordHuntGameType;

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
                GoGameType.INSTANCE,
                com.sawdust.games.go.view.GoGameType.INSTANCE
        };
    }
}
