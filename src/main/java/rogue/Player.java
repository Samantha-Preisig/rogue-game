package rogue;

import java.awt.Point;
import java.util.ArrayList;

public class Player {

    private Point xyLocation;
    private String playerName;
    private ArrayList<Item> inventory = new ArrayList<>();
    private Room room;

    /**
     * Default constructor.
     */
    public Player() {
        xyLocation = new Point(1, 1);
        setXyLocation(xyLocation);
    }

    /**
     * Constructor that takes player's name and sets up player's point (x, y) location.
     * @param name (String) player's name
     */
    public Player(String name) {
        playerName = name;
        xyLocation = new Point(1, 1);
        setXyLocation(xyLocation);
    }

    /**
     * Accessor method to access the player's name.
     * @return (String) player's name
     */
    public String getName() {
        return playerName;
    }

    /**
     * Mutator method to set the player's name.
     * @param newName (String) player's name
     */
    public void setName(String newName) {
        playerName = newName;
    }

    /**
     * Accessor method to access the player's point (x, y) location.
     * @return (Point) player's point (x, y) location
     */
    public Point getXyLocation() {
        return xyLocation;
    }

    /**
     * Mutator method to set the player's point (x, y) location.
     * @param newXyLocation (Point) player's point (x, y) location
     */
    public void setXyLocation(Point newXyLocation) {
        xyLocation = newXyLocation;
    }

    /**
     * Accessor method to access what room the player is currently in.
     * @return (Room) the room the player is currently in
     */
    public Room getCurrentRoom() {
        return room;
    }

    /**
     * Mutator method to set the room the player is in currently as the "current room".
     * @param newRoom (Room) current room the player is in
     */
    public void setCurrentRoom(Room newRoom) {
        room = newRoom;
    }

    /**
     * Accessor method to access the player's inventory.
     * @return (ArrayList<Item>) player's inventory
     */
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    /**
     * Adds an item to the player's inventory.
     * @param toAdd (Item) item the player picked up
     */
    public void addToInventory(Item toAdd) {
        inventory.add(toAdd);
    }
}
