package common;

public class Protocol {
    public static final String WELCOME = "WELCOME";
    public static final String MOVE = "MOVE";
    public static final String YOUR_TURN = "YOUR_TURN";
    public static final String WAIT = "WAIT";
    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String QUIT = "QUIT";

    public static final String COLOR = "COLOR";      // e.g. "COLOR WHITE"
    public static final String START = "START";
    public static final String OPPONENT_LEFT = "OPPONENT_LEFT";
    public static final String WHITE = "WHITE";
    public static final String BLACK = "BLACK";

    public static final String CREATE= "CREATE";          // client → server: I want to create a room
    public static final String ROOM     = "ROOM";            // server → client: "ROOM ABC123"
    public static final String JOIN     = "JOIN";            // client → server: "JOIN ABC123"
    public static final String BAD_ROOM = "BAD_ROOM";        // server → client: code not found

}
