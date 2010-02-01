package com.sawdust.games.stop.immutable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class XmlBoard
{
    @XmlValue
    String text;

    @XmlAttribute
    int rows;

    @XmlAttribute
    int cols;

    static final int NON_INITIALIZED = -5;

    public XmlBoard()
    {}
    
    public XmlBoard(Board board)
    {
        rows = board.rows;
        cols = board.cols;
        int[][] matrix = extractMatrix(board);
         text = matrixToString(matrix);
    }

    public int[][] getMatrix()
    {
        int matrix[][] = null;
        String[] lines = text.split("\n");
        int w = lines.length;
        matrix = new int[lines.length][];
        assert(rows == lines.length);
        int h = -1;
        int y = 0;
        for(String line : lines)
        {
            int x = 0;
            String[] tokens = line.split(" ");
            matrix[y] = new int[tokens.length];
            assert(cols == tokens.length);
            for(String token : tokens)
            {
                matrix[y][x++] = Integer.parseInt(token);
            }
            y++;
        }
        return matrix;
    }

    private int[][] extractMatrix(Board board)
    {
        int matrix[][] = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = NON_INITIALIZED;
        for (island isl : board.open)
            for (tokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (island isl : board.islands)
            for (tokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                assert (matrix[i][j] != NON_INITIALIZED);
        return matrix;
    }

    private String matrixToString(int[][] matrix)
    {
        StringBuffer sb = new StringBuffer();
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
