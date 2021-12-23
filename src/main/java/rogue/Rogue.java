package rogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Point;
import java.util.Random;
import java.util.Iterator;

public class Rogue {

    public static final char UP = 'h';
    public static final char DOWN = 'l';
    public static final char LEFT = 'j';
    public static final char RIGHT = 'k';

    public static final char INVENTORY = 'i';
    public static final char ROOMID = 'r';

    private int maxDoors;

    private ArrayList<Room> allRooms = new ArrayList<Room>();
    private ArrayList<Item> allItems = new ArrayList<Item>();
    private HashMap<String, Character> symbols = new HashMap<>();
    private int totalRooms = 0;
    private String displayAllRooms = "";
    private String nextDisplay = "";
    private String message = "";
    private Player player;
    private RogueParser parser;

    private Room room;
    private Door door;

    /**
     * Default constructor.
     */
    public Rogue() {
        player = new Player();
    }

    /**
     * Constructor that takes a RogueParser to further distribute information from a given json file.
     * @param theDungeonInfo (RogueParser) parser from RogueParser
     */
    public Rogue(RogueParser theDungeonInfo) {
        parser = theDungeonInfo;
        player = new Player();
        Map roomInfo = parser.nextRoom();
        while (roomInfo != null) {
            addRoom(roomInfo);
            roomInfo = parser.nextRoom();
            totalRooms++;
        }
        setDoorConnections();
        try {
            verifyRooms();
            //setDoorConnections();
        } catch (InvalidDungeonException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        Map itemInfo = parser.nextItem();
        while (itemInfo != null) {
            addItem(itemInfo);
            itemInfo = parser.nextItem();
        }
        nextDisplay = room.displayRoom();
    }

    /**
     * Verifies the given json file by evaluating each room for specific exception cases.
     * @throws InvalidDungeonException if rooms are invalid and cannot be corrected
     */
    public void verifyRooms() throws InvalidDungeonException {
        Room currentRoom = null;
        try {
            for (Room currRoom : allRooms) {
                currentRoom = currRoom;
                currRoom.verifyRoom();
            }
        } catch (NotEnoughDoorsException e) {
            for (Room currRoom : allRooms) {
                /*There can only be one door on each side of the room (4 rooms total)*/
                if (currRoom.getDoors().size() != maxDoors) {
                    connectRooms(currentRoom, currRoom); /*Correct this error*/
                    verifyRooms(); /*Recursively call verifyRooms() until there is enough doors*/
                    return;
                }
            }
            /*Game cannot be played with file provided*/
            throw new InvalidDungeonException("Dungeon file cannot be used (incorrect or insufficient information).");
        }
    }

    /**
     * Creates two doors that connect two rooms (initial room and connected room).
     * @param initialRoom (Room) initial room
     * @param connectedRoom (Room) connected room
     */
    private void connectRooms(Room initialRoom, Room connectedRoom) {
        int initialWallPosition = 0;
        int connectedWallPosition = 0;
        String direction = connectedRoom.findFreeDoorDirection();
        Random rand = new Random();

        while (initialWallPosition == 0 && connectedWallPosition == 0) { /*A newly generated door cannot be in the
            corners*/
            if (direction == "N" || direction == "S") {
                initialWallPosition = rand.nextInt(initialRoom.getWidth() - 2);
                connectedWallPosition = rand.nextInt(connectedRoom.getWidth() - 2);
            } else if (direction == "E" || direction == "W") {
                initialWallPosition = rand.nextInt(initialRoom.getHeight() - 2);
                connectedWallPosition = rand.nextInt(connectedRoom.getHeight() - 2);
            }
        }

        if (getOtherDirection(direction) != null) {
            Door initialDoor = new Door(initialRoom, initialWallPosition, connectedRoom);
            initialRoom.setDoor(getOtherDirection(direction), initialDoor);
            Door connectedDoor = new Door(initialRoom, connectedWallPosition, connectedRoom);
            connectedRoom.setDoor(getOtherDirection(direction), connectedDoor);
        }
    }

    /**
     * Accessor method to access the opposing direction of the initial direction passed in.
     * @param initialDirection (String) initial direction, i.e. "N", "S", "E", or "W"
     * @return (String) other direction (pairs with "N", "S", "E", or "W")
     */
    private String getOtherDirection(String initialDirection) {
        if (initialDirection == "N") {
            return "S";
        } else if (initialDirection == "S") {
            return "N";
        } else if (initialDirection == "E") {
            return "W";
        } else if (initialDirection == "W") {
            return "E";
        }
        return null;
    }

    /**
     * Mutator method which finds the connectedId in order to connect two rooms.
     */
    public void setDoorConnections() {
        int findMe;
        for (Room currRoom : allRooms) {
            HashMap<String, Door> doors = currRoom.getDoors();
            for (Door currDoor : doors.values()) {
                findMe = currDoor.getConnectedId();
                for (Room search : allRooms) {
                    if (findMe == search.getId()) {
                        currDoor.connectRoom(search);
                    }
                }
            }
        }
    }

    /**
     * Sets up each room with all required information (dimensions, items, id, etc) and adds to the ArrayList of all
     * rooms.
     * @param toAdd (Map<String, String>) a map holding the tag (key) and the information (value) of an individual
     * item of information
     */
    public void addRoom(Map<String, String> toAdd) {
        Room currRoom = new Room(parser);

        int roomWidth = Integer.parseInt(toAdd.get("width").toString());
        int roomHeight = Integer.parseInt(toAdd.get("height").toString());
        int roomID = Integer.parseInt(toAdd.get("id").toString());
        String roomStart = toAdd.get("start");

        setUpRoom(currRoom, roomWidth, roomHeight, roomID, roomStart);
        createRoomDoors(currRoom, toAdd);

        allRooms.add(currRoom);
    }

    /**
     * Uses room mutator methods to initialize the room given all the information passed in.
     * @param currRoom (Room) room being initialized
     * @param roomWidth (int) room's width dimension
     * @param roomHeight (int) room's height dimension
     * @param roomID (int) room's id
     * @param roomStart (String) boolean information on player's whether the player is starting in the room
     */
    private void setUpRoom(Room currRoom, int roomWidth, int roomHeight, int roomID, String roomStart) {
        currRoom.setPlayer(player);
        currRoom.setWidth(roomWidth);
        currRoom.setHeight(roomHeight);
        currRoom.setId(roomID);
        currRoom.setIsPlayerInRoom(Boolean.parseBoolean(roomStart));
        if (currRoom.getIsPlayerInRoom()) {
            room = currRoom; /*The player will only ever be in one room initially (and at a time), this is the "start
            room"*/
        }
    }

    /**
     * Creates doors for the room passed in.
     * @param currRoom (Room) room that needs doors to be created
     * @param toAdd (Map<String, String>) a map holding the tag (key) and the information (value) of an individual
     * item of information (direction tags and wall positions are accessed)
     */
    private void createRoomDoors(Room currRoom, Map<String, String> toAdd) {
        /*If direction is -1, a door does not exist in that direction*/
        int wallPos = -1;
        int connectID = -1;

        if (!((toAdd.get("N").toString()).equals("-1"))) {
            wallPos = Integer.parseInt(toAdd.get("N").toString());
            connectID = Integer.parseInt(toAdd.get("Nid").toString());
            Door nDoor = new Door(currRoom, wallPos, connectID);
            currRoom.setDoor("N", nDoor);
        }
        if (!((toAdd.get("S").toString()).equals("-1"))) {
            wallPos = Integer.parseInt(toAdd.get("S").toString());
            connectID = Integer.parseInt(toAdd.get("Sid").toString());
            Door sDoor = new Door(currRoom, wallPos, connectID);
            currRoom.setDoor("S", sDoor);
        }
        if (!((toAdd.get("E").toString()).equals("-1"))) {
            wallPos = Integer.parseInt(toAdd.get("E").toString());
            connectID = Integer.parseInt(toAdd.get("Eid").toString());
            Door eDoor = new Door(currRoom, wallPos, connectID);
            currRoom.setDoor("E", eDoor);
        }
        if (!((toAdd.get("W").toString()).equals("-1"))) {
            wallPos = Integer.parseInt(toAdd.get("W").toString());
            connectID = Integer.parseInt(toAdd.get("Wid").toString());
            Door wDoor = new Door(currRoom, wallPos, connectID);
            currRoom.setDoor("W", wDoor);
        }
    }

    /**
     * Uses item mutator methods to initialize the item given all the information passed in.
     * @param currItem (Item) item being initialized
     * @param itemLocation (int) item's point (x, y) location
     * @param itemID (int) item's id
     * @param itemName (String) item's name
     * @param itemType (String) item's type
     */
    private void setUpItem(Item currItem, Point itemLocation, int itemID, String itemName, String itemType) {
        currItem.setXyLocation(itemLocation);
        currItem.setId(itemID);
        currItem.setName(itemName);
        currItem.setType(itemType);
    }


    /**
     * Sets up each item with all required information (location, name, type, etc) and adds to the ArrayList of all
     * items.
     * @param toAdd (Map<String, String>) a map holding the tag (key) and the information (value) of an individual
     * item of information
     */
    public void addItem(Map<String, String> toAdd) {
        boolean exception;
        Item currItem = new Item();

        int itemID = Integer.parseInt(toAdd.get("id").toString());
        String itemName = toAdd.get("name");
        String itemType = toAdd.get("type");
        int itemRoomID = Integer.parseInt(toAdd.get("room").toString());
        if (itemRoomID != -1) {
            int itemXLocation = Integer.parseInt(toAdd.get("x").toString());
            int itemYLocation = Integer.parseInt(toAdd.get("y").toString());
            Point itemLocation = new Point(itemXLocation, itemYLocation);
            setUpItem(currItem, itemLocation, itemID, itemName, itemType);
            allItems.add(currItem);

            for (Room currRoom : allRooms) { /*Adding item to the room it belongs to*/
                if (currRoom.getId() == itemRoomID) {
                    exception = true;
                    while (exception) {
                        try {
                            currRoom.addItem(currItem);
                            exception = false;
                        } catch (ImpossiblePositionException e) { /*This (try/catch) could be implemented recursively
                            if there is a posibility that the room could contain no empty tiles*/
                            currItem.setXyLocation(currRoom.getEmptyTile()); /*Get an empty/safe (x, y) location and set
                            that as currItem's new (x, y) location*/
                            continue; /*Loop again and try to add item with new location*/
                        } catch (NoSuchItemException e) {
                            int i = 0;
                            for (Item item : allItems) {
                                if (item.getId() == currItem.getId()) {
                                    allItems.remove(i);
                                }
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Evaluates the requested position given by the player's input and calculates the outcome of that move if valid.
     * If the input is requesting information, it will display the requested information (i.e. inventory or room number)
     * @param input (char) player's input
     * @return (String) message to narrate what the player's intentions are
     * @throws InvalidMoveException if the player requests a position that is illegal (such as moving into a wall)
     */
    public String makeMove(char input) throws InvalidMoveException {
        message = ""; /*Clear message*/
        if (input == INVENTORY || input == ROOMID) { /*Player is requesting information, not to make a move*/
            return getInfo(input);
        }

        /*Player's requested position*/
        Point tempPlayerPos = getPlayerReqPos(input);
        int tempXPos = (int) tempPlayerPos.getX();
        int tempYPos = (int) tempPlayerPos.getY();

        if (isMoveValid(tempXPos, tempYPos)) {
            if (room.itemCheck(tempYPos, tempXPos)) {
                String itemName = pickUpItem(tempPlayerPos, tempXPos, tempYPos);
                return "You picked up " + itemName + "!";
            } else if (room.doorCheck(tempYPos, tempXPos)) {
                String oppositeDir = null;

                if (oppositeDirWEDoorSet(tempXPos) != null) {
                    oppositeDir = oppositeDirWEDoorSet(tempXPos);
                } else if (oppositeDirNSDoorSet(tempYPos) != null) {
                    oppositeDir = oppositeDirNSDoorSet(tempYPos);
                }

                ArrayList<Room> connectedRooms = door.getConnectedRooms();
                if (connectedRooms.size() == 2) { /*If connectedRooms ArrayList is of size 2, that means the ArrayList
                    contains the initial and it's connected room, therefore the door has a connection*/
                    room.setIsPlayerInRoom(false); /*Player is no longer in initial room after requested position*/

                    if (door.getOtherRoom(room) != null) { /*room = next room (could equal null if no connection)*/
                        room = door.getOtherRoom(room);
                        room.setIsPlayerInRoom(true); /*Player is in next room (which is now the current room)*/
                        newPlayerPos(posNextRoom(tempPlayerPos, oppositeDir, tempXPos, tempYPos));
                        return "You have entered room " + room.getId();
                    } else {
                        throw new InvalidMoveException(); /*No connection to another room*/
                    }
                }
            } else {
                /*Update player's point position (tempPlayerPos)*/
                tempPlayerPos.setLocation(tempXPos, tempYPos);
                newPlayerPos(tempPlayerPos);
                return message;
            }
        } else {
            throw new InvalidMoveException();
        }
        return "You pressed " + input;
    }

    /**
     * Gather's requested information (either the player's inventory or the room number).
     * @param input (char) player's input
     * @return (String) the information requested
     */
    private String getInfo(char input) {
        if (input == INVENTORY) {
            ArrayList<Item> playerItems = player.getInventory();
            if (playerItems.size() == 0) {
                message = "You do not have anything in your inventory";
            } else {
                for (int i = 0; i < (playerItems.size() - 1); i++) {
                    message += playerItems.get(i).getName() + ", ";
                }
                message += playerItems.get(playerItems.size() - 1).getName();
                return "Inventory: " + message;
            }
        } else if (input == ROOMID) {
            return "You are in Room " + room.getId() + " (total rooms: " + totalRooms + ")";
        }
        return null;
    }

    /**
     * Return's player's requested point (x, y) location.
     * @param input (char) player's input
     * @return (Point) player's requested point (x, y) location
     */
    private Point getPlayerReqPos(char input) {
        /*Player's initial position*/
        int xPos = (int) player.getXyLocation().getX();
        int yPos = (int) player.getXyLocation().getY();

        if (input == UP) {
            yPos--;
            message = "You are headed North";
        } else if (input == DOWN) {
            yPos++;
            message = "You are headed South";
        } else if (input == RIGHT) {
            xPos++;
            message = "You are headed East";
        } else if (input == LEFT) {
            xPos--;
            message = "You are headed West";
        }

        /*Player's requested position*/
        Point tempPlayerPos = new Point(xPos, yPos);
        return tempPlayerPos;
    }

    /**
     * Returns the opposite direction of the door the player is at (this can potentially be null if it is not at
     * W or E door).
     * @param tempXPos (int) player's requested position (x-value)
     * @return (String) opposite direction of the door the player is entering into
     */
    private String oppositeDirWEDoorSet(int tempXPos) {
        if (tempXPos == 0) {
            door = room.getDoor("W");
            return "E"; //oppositeDir = "E";
        } else if (tempXPos == room.getWidth() - 1) {
            door = room.getDoor("E");
            return "W"; //oppositeDir = "W";
        }
        return null;
    }

    /**
     * Returns the opposite direction of the door the player is at (this can potentially be null if it is not at
     * N or S door).
     * @param tempYPos (int) player's requested position (y-value)
     * @return (String) opposite direction of the door the player is entering into
     */
    private String oppositeDirNSDoorSet(int tempYPos) {
        if (tempYPos == 0) {
            door = room.getDoor("N");
            return "S"; //oppositeDir = "S";
        } else if (tempYPos == room.getHeight() - 1) {
            door = room.getDoor("S");
            return "N"; //oppositeDir = "N";
        }
        return null;
    }

    /**
     * Sets the player's new point (x, y) location and updates next display string.
     * @param tempPlayerPos (Point) player's requested point (x, y) location
     */
    private void newPlayerPos(Point tempPlayerPos) {
        player.setXyLocation(tempPlayerPos);
        room.setPlayer(player);
        nextDisplay = room.displayRoom();
    }

    /**
     * Removes item player is about to step on and adds the item to the player's inventory.
     * @param tempPlayerPos (Point) player's requested point (x, y) location
     * @param tempXPos (int) player's requested location (x-value)
     * @param tempYPos (int) player's requested location (y-value)
     * @return (String) item's name that was removed from the ArrayList of all items
     */
    private String pickUpItem(Point tempPlayerPos, int tempXPos, int tempYPos) {
        /*Loop through item positions in room, remove from roomLoot and add to player's inventory*/
        ArrayList<Item> roomItems = room.getRoomItems();
        String itemName = "";
        int i = 0;
        Iterator<Item> roomItem = roomItems.iterator();
        while (roomItem.hasNext()) {
            Item currItem = roomItem.next();
            if (currItem.getXyLocation().equals(tempPlayerPos)) {
                player.addToInventory(currItem);
                itemName = currItem.getName();
                roomItem.remove();
            }
        }
        tempPlayerPos.setLocation(tempXPos, tempYPos);
        newPlayerPos(tempPlayerPos);
        return itemName;
    }

    /**
     * Returns the player's point (x, y) location once the player steps through the door.
     * @param playerPos (Point) player's requested point (x, y) location
     * @param oppositeDir (int) the opposing direction (pair direction) to the door being entered
     * @param xPos (int) player's requested location (x-value)
     * @param yPos (int) player's requested location (y-value)
     * @return (Point) player's point (x, y) location in the connected room
     * @throws InvalidMoveException if there is no door in the connecting room to allow this move
     */
    private Point posNextRoom(Point playerPos, String oppositeDir, int xPos, int yPos) throws InvalidMoveException {
        if (oppositeDir.equals("E") || oppositeDir.equals("W")) { /*Player's x position changes to opposing side (y
            position is determined by the door's wallPosition)*/
            if (oppositeDir.equals("E")) { /*Player exits a room with west door and enters the next room through an
                east door*/
                /*From x = (initialRoom.getWidth())-1 to x = 0 (nextRoom)*/
                xPos = 1;
            } else if (oppositeDir.equals("W")) { /*Player exits a room with an east door and enters the next room
                through a west door*/
                /*From x = 0 (initialRoom) to x = (nextRoom.getWidth())-1*/
                xPos = ((room.getWidth()) - 1) - 1;
            }
            if (room.getDoor(oppositeDir) != null) {
                yPos = room.getDoor(oppositeDir).getWallPosition();
            } else {
                throw new InvalidMoveException();
            }
        } else if (oppositeDir.equals("N") || oppositeDir.equals("S")) { /*Player's y position changes to opposing
            side (x position is determined by the door's wallPosition)*/
            if (oppositeDir.equals("N")) { /*Player exits a room with south door and enters the next room through a
                north door*/
                /*From y = (initialRoom.getHeight())-1 to y = 0 (nextRoom)*/
                yPos = 1;
            } else if (oppositeDir.equals("S")) { /*Player exits a room with north door and enters the next room
                through a south door*/
                /*From y = 0 (initialRoom) to y = (nextRoom.getHeight())-1*/
                yPos = ((room.getHeight()) - 1) - 1;
            }
            if (room.getDoor(oppositeDir) != null) {
                xPos = room.getDoor(oppositeDir).getWallPosition();
            } else {
                throw new InvalidMoveException();
            }
        }
        /*Update player's point position (tempPlayerPos)*/
        playerPos.setLocation(xPos, yPos);
        return playerPos;
    }

    /**
     * Checks if the player's requested position is valid (not walking into walls).
     * @param tempXPos (int) player's requested location (x-value)
     * @param tempYPos (int) player's requested location (y-value)
     * @return (boolean) true if the requested position is valid, false otherwise
     */
    private boolean isMoveValid(int tempXPos, int tempYPos) {
        /*Door check*/
        if (room.doorCheck(tempYPos, tempXPos)) {
            /*If that wall contains a door at the position the player wants to be at*/
            return true;
        }

        /*Wall check (if the position request is not a door, the only other invalid tile is a wall)*/
        if (tempXPos == 0 || tempXPos == (room.getWidth() - 1)) {
            /*Player is trying to move into a wall (east or west wall)*/
            return false;
        }
        if (tempYPos == 0 || tempYPos == (room.getHeight() - 1)) {
            /*Player is trying to move into a wall (north or south wall)*/
            return false;
        }
        return true; /*If the tile is not a door or a wall, it's an item or the floor (both valid)*/
    }

    /**
     * Accessor method to access the string of the next instance of the room.
     * @return (String) updated room instance
     */
    public String getNextDisplay() {
        return nextDisplay;
    }

    /**
     * Accessor method to access the outro message for the game.
     * @return (String) outro message
     */
    public String gameOutro() {
        return "Thank you for playing Rogue!";
    }

    /**
     * Mutator method to set the symbols for the game.
     * @param newSymbols (HashMap<String, Character>) symbols
     */
    public void setSymbols(HashMap<String, Character> newSymbols) {
        symbols = newSymbols;
    }

    /**
     * Mutator method to set up the player.
     * @param thePlayer (Player) current player
     */
    public void setPlayer(Player thePlayer) {
        player = thePlayer;
    }

    /**
     * Accessor method to access the player.
     * @return (Player) player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Accessor method to access the ArrayList of (all) rooms.
     * @return (ArrayList<Room>) all rooms
     */
    public ArrayList<Room> getRooms() {
        return allRooms;
    }

    /**
     * Accessor method to access the ArrayList of (all) items.
     * @return (ArrayList<Item>) all items
     */
    public ArrayList<Item> getItems() {
        Room currRoom = new Room();
        return currRoom.getRoomItems();
    }

    /**
     * Returns a string to display all rooms.
     * @return (String) string to display all rooms
     */
    public String displayAll() {
        for (int i = 0; i < allRooms.size(); i++) {
            allRooms.get(i).setSymbols(symbols);
            displayAllRooms += allRooms.get(i).displayRoom();
        }
        return displayAllRooms;
    }
}
