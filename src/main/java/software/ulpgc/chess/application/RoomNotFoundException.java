package software.ulpgc.chess.application;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(String roomId) {
        super("Room does not exist: " + roomId);
    }
}