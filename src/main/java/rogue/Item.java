package rogue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

/*A basic Item class; basic functionality for both consumables and equipment*/
public class Item {

    private String itemName;
    private String itemType;
    private String description;
    private int itemId;
    private Point itemXyLocation;
    private ArrayList<Map<String, String>> items = new ArrayList<>();

    private Room room;
    private Character displayCharacter;

    /**
     * Default constructor.
     */
    public Item() {
        itemName = "";
        itemType = "";
        description = "";
        itemId = 0;
    }

    /**
     * Accessor method to access the item's id.
     * @return (int) item's id
     */
    public int getId() {
        return itemId;
    }

    /**
     * Mutator method to set the item's id.
     * @param id (int) item's id
     */
    public void setId(int id) {
        itemId = id;
    }

    /**
     * Accessor method to access the item's name.
     * @return (String) item's name
     */
    public String getName() {
        return itemName;
    }

    /**
     * Mutator method to set the item's name.
     * @param name (String) item's name
     */
    public void setName(String name) {
        itemName = name;
    }

    /**
     * Accessor method to access the item's type.
     * @return (String) item's type
     */
    public String getType() {
        return itemType;
    }

    /**
     * Mutator method to set the item's type.
     * @param type (String) item's type
     */
    public void setType(String type) {
        itemType = type;
    }

    /**
     * Accessor method to access the item's display character.
     * @return (Character) item's display character
     */
    public Character getDisplayCharacter() {
        return displayCharacter;
    }

    /**
     * Mutator method to set the item's display character.
     * @param newDisplayCharacter (Character) item's display character
     */
    public void setDisplayCharacter(Character newDisplayCharacter) {
        displayCharacter = newDisplayCharacter;
    }

    /**
     * Accessor method to access the item's description.
     * @return (String) item's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Mutator method to set the item's description.
     * @param newDescription (String) item's description
     */
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /**
     * Accessor method to access the item's point (x, y) location.
     * @return (Point) item's point (x, y) location
     */
    public Point getXyLocation() {
        return itemXyLocation;
    }

    /**
     * Mutator method to set the item's point (x, y) location.
     * @param newXyLocation (Point) item's point (x, y) location
     */
    public void setXyLocation(Point newXyLocation) {
        itemXyLocation = newXyLocation;
    }

    /**
     * Accessor method to access the item's current room.
     * @return (Room) item's current room
     */
    public Room getCurrentRoom() {
        return room;
    }

    /**
     * Mutator method to set the item's current room.
     * @param newCurrentRoom (Room) item's current room
     */
    public void setCurrentRoom(Room newCurrentRoom) {
        room = newCurrentRoom;
    }
}
