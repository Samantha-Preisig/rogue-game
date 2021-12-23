package rogue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TerminalPosition;

import javax.swing.JFrame;
import java.awt.Container;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.io.IOException;

/*Handling NotEnoughDoorsException is not entirely working/complete --> complete this for A3*/

public class WindowUI extends JFrame {

    private SwingTerminal terminal;
    private TerminalScreen screen;
    public static final int WIDTH = 700;
    public static final int HEIGHT = 800;
    /*Screen buffer dimensions are different than terminal dimensions*/
    public static final int COLS = 80;
    public static final int ROWS = 24;
    private final char startCol = 0;
    private final char msgRow = 1;
    private final char roomRow = 3;

    /**
     *Constructor that sets up terminal and other essentials in order for the game to run smoothly.
     **/
    public WindowUI() {
        super();
        setWindowDefaults(getContentPane());
        setTerminal(getContentPane());
        pack();
        start();
    }

    private void setWindowDefaults(Container contentPane) {
        setTitle("Rogue!");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        contentPane.setLayout(new BorderLayout());
    }

    private void setTerminal(Container contentPane) {
        terminal = new SwingTerminal();
        contentPane.add(terminal, BorderLayout.CENTER);
    }

    private void start() {
        try {
            screen = new TerminalScreen(terminal);
            //screen = new VirtualScreen(baseScreen);
            screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
            screen.startScreen();
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Prints a string to the screen starting at the indicated column and row.
     *@param toDisplay the string to be printed
     *@param column the column in which to start the display
     *@param row the row in which to start the display
     **/
    public void putString(String toDisplay, int column, int row) {
        Terminal t = screen.getTerminal();
        try {
            t.setCursorPosition(column, row);
            for (char ch: toDisplay.toCharArray()) {
                t.putCharacter(ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Changes the message at the top of the screen for the user.
     *@param msg the message to be displayed
     **/
    public void setMessage(String msg) {
        putString("                                                                                ", 1, 1);
        putString(msg, startCol, msgRow);
    }

    /**
     *Redraws the whole screen including the room and the message.
     *@param message the message to be displayed at the top of the room
     *@param room the room map to be drawn
     **/
    public void draw(String message, String room) {
        try {
            setMessage(message);
            putString(room, startCol, roomRow);
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Obtains input from the user and returns it as a char.  Converts arrow
     *keys to the equivalent movement keys in rogue.
     *@return the ascii value of the key pressed by the user
     **/
    public char getInput() {
        KeyStroke keyStroke = null;
        char returnChar = ' ';
        while (keyStroke == null) {
            try {
                keyStroke = screen.pollInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            returnChar = Rogue.DOWN;
        } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            returnChar = Rogue.UP;
        } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
            returnChar = Rogue.LEFT;
        } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
            returnChar = Rogue.RIGHT;
        } else if (keyStroke.getKeyType() == KeyType.Character) {
            if (keyStroke.getCharacter() == 's') {
                returnChar = Rogue.DOWN;
            } else if (keyStroke.getCharacter() == 'w') {
                returnChar = Rogue.UP;
            } else if (keyStroke.getCharacter() == 'a') {
                returnChar = Rogue.LEFT;
            } else if (keyStroke.getCharacter() == 'd') {
                returnChar = Rogue.RIGHT;
            } else {
                returnChar = keyStroke.getCharacter();
            }
        }
        return returnChar;
    }

    /**
     *Updates the next instance of the terminal output.
     *The update can either be a new message or a new message with a new instance of the room
     *@param theGameUI (WindowUI) passing access to terminal manipulation
     *@param theGame (Rogue) passing access to the rogue object created in the main method
     *@param userInput (char) to determine the next output/instance of the game
     **/
    public static void updateGame(WindowUI theGameUI, Rogue theGame, char userInput) {
        String message = "";
        String roomInstance = ""; /*If a custom character is inputted ('i', 'r'), the roomInstance stays the same, this
        variable will hold on to the last instance of the room*/
        /*Ask the game if the user can move there*/
        if (userInput != 'i' || userInput != 'r') { /*custom input characters that display information instead of a
            new instance of the room*/
            try {
                message = theGame.makeMove(userInput);
                roomInstance = theGame.getNextDisplay();
                theGameUI.draw(message, roomInstance);
            } catch (InvalidMoveException badMove) {
                message = "You cannot make that move";
                theGameUI.setMessage(message);
                theGameUI.draw(message, theGame.getNextDisplay());
            }
        /*The player requests additional information*/
        } else if (userInput == 'i' || userInput == 'r') {
            try {
                message = theGame.makeMove(userInput);
                theGameUI.draw(message, roomInstance);
            } catch (InvalidMoveException noFunctionality) {
                message = "";
            }
        }
    }

    /**
     *The controller method for making the game logic work.
     *@param args command line parameters
     **/
    public static void main(String[] args) {

        String configurationFileLocation = "fileLocations.json";

        /*Parse the json files*/
        RogueParser parser = new RogueParser(configurationFileLocation);

        /*Allocate memory for the GUI*/
        WindowUI theGameUI = new WindowUI();

        /*Allocate memory for the game and set it up*/
        Rogue theGame = new Rogue(parser);

        /*Set up the initial game display*/
        Player thePlayer = new Player("Samantha");
        theGame.setPlayer(thePlayer);

        String message = "Welcome to my Rogue game";
        theGameUI.draw(message, theGame.getNextDisplay());
        theGameUI.setVisible(true);

        char userInput = 'h'; /*Arbitrary start input*/
        while (userInput != 'q') {
            /*Get input from the user*/
            userInput = theGameUI.getInput();
            message = "";
            updateGame(theGameUI, theGame, userInput);
        }
        message = theGame.gameOutro();
        theGameUI.draw(message, theGame.getNextDisplay());
    }
}
