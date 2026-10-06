package software.ulpgc.chess;

public record Square(File file, Rank rank) {

    public static Square at(String square){

        File file = File.valueOf(square.substring(0,1).toUpperCase());
        Rank rank = Rank.valueOf("R" + square.substring(1));

        return new Square(file, rank);
    }
}
