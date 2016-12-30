package buildcraft.api.permission;

/** Defines an entity or tile that is owned by a specific player. */
public interface IPlayerOwned {
    IOwner getOwner();
}
