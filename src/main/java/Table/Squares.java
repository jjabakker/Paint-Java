package Table;

import tech.tablesaw.api.Table;

import java.io.IOException;

public class Squares extends PaintTable {

    public static void main(String[] args) throws IOException {
        int[] rowCol = getRowAndColumn(10, 10);
        int nRow = rowCol[0];
        int nCol = rowCol[1];

        System.out.println("Row: " + nRow);
        System.out.println("Col: " + nCol);
    }

    public Squares() {
        super();
    }

    public static Squares load(String path) throws IOException {
        Squares instance = new Squares();
        instance.readTable(path);
        return instance;
    }

    public static int[] getRowAndColumn(int squareNr, int nrRowsInSquare) {
        return new int[] { squareNr / nrRowsInSquare, squareNr % nrRowsInSquare };
    }

}