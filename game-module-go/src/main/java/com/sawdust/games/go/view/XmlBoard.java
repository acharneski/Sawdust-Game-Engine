package com.sawdust.games.go.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.sawdust.games.go.model.Board;
import com.sawdust.games.go.model.BoardPosition;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.Island;


@XmlRootElement
public class XmlBoard
{
    @XmlValue
    String text;

    @XmlAttribute
    public
    int rows;

    @XmlAttribute
    public
    int cols;

    static final GoPlayer NON_INITIALIZED = null;

    public XmlBoard()
    {}
    
    public XmlBoard(Board board)
    {
        rows = board.rows;
        cols = board.cols;
        GoPlayer[][] matrix = extractMatrix(board);
         text = matrixToString(matrix);
    }

    public GoPlayer[][] getMatrix()
    {
        GoPlayer[][] matrix = null;
        String[] lines = text.split("\n");
        int w = countNonEmpty(lines);
        matrix = new GoPlayer[lines.length][];
        assert(rows == w);
        int y = 0;
        for(String line : lines)
        {
            if(line.isEmpty()) continue;
            int x = 0;
            String[] tokens = line.split(" ");
            int h = countNonEmpty(tokens);
            matrix[y] = new GoPlayer[h];
            assert(cols == h);
            for(String token : tokens)
            {
                if(token.isEmpty()) continue;
                matrix[y][x++] = GoPlayer.parse(token);
            }
            y++;
        }
        return matrix;
    }

    private int countNonEmpty(String[] lines)
    {
        int r = 0;
        for(String s : lines) if(!s.isEmpty()) r++;
        return r;
    }

    private GoPlayer[][] extractMatrix(Board board)
    {
        GoPlayer matrix[][] = new GoPlayer[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = NON_INITIALIZED;
        for (Island isl : board.open)
            for (BoardPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (Island isl : board.islands)
            for (BoardPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                assert (matrix[i][j] != NON_INITIALIZED);
        return matrix;
    }

    private String matrixToString(GoPlayer[][] matrix)
    {
        StringBuffer sb = new StringBuffer("\n");
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                sb.append(matrix[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        String txtMatrix = sb.toString();
        return txtMatrix;
    }

}
