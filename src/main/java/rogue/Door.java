package rogue;

import java.util.ArrayList;

public class Door {

    private Room connectedRoom;
    private Room initialRoom;
    private int wallPosition;
    private int connectedId;

    /**
     * Default constructor.
     */
    public Door() {
        wallPosition = 1;
        connectedId = 2;
    }

    /**
     * Constructor that takes initial room and sets initial room.
     * @param initRoom (Room) initial room
     */
    public Door(Room initRoom) {
        setInitialRoom(initRoom);
    }

    /**
     * Constructor that takes initial room, wall position of the door in initial room, and the roomId of the connected
     * room and sets up door.
     * @param initRoom (Room) initial room
     * @param wallPos (int) wall position of door in initial room
     * @param connectedID (int) roomId of the connected room
     */
    public Door(Room initRoom, int wallPos, int connectedID) {
        setInitialRoom(initRoom);
        setWallPosition(wallPos);
        setConnectedId(connectedID);
    }

    /**
     * Constructor that takes initial room, wall position of the door in initial room, and the connected room to set up
     * door.
     * @param initRoom (Room) initial room
     * @param wallPos (int) wall position of door in initial room
     * @param nextRoom (Room) roomId of the connected room
     */
    public Door(Room initRoom, int wallPos, Room nextRoom) {
        setInitialRoom(initialRoom);
        setWallPosition(wallPosition);
        connectRoom(nextRoom);
    }

    /**
     * Mutator method to set the door's wall position.
     * @param position (int) wall position
     */
    private void setWallPosition(int position) {
        wallPosition = position;
    }

    /**
     * Accessor method to access the door's wall position.
     * @return (int) door's wall position
     */
    public int getWallPosition() {
        return wallPosition;
    }

    /**
     * Mutator method to set the initial room of the door.
     * @param initial (Room) initial room
     */
    private void setInitialRoom(Room initial) {
        initialRoom = initial;
    }

    /**
     * Mutator method to set the connected room id.
     * @param id (int) connected room id
     */
    private void setConnectedId(int id) {
        connectedId = id;
    }

    /**
     * Accessor method to access the door's connected room id.
     * @return (int) connected room id of door
     */
    public int getConnectedId() {
        return connectedId;
    }

    /**
     * Sets the connected room to the room passed to it.
     * @param r (Room) connected room
     */
    public void connectRoom(Room r) {
        connectedRoom = r;
    }

    /**
     * Creates Room ArrayList and is an accessor method to access the door's initial and connected room.
     * @return (ArrayList<Room>) ArrayList (of a potential size of 2) containing initial and connected room for a door
     */
    public ArrayList<Room> getConnectedRooms() {
        ArrayList<Room> connectedRooms = new ArrayList<Room>(); /*ArrayLists that contain up to 2 rooms depending on
        if there is a connected room to the initial room (this idea is used to check connected rooms)*/
        connectedRooms.add(connectedRoom);
        connectedRooms.add(initialRoom);
        return connectedRooms;
    }

    /**
     * Accessor method to access the room the door connects to.
     * @param currentRoom (Room) the room the player is currently in
     * @return (Room) the room the door connects to
     */
    public Room getOtherRoom(Room currentRoom) {
        if (currentRoom == connectedRoom) {
            return initialRoom;
        } else if (currentRoom == initialRoom) {
            return connectedRoom;
        } else {
            return null; /*No other room*/
        }
    }
}
