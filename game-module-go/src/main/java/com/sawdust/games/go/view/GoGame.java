package com.sawdust.games.go.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.BaseGame;
import com.sawdust.engine.model.basetypes.TokenGame;
import com.sawdust.engine.model.players.Agent;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.Message;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;
import com.sawdust.games.DateUtil;
import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.BoardPosition;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.GoScore;
import com.sawdust.games.go.model.Island;
import com.sawdust.games.go.model.Move;
import com.sawdust.games.wordHunt.BoardToken;

public abstract class GoGame extends TokenGame
{
    private static final Logger LOG = Logger.getLogger(GoGame.class.getName());
    public static final int NUMBER_OF_PLAYERS = 2;
    public static final int OFFSET_BOARD = 20;
    public static final int ROW_PLAYERTOKEN = -2;
    public static final int ROW_SCORES = -3;

    private static final Position scorePosition = new Position(525, 150);
    private static final Vector scroreOffset = new Vector(0, 35);
    private static final Position basePosition      = new Position(30, 10);
    private static final Vector   columnOffset      = new Vector(50, 0);
    private static final Position playPosition      = new Position(525, 50);
    private static final Vector   playTokenOffset   = new Vector(0, 75);
    private static final Vector   rowOffset         = new Vector(0, 50);
    
    public final int maxRow = 5;
    private IndexPosition _lastPosition;
    private GoBoard board = null;
    
