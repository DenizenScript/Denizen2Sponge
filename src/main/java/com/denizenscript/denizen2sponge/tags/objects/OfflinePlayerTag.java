package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class OfflinePlayerTag extends AbstractTagObject {

    // <--[object]
    // @Type OfflinePlayerTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an offline player that has previously played on the server. Identified by UUID.
    // -->

    private User internal;

    public OfflinePlayerTag(User player) {
        internal = player;
    }

    public User getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name OfflinePlayerTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the player.
        // @Example "Bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> {
            return new TextTag(((OfflinePlayerTag) obj).internal.getName());
        });
        // <--[tag]
        // @Name OfflinePlayerTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the player.
        // -->
        handlers.put("uuid", (dat, obj) -> {
            return new TextTag(((OfflinePlayerTag) obj).internal.getUniqueId().toString());
        });
    }

    public static OfflinePlayerTag getFor(Action<String> error, String text) {
        try {
            try {
                Optional<User> oplayer = Sponge.getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(UUID.fromString(text));
                User gp = oplayer.orElse(null);
                if (gp == null) {
                    error.run("Invalid OfflinePlayerTag UUID input!");
                    return null;
                }
                return new OfflinePlayerTag(gp);
            }
            catch (IllegalArgumentException e) {
                Optional<User> oplayer = Sponge.getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(text);
                User gp = oplayer.orElse(null);
                if (gp == null) {
                    error.run("Invalid OfflinePlayerTag named input!");
                    return null;
                }
                return new OfflinePlayerTag(gp);
            }
        }
        catch (Exception e) {
            Debug.exception(e);
            error.run("Game profile read for offline player failed due to an exception, trace precedes this error.");
            return null;
        }
    }

    public static OfflinePlayerTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof OfflinePlayerTag) ? (OfflinePlayerTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }


    @Override
    public String getTagTypeName() {
        return "OfflinePlayerTag";
    }
    @Override
    public String debug() {
        return toString() + "/" + internal.getName();
    }
}
