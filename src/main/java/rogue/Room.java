package rogue;

import java.util.ArrayList;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;

/*A room within the dungeon - contains monsters, treasure, doors out, etc.*/
public class Room {

   private int width;
   private int height;
   private int roomID;
   private String displayRoom = "";

   private HashMap<String, Character> symbols;
   private ArrayList<Item> items = new ArrayList<>();
   private HashMap<String, Door> doors = new HashMap<>();
   private ArrayList<String> directions = new ArrayList<String>(Arrays.asList(new String[] {"N", "S", "E", "W"}));

   private Player player;
   private Point playerXyLocation;
   private boolean playerInRoom = false;

   private RogueParser parser;

   /**
    * Default constructor.
    */
   public Room() {
      /*Arbitrary values (within the range of non-magic numbers)*/
      width = 0;
      height = 0;
      roomID = 1;
   }

   /**
    * Constructor that takes a RogueParser to further distribute information from a given json file.
    * @param theDungeonInfo (RogueParser) parser from RogueParser
    */
   public Room(RogueParser theDungeonInfo) {
      /*Gives room access to parser information*/
      parser = theDungeonInfo;
      setSymbols(parser.getSymbolHashMap());
   }

   /**
    * Finds a wall direction that does not contain a door.
    * @return (String) direction (i.e. "N", "S", "E", or "W")
    */
   public String findFreeDoorDirection() {
      for (String dir : directions) {
         if (doors.get(dir) == null) {
            return dir;
         }
      }
      return null;
   }

   /**
    * Verfies room by checking item locations (to be within the walls), player location (within the walls), and
    * sufficient amount of doors.
    * @return (boolean) true if the room follows guidelines, false otherwise
    * @throws NotEnoughDoorsException if there are no doors in the room
    */
   public boolean verifyRoom() throws NotEnoughDoorsException {
      try {
         for (Item check : items) {
            checkItemLocation(check);
         }
      } catch (Exception e) {
         return false;
      }
      if (playerInRoom) {
         int xPos = (int) player.getXyLocation().getX();
         int yPos = (int) player.getXyLocation().getY();
         if ((xPos < 0) || (yPos < 0)) {
            return false;
         } else if ((xPos > getWidth() - 1) || (yPos > getHeight() - 1)) {
            return false;
         }
      }
      if (doors.size() == 0) {
         throw new NotEnoughDoorsException();
      }
      return true;
   }

   /**
    * Mutator method to set player's state within the current room.
    * @param state (boolean) true if player is in current room, false otherwise
    */
   public void setIsPlayerInRoom(boolean state) {
      playerInRoom = state;
   }

   /**
    * Accessor method to access the player's state in current room.
    * @return (boolean) true if player is in current room, false otherwise
    */
   public boolean getIsPlayerInRoom() {
      return playerInRoom;
   }

   /**
    * Adds item to the ArrayList of items in room if the item location and item id make sense.
    * @param toAdd (Item) item to add to ArrayList of items
    * @throws ImpossiblePositionException if item is located outside the walls of the room
    * @throws NoSuchItemException if item's id does not match any possible item id listed in json file
    */
   public void addItem(Item toAdd) throws ImpossiblePositionException, NoSuchItemException {
      checkItemLocation(toAdd);
      checkItemId(toAdd);
      items.add(toAdd);
   }

   /**
    * Checks if item's location is valid.
    * @param itemToCheck (Item) current item being inspected
    * @throws ImpossiblePositionException if item is located outside the walls of the room
    */
   private void checkItemLocation(Item itemToCheck) throws ImpossiblePositionException {
      int xItem = (int) itemToCheck.getXyLocation().getX();
      int yItem = (int) itemToCheck.getXyLocation().getY();
      if ((xItem > getHeight() || xItem < 0 || yItem > getWidth() || yItem < 0)) {
         throw new ImpossiblePositionException();
      }
      if (onPlayer(xItem, yItem)) { /*If item to be added is on player*/
         throw new ImpossiblePositionException();
      }
      if (onItem(xItem, yItem)) { /*If item to be added is on another existing item*/
         throw new ImpossiblePositionException();
      }
   }

   /**
    * Checks if the x and y values passed in are occupied by the player's position.
    * @param x (int) x-value of an tile being inspected
    * @param y (int) y-value of an tile being inspected
    * @return (boolean) true if player is occupying the tile, otherwise false
    */
   private boolean onPlayer(int x, int y) {
      if (playerInRoom) {
         if ((x == (int) player.getXyLocation().getX()) && (y == (int) player.getXyLocation().getY())) {
            return true;
         }
      }
      return false;
   }