    @Override
    public Collection<GameLabel> getLabels(Player access) throws GameException
    {
        ArrayList<GameLabel> labels = new ArrayList<GameLabel>();
        int card = 0;

        labels.add(new GameLabel("PASS_CMD", new IndexPosition(ROW_SCORES, card++), "Pass").setCommand("Pass"));
        GoScore blackScore = board.getScore(board.getPlayers()[0]);

        if (null != blackScore)
        {
            labels.add(new GameLabel("BLACK_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Prisoners:"));

            labels.add(new GameLabel("BLACK_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(blackScore.prisoners)));

            labels.add(new GameLabel("BLACK_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Territory:"));

            labels.add(new GameLabel("BLACK_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(blackScore.territory)));
        }

        GoScore whiteScore = board.getScore(board.getPlayers()[1]);

        if (null != whiteScore)
        {
            labels.add(new GameLabel("WHITE_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "White Prisoners:"));

            labels.add(new GameLabel("WHITE_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(whiteScore.prisoners)));

            labels.add(new GameLabel("WHITE_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "White Territory:"));

            labels.add(new GameLabel("WHITE_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(whiteScore.territory)));
        }
        return labels;
    }

    @Override
    public Position getPosition(IndexPosition key, Player access) throws GameException
    {
        if (null == key) return null;
        
        if ((key.getCurveIndex() >= 0) && (key.getCurveIndex() < maxRow))
        {
            return basePosition.add(rowOffset.scale(key.getCurveIndex())).add(columnOffset.scale(key.getCardIndex()));
        }
        
        final int offsetIndex = key.getCurveIndex() - OFFSET_BOARD;
        if ((offsetIndex >= 0) && (offsetIndex < maxRow))
        {
           final Position add = basePosition.add(rowOffset.scale(offsetIndex)).add(columnOffset.scale(key.getCardIndex()));
           add.setZ(-1);
           return add;
        }

        if (key.getCurveIndex() == ROW_SCORES) 
        {
            return scorePosition.add(scroreOffset.scale(key.getCardIndex()));
        }
        
        if (key.getCurveIndex() == ROW_PLAYERTOKEN)
        {
            return playPosition.add(playTokenOffset.scale(key.getCardIndex()));
        }

        return null;
    }

    @Override
    public ArrayList<Token> getTokens()
    {
        ArrayList<Token> returnValue = new ArrayList<Token>();
        int cardIdCounter = 0;
        IndexPosition lastPosition = _lastPosition;
        if (null != lastPosition)
        {
            BoardToken token = new BoardToken(0X1001, "GO1", "GO:HIGHLIGHT", null, null, false, lastPosition);
            token.getPosition().setZ(2);
            token.setText("Last-moved piece");
            returnValue.add(token);
        }

        String[] playerArt = new String[]{"GO:BLACK", "GO:WHITE"};
        cardIdCounter = 0x3000;
        for(Island i : board.getBoard().islands)
        {
            for(BoardPosition t : i.tokens)
            {
                IndexPosition position = new IndexPosition(t.x, t.y, 3);
                int idx = i.player.value-1;
                BoardToken token = new BoardToken(++cardIdCounter, "GO1", playerArt[idx], getParticipant(idx), null, false, position);
                token.getPosition().setZ(3);
                returnValue.add(token);
                
            }
        }
        
        for (int i = 0; i < maxRow; i++)
        {
            for (int j = 0; j < maxRow; j++)
            {
                IndexPosition position = new IndexPosition(i + OFFSET_BOARD, j, 0);
                BoardToken token = new BoardToken(++cardIdCounter, "GO1", "GO:BOARD", null, null, false, position);
                token.getPosition().setZ(1);
                token.setText("Board Tile");
                returnValue.add(token);
            }
        }

        cardIdCounter = 0x4000;
        ArrayList<IndexPosition> openPos = new ArrayList<IndexPosition>();
        for(Island i : board.getBoard().open)
        {
            for(BoardPosition t : i.tokens)
            {
                openPos.add(new IndexPosition(t.x, t.y));
            }
        }
        
        final IndexPosition position = new IndexPosition(ROW_PLAYERTOKEN, 0, 1);
        int i = ((GoPlayer)board.getCurrentPlayer()).value-1;
        Participant participant = getParticipant(i);
        String art = playerArt[i];
        final BoardToken token = new BoardToken(++cardIdCounter, "GO1", art, participant, null, true, position);
        token.getPosition().setZ(4);
        token.setText("Place this piece on the board to move");
        HashMap<IndexPosition, String> moveCommands = token.getMoveCommands();
        for (final IndexPosition pos : openPos)
        {
            final String cmd = String.format("Move %d, %d", pos.getCurveIndex(), pos.getCardIndex());
            moveCommands.put(pos, cmd);
        }
        returnValue.add(token);

        return returnValue;
    }

    private Participant getParticipant(int i)
    {
        GoPlayer goPlayer = board.getPlayers()[i];
        return getParticipant(goPlayer);
    }

    private Participant getParticipant(final GoPlayer goPlayer)
    {
        ArrayList<Participant> ps = new ArrayList<Participant>(_displayFilter.keySet());
        int idx = goPlayer.value-1;
        if(idx >= ps.size())
        {
            final com.sawdust.games.go.controller.ai.GoSearchAgent goSearchAgent = new com.sawdust.games.go.controller.ai.GoSearchAgent(15,3);
            return new Agent<GoGame>(goPlayer.getName()){
                @Override
                public void Move(GoGame game, Participant participant) throws GameException
                {
                    Move selectMove = goSearchAgent.selectMove(goPlayer, game.board, DateUtil.future(500));
                    LOG.fine("Agent Move: "+selectMove.toString());
                    game.doMove((BoardMove) selectMove);
                }
            };
        }
        Participant participant = ps.get(idx);
        return participant;
    }

    protected GoGame()
    {
        super();
    }

    public GoGame(GameConfig config)
    {
        super(config);
    }

    @Override
    public void doReset()
    {
        board = new GoBoard(maxRow,maxRow);
    }

    @Override
    public abstract GameSession getSession();

    @Override
    public void doStart() throws GameException
    {
        doReset();
    }

    @Override
    public void doUpdate() throws GameException
    {
    }

    @Override
    public Participant getCurrentPlayer()
    {
        GoPlayer currentPlayer = (GoPlayer) board.getCurrentPlayer();
        Participant participant = getParticipant(currentPlayer);
        return participant;
    }

    @Override
    public String getDisplayName(Participant userId)
    {
        return userId.getId();
    }

    @Override
    public GameType<?> getGameType()
    {
        return new GoGameType();
    }

    @Override
    public ArrayList<GameCommand> getMoves(Participant access) throws GameException
    {
        if(null == board) 
        {
            this.doStart(); //HACK
        }
        ArrayList<GameCommand> arrayList = new ArrayList<GameCommand>();
        assert (access.getId().equals(board.getCurrentPlayer()));
        int moveId = 0;
        for (final BoardMove move : board.getMoves(board.getCurrentPlayer()))
        {
            final String moveString = GoGame.this.getMoveString(move);
            arrayList.add(new GameCommand()
            {
                @Override
                public String getHelpText()
                {
                    return "Help not availible";
                }

                @Override
                public String getCommandText()
                {
                    return moveString;
                }

                @Override
                public boolean doCommand(Participant access, String commandText) throws GameException
                {
                    assert (access.getId().equals(GoGame.this.getParticipant((GoPlayer)board.getCurrentPlayer())));
                    if(!moveString.equals(commandText)) return false;
                    GoGame.this.doMove(move);
                    saveState();
                    return true;
                }
            });
        }
        return arrayList;
    }

    protected void doMove(BoardMove move) throws GameException
    {
        LOG.fine("Moving: " + move.toString());
        board = board.doMove(move);

        if(null != move.position)
        {
            addMessage(new Message(String.format("Player %d moves at %d,%d", move.player.value, move.position.x, move.position.y)));
        }
        else
        {
            addMessage(new Message(String.format("Player %d passed", move.player.value)));
        }
        
        GoPlayer currentPlayer = (GoPlayer)board.getCurrentPlayer();
        if(null == currentPlayer)
        {
            GoPlayer winner = (GoPlayer) board.getWinner();
            addMessage(new Message(String.format("Player %d won", winner.value)));
        }
        else
        {
            Participant participant = getParticipant(currentPlayer);
            LOG.fine(board.toXmlString());
            if(participant instanceof Agent<?>)
            {
                GoGame.this.saveState();
                LOG.fine("Agent moving...");
                ((Agent) participant).Move(this, participant);
            }
            LOG.fine("Finished Move");
        }
        
    }

    protected String getMoveString(BoardMove move)
    {
        if(null == move.position) return "Pass";
        return String.format("Move %d, %d", move.position.x, move.position.y);
    }

    @Override
    public int getUpdateTime()
    {
        return 90;
    }

}
