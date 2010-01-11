/**
 * 
 */
package com.sawdust.games.euchre;

import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.CommandResult;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexCard;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.game.ActivityEvent;
import com.sawdust.engine.view.game.Message.MessageType;

final class PlayingPhase extends GamePhase
{
    public static final GamePhase INSTANCE = new PlayingPhase();
    
    @Override
    public void doCommand(final EuchreGame game, final EuchreCommand cmd, final Object... params) throws GameException
    {
        switch (cmd)
        {
        case Play:
            playCard(game, (Participant) params[0], (Integer) params[1]);
            break;
        default:
            throw new GameLogicException(String.format("Unknown command %s while in state %s", cmd, this));
        }
    }
    
    protected void endRound(final EuchreGame game) throws GameException
    {
        final Participant winner = game._winningCard.getOwner();
        final int teamNumber = game.getTeamNumber(winner)-1;
        final int endScore = 1+ game.getTeamStatus(teamNumber).currentHandCount;
        game.getTeamStatus(teamNumber).currentHandCount = endScore;
        int totalRounds = ++game._roundNumber;
        game.doAddMessage("<strong>%s (team %s) wins this trick, for a total of %d wins</strong>", game.getDisplayName(winner), teamNumber+1, endScore);
        game.doClearPlayedCards();
        int otherTeam = (0 == teamNumber) ? 1 : 0;
        final int makingTeam = game.getTeamNumber(game._maker)-1;
        final int nonMakingTeam = (0 == makingTeam) ? 1 : 0;
        if (totalRounds >= 5)
        {
            game._roundNumber = 0;
            int affectedTeam;
            if(makingTeam == teamNumber && endScore == 5)
            {
                game.doAddMessage("<strong>Team %s took every trick, and gets %d points.</strong>", teamNumber+1, otherTeam+1, 2);
                game.getTeamStatus(otherTeam).totalPoints += 2;
                affectedTeam = otherTeam;
                game._totalPts += 2;
            }
            else if(makingTeam == teamNumber && endScore >= 3)
            {
                game.doAddMessage("<strong>Team %s wins, and gets %d points.</strong>", teamNumber+1, 1);
                game.getTeamStatus(teamNumber).totalPoints += 1;
                affectedTeam = teamNumber;
                game._totalPts += 1;
            }
            else
            {
                game.doAddMessage("<strong>Team %s was Euchred! Team %s gets %d points.</strong>", makingTeam+1, nonMakingTeam+1, 2);
                game.getTeamStatus(nonMakingTeam).totalPoints += 2;
                affectedTeam = nonMakingTeam;
                game._totalPts += 2;
            }
            game.doSaveState();
            game.doAdvanceTime(1000);
            if(game.getTeamStatus(affectedTeam).totalPoints >= game.getPointGoal())
            {
                game.doAddMessage("<strong>Team %s wins the game!</strong>", affectedTeam+1);
                game.doPayToTeam(affectedTeam+1);
                game.getPlayerManager().resetCurrentPlayer();
                game.setCurrentPhase(EuchreGame.COMPLETE);
                game.doAddMessage("Enter 'Deal' or 'Quit'.");
                
                for(Participant p : game.getTeam(affectedTeam))
                {
                    if(p instanceof Player)
                    {
                        String type = "Win/Euchre";
                        String event = String.format("I won a game of Euchre!");
                        ((Player)p).logActivity(new ActivityEvent(type,event));
                    }
                    
                }
                
            }
            else
            {
                game.setCurrentPhase(EuchreGame.DEALING);
                game.doCommand(EuchreCommand.Deal);
            }
        }
        else
        {
            game.getPlayerManager().setCurrentPlayer(winner);
            game._roundStartPlayer = winner;
            game._winningCard = null;
        }
    }
    
    @Override
    public String getId()
    {
        return "Playing";
    }
    
    @Override
    public ArrayList<GameCommand> getMoves(final Participant access, final EuchreGame game) throws GameException
    {
        final ArrayList<GameCommand> returnValue = new ArrayList<GameCommand>();
        
        for (int cardSlot = 0; cardSlot < EuchreGame.NUMBER_OF_CARDS; cardSlot++)
        {
            final Token token = game.getToken(new IndexPosition(game.getPlayerManager().getCurrentPlayerIndex(), cardSlot));
            if (null != token)
            {
                final String cardStr = Integer.toString(cardSlot);
                returnValue.add(new GameCommand()
                {
                    
                    @Override
                    public String getHelpText()
                    {
                        return null;
                    }
                    
                    @Override
                    public String getCommandText()
                    {
                        return "Play " + ((IndexCard) token).getCard().toString();
                    }
                    
                    @Override
                    public CommandResult doCommand(Participant p, String commandText) throws GameException
                    {
                        com.sawdust.games.euchre.Command.PlayCards.doCommand((Player) p, game.getSession(), cardStr);
                        return new CommandResult<EuchreGame>(game);
                    }
                });
            }
        }
        return returnValue;
    }
    
