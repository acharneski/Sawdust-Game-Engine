package com.sawdust.test.gae;


import java.util.Date;

import org.junit.Test;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.gae.datastore.Games;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.test.Util;

public class Blackjack extends GameTest<BlackjackGame>
{
    public Blackjack()
    {
        super("blackjack");
    }

    private LoadedDeck _deck = new LoadedDeck();
    
    @Override
    protected BlackjackGame getGame(com.sawdust.engine.controller.entities.GameSession session, AccessToken access, Account account) throws GameException
    {
        GameConfig prototypeConfig = BlackjackGameType.INSTANCE.getBaseConfig(account);
        BlackjackGame newGame = (BlackjackGame) Games.NewGame(BlackjackGameType.INSTANCE, prototypeConfig, session, access);
        newGame.setDeck(_deck);
        return newGame;
    }
    
    @Test(timeout = 10000)
    public void testGameWin() throws Exception
    {
        System.out.println("\ntestGameWin()\n");
        Date now = new Date(0);
        _deck.doAddCard(Ranks.Ace, Suits.Clubs);
        _deck.doAddCard(Ranks.Ace, Suits.Clubs);
        _deck.doAddCard(Ranks.King, Suits.Clubs);
        _deck.doAddCard(Ranks.Jack, Suits.Hearts);
        _deck.doAddCard(Ranks.Five, Suits.Clubs);
        _deck.doAddCard(Ranks.Four, Suits.Clubs);
        PlayerContext player1 = _players.get(0);
        Util.assertEqual(player1.account.getBalance(), 10);
        reset();
        
        _session.addPlayer(player1.player);
        _session.doStart();
        reset();
        
        now = Util.printNewMessages(_game, now);
        Util.assertEqual(player1.account.getBalance(), 10);
        
        Util.testGuiCommand(_session, player1.player, "Hit Me");
        reset();
        
        now = Util.printNewMessages(_game, now);
        Util.testGuiCommand(_session, player1.player, "Hit Me");
        reset();
        
        now = Util.printNewMessages(_game, now);
        Util.testGuiCommand(_session, player1.player, "Stay");
        reset();

        now = Util.printNewMessages(_game, now);
        Util.assertMessageFound(_game, "You Lose.");
        Util.assertEqual(player1.account.getBalance(), 10);
    }
}