   /**
    * Checks if the x and y values passed in are occupied by an item's position.
    * @param x (int) x-value of an tile being inspected
    * @param y (int) y-value of an tile being inspected
    * @return (boolean) true if the item is occupying the tile, otherwise false
    */
   private boolean onItem(int x, int y) {
      for (Item checkItem : items) {
         if ((x == (int) checkItem.getXyLocation().getX()) && (y == (int) checkItem.getXyLocation().getY())) {
            return true;
         }
      }
      return false;
   }

   /**
    * Accessor method to access a randomly generated floor (empty) tile.
    * @return (Point) point (x, y) location of the randomly generated empty tile
    */
   public Point getEmptyTile() {
      Random rand = new Random();
      int randomX;
      int randomY;
      do {
         randomX = rand.nextInt(getWidth() - 1);
         randomY = rand.nextInt(getHeight() - 1);
      } while (onItem(randomX, randomY) || onPlayer(randomX, randomY));
      Point emptyTile = new Point(randomX, randomY);
      return emptyTile;
   }

   /**
    * Checks if the item's id matches an existing item's (checking if item exists given the json file).
    * @param itemToCheck (Item) item to check
    * @throws NoSuchItemException if item's id does not match any possible item id listed in json file
    */
   private void checkItemId(Item itemToCheck) throws NoSuchItemException {
      ArrayList<Map<String, String>> itemLocations = parser.getItemLocationsArray();
         for (Map<String, String> itemLocation : itemLocations) {
            if ((Integer.parseInt(itemLocation.get("id"))) == (itemToCheck.getId())) {
               return;
            }
         }
         /*If it goes through for loop without matching id, then throw NoSuchItemException*/
         throw new NoSuchItemException();
   }

   /**
    * Mutator method to set the symbols for the game.
    * @param newSymbols (HashMap) symbols
    */
   public void setSymbols(HashMap newSymbols) {
      symbols = newSymbols;
   }

   /**
    * Accessor method to access the room's width.
    * @return (int) room's width dimension
    */
   public int getWidth() {
      return width;
   }

   /**
    * Mutator method to set room width.
    * @param newWidth (int) room's width dimension
    */
   public void setWidth(int newWidth) {
      width = newWidth;
   }

   /**
    * Accessor method to access the room's height.
    * @return (int) room's height dimension
    */
   public int getHeight() {
      return height;
   }

   /**
    * Mutator method to set room height.
    * @param newHeight (int) room's height dimension
    */
   public void setHeight(int newHeight) {
      height = newHeight;
   }

   /**
    * Accessor method to access the room's id.
    * @return (int) room's id
    */
   public int getId() {
      return roomID;
   }

   /**
    * Mutator method to set room id.
    * @param newId (int) room's id
    */
   public void setId(int newId) {
      roomID = newId;
   }

   /**
    * Accessor method to access the room's items ArrayList.
    * @return (ArrayList<Item>) room's items ArrayList
    */
   public ArrayList<Item> getRoomItems() {
      return items;
   }

   /**
    * Mutator method to set room items ArrayList.
    * @param newRoomItems (ArrayList<Item>) room's items ArrayList
    */
   public void setRoomItems(ArrayList<Item> newRoomItems) {
      this.items = newRoomItems;
   }

   /**
    * Accessor method to access the player.
    * @return (Player) player
    */
   public Player getPlayer() {
      return player;
   }

   /**
    * Mutator method to set player in room.
    * @param newPlayer (Player) player
    */
   public void setPlayer(Player newPlayer) {
      player = newPlayer;
   }

   /**
    * Indirect hashmap method to get the value of door hashmap by passing in the key value, direction.
    * @param direction (String) wall direction of the door
    * @return (Door) corresponding door given the key (direction)
    */
   public Door getDoor(String direction) {
      if (doors.containsKey(direction)) {
         return doors.get(direction);
      } else {
         return null; // throw exception??
      }
   }

   /**
    * Accessor method to access the hashmap of doors in the room.
    * @return (HashMap<String, Door>) doors belonging to the room
    */
   public HashMap<String, Door> getDoors() {
      return doors;
   }

   /**
    * Mutator method to set door in room.
    * @param direction (String) wall direction of door (i.e. "N", "S", "E", or "W")
    * @param door (Door) value of the door hashmap
    */
   public void setDoor(String direction, Door door) {
      doors.put(direction, door);
   }