    protected void playCard(final EuchreGame game, final Participant participant, final int cardIndexToPlay) throws GameException
    {
        /*
         * R-E-0146: Play continues in clockwise order; each player must follow
         * suit if they have a R-E-0147: card of the suit led. The left bower is
         * considered a member of the trump suit R-E-0148: and not a member of
         * its native suit.
         */
        final int currentPlayerIndex = game.getPlayerManager().getCurrentPlayerIndex();
        final IndexCard cardToPlay = (IndexCard) game.getToken(new IndexPosition(currentPlayerIndex, cardIndexToPlay));
        if (null == cardToPlay) /*
                                 * R-E-0103: The primary rule to remember when
                                 * playing euchre is that one is never required
                                 * R-E-0104: to trump, but one is required to
                                 * follow suit if possible to do so: if diamonds
                                 * R-E-0105: are led, a player with diamonds is
                                 * required to play a diamond. This differs from
                                 * R-E-0106: games such as pinochle.
                                 */
        throw new GameLogicException(String.format("Could not find card in slot %d!", cardIndexToPlay));
        
        final Suits leadingSuit = game.getLeadingSuit();
        final boolean isLeadingSuit = (game.getEffectiveSuit(cardToPlay.getCard()) == leadingSuit);
        final boolean canLeadSuit = game.getPlayerCanLead(currentPlayerIndex);
        if (!isLeadingSuit && canLeadSuit) throw new GameLogicException(String.format("You must play a %s", leadingSuit.fullString()));
        
        game.doAddMessage(MessageType.Compact, "Played %s of %s ", cardToPlay.getCard().getRank().name(), cardToPlay.getCard().getSuit().fullString());
        if (game.isWinningCard(cardToPlay))
        {
            game._winningCard = cardToPlay;
            game.doAddMessage(MessageType.Compact, "<strong>(Currently Winning)</strong>");
        }
        game.doAddMessage("");
        
        cardToPlay.setPosition(new IndexPosition(EuchreLayout.POS_IN_PLAY, game._roundCardCount++));
        cardToPlay.setPublic();
        cardToPlay.setMovable(false);
        final Participant gotoNextPlayer = game.getPlayerManager().gotoNextPlayer();
        if (game._roundStartPlayer.equals(gotoNextPlayer))
        {
            endRound(game);
        }
        
        final Participant currentPlayer = game.getPlayerManager().getCurrentPlayer();
        String displayName;
        if (null != currentPlayer)
        {
            displayName = game.getDisplayName(currentPlayer);
        }
        else
        {
            displayName = "<NULL>";
        }
        game.doAddMessage(MessageType.Compact, "It is now %s's turn: ", displayName);
    }
    
    @Override
    public Collection<GameLabel> setupLabels(final EuchreGame game, final Player access)
    {
        final ArrayList<GameLabel> returnValue = new ArrayList<GameLabel>();
        returnValue.addAll(game.getPlayerLabels());
        
        for (int cardSlot = 0; cardSlot < EuchreGame.NUMBER_OF_CARDS; cardSlot++)
        {
            final Token token = game.getToken(new IndexPosition(game.getPlayerManager().getCurrentPlayerIndex(), cardSlot));
            if (null != token)
            {
                final GameLabel label = new GameLabel("PlayCard " + cardSlot, new IndexPosition(EuchreLayout.POS_CARDPLAY_LABEL, cardSlot), "Play");
                // label.setCommand("Play " + cardSlot);
                label.setCommand("Play " + ((IndexCard) token).getCard().toString());
                returnValue.add(label);
            }
        }
        int index = 0;
        
        returnValue.add(new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), "Trump Suit: "
                + game._trumpSuit.fullString()));
        index++;
        
        for (int team = 0; team < 2; team++)
        {
            TeamStatus teamStatus = game.getTeamStatus(team);
            final String scoreString = String.format("Team %d Score: %d points, %d tricks", team + 1, teamStatus.totalPoints, teamStatus.currentHandCount);
            returnValue.add(new GameLabel("GeneralCommand " + index, new IndexPosition(EuchreLayout.POS_GENERAL_CMDS, index), scoreString).setWidth(300));
            index++;
        }
        return returnValue;
    }
}
