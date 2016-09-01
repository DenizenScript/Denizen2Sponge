package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OfflinePlayerTag extends AbstractTagObject {

    // <--[object]
    // @Type OfflinePlayerTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an offline player that has previously played on the server. Identified by UUID.
    // -->

    private GameProfile internal;

    public OfflinePlayerTag(GameProfile player) {
        internal = player;
    }

    public GameProfile getInternal() {
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
            Optional<String> nameOpt = ((OfflinePlayerTag) obj).internal.getName();
            if (nameOpt.isPresent()) {
                return new TextTag(nameOpt.get());
            }
            else {
                dat.error.run("Specified game profile lacks a name - file data error?");
                return new NullTag();
            }
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
                CompletableFuture<GameProfile> oplayer = Sponge.getServer().getGameProfileManager().get(UUID.fromString(text));
                GameProfile gp = oplayer.get();
                if (gp == null) {
                    error.run("Invalid PlayerTag UUID input!");
                    return null;
                }
                return new OfflinePlayerTag(gp);
            }
            catch (IllegalArgumentException e) {
                CompletableFuture<GameProfile> oplayer = Sponge.getServer().getGameProfileManager().get(UUID.fromString(text));
                GameProfile gp = oplayer.get();
                if (gp == null) {
                    error.run("Invalid PlayerTag named input!");
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
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }
}