   /**
    * Returns true if player is in room, false otherwise.
    * @return (boolean) true if player is in room, false otherwise
    */
   public boolean isPlayerInRoom() {
      return playerInRoom;
   }

   /**
    * Checks if door is occupying the (x, y) location passed in (j, i).
    * @param i (int) y-value of the location
    * @param j (int) x-value of the location
    * @return (boolean) true if door exists at (j, i), false otherwise
    */
   public boolean doorCheck(int i, int j) { /*Public as makeMove (and subsequent helper methods) utilize this
      functionality*/
      if (((getDoor("N") != null) && (i == 0 && j == getDoor("N").getWallPosition()))) {
         return true;
      }
      if (((getDoor("S") != null) && (i == (getHeight() - 1) && j == getDoor("S").getWallPosition()))) {
         return true;
      }
      if (((getDoor("W") != null) && (j == 0 && i == getDoor("W").getWallPosition()))) {
         return true;
      }
      if (((getDoor("E") != null) && (j == (getWidth() - 1) && i == getDoor("E").getWallPosition()))) {
         return true;
      }
      return false;
   }

   /**
    * Checks if item is occupying the (x, y) location passed in (j, i).
    * @param i (int) y-value of the location
    * @param j (int) x-value of the location
    * @return (boolean) true if item exists at (j, i), false otherwise
    */
   public boolean itemCheck(int i, int j) { /*Public as makeMove (and subsequent helper methods) utilize this
      functionality*/
      double xItem = 0;
      double yItem = 0;

      for (int k = 0; k < items.size(); k++) {
         xItem = (items.get(k).getXyLocation()).getX();
         yItem = (items.get(k).getXyLocation()).getY();
         /*since i represents height (y-value) and j represents width (x-value), compare i to yItem and j to xItem*/
         if (j == xItem && i == yItem) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the item type of the item at (j, i).
    * @param i (int) y-value of the location
    * @param j (int) x-value of the location
    * @return (String) item type of item located at (j, i)
    */
   private String itemType(int i, int j) {
      double xItem = 0;
      double yItem = 0;
      String type;

      if (itemCheck(i, j)) {
         for (int k = 0; k < items.size(); k++) {
            xItem = (items.get(k).getXyLocation()).getX();
            yItem = (items.get(k).getXyLocation()).getY();
            if (j == xItem && i == yItem) {
               type = items.get(k).getType();
               if (type.equals("potion")) {
                  return "POTION";
               } else if (type.equals("scroll")) {
                  return "SCROLL";
               } else if (type.equals("armour")) {
                  return "ARMOR";
               } else if (type.equals("food")) {
                  return "FOOD";
               } else if (type.equals("gold")) {
                  return "GOLD";
               }
            }
         }
      }
      return null;
   }

   /**
    * Checks if player is occupying the (x, y) location passed in (j, i).
    * @param i (int) y-value of the location
    * @param j (int) x-value of the location
    * @return (boolean) true if player exists at (j, i), false otherwise
    */
   private boolean playerCheck(int i, int j) {
      double xPlayer = 0;
      double yPlayer = 0;

      if (isPlayerInRoom()) {
         xPlayer = (getPlayer().getXyLocation()).getX();
         yPlayer = (getPlayer().getXyLocation()).getY();
      }
      if (j == xPlayer && i == yPlayer) {
         return true;
      }
      return false;
   }

   /**
    * Builds a string to display the room with walls, items, doors, floor, and player.
    * @return (String) room
    */
   public String displayRoom() {
      displayRoom = "";

      for (int i = 0; i < getHeight(); i++) {
         for (int j = 0; j < getWidth(); j++) {
            if ((i == 0 || i == (getHeight() - 1)) || (j == 0 || j == (getWidth() - 1))) {
               if (doorCheck(i, j)) {
                  displayRoom += symbols.get("DOOR");
               } else {
                  if (i == 0 || i == (getHeight() - 1)) {
                     displayRoom += symbols.get("NS_WALL");
                  } else if (j == 0 || j == (getWidth() - 1)) {
                     displayRoom += symbols.get("EW_WALL");
                  }
               }
            } else {
               if (itemCheck(i, j)) {
                  displayRoom += symbols.get(itemType(i, j));
               } else if (playerCheck(i, j)) {
                  displayRoom += symbols.get("PLAYER");
               } else {
                  displayRoom += symbols.get("FLOOR");
               }
            }
         }
         displayRoom += "\n";
      }
      return displayRoom;
   }
}
