package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.*;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base server
    // @Group Sponge Helper Types
    // @ReturnType ServerBaseTag
    // @Returns a generic handler for server data.
    // -->

    @Override
    public String getName() {
        return "server";
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name ServerBaseTag.worlds
        // @Updated 2017/01/19
        // @Group Server Lists
        // @ReturnType ListTag<WorldTag>
        // @Returns a list of all loaded worlds on the server.
        // -->
        handlers.put("worlds", (dat, obj) -> {
            ListTag list = new ListTag();
            for (World world : Sponge.getServer().getWorlds()) {
                list.getInternal().add(new WorldTag(world));
            }
            return list;
        });
        // <--[tag]
        // @Name ServerBaseTag.online_players
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType ListTag<PlayerTag>
        // @Returns a list of all the current online players on the server.
        // -->
        handlers.put("online_players", (dat, obj) -> {
            ListTag list = new ListTag();
            for (Player player : (Sponge.getServer().getOnlinePlayers())) {
                list.getInternal().add(new PlayerTag(player));
            }
            return list;
        });
        // <--[tag]
        // @Name ServerBaseTag.tps
        // @Updated 2017/04/04
        // @Group Server Information
        // @ReturnType NumberTag
        // @Returns the current ticks per second on the server.
        // -->
        handlers.put("tps", (dat, obj) -> new NumberTag(Sponge.getServer().getTicksPerSecond()));
        // <--[tag]
        // @Name ServerBaseTag.max_players
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType IntegerTag
        // @Returns the current maximum players setting on the server.
        // -->
        handlers.put("max_players", (dat, obj) -> new IntegerTag(Sponge.getServer().getMaxPlayers()));
        // <--[tag]
        // @Name ServerBaseTag.player_idle_timeout
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType IntegerTag
        // @Returns the current player idle timeout setting on the server.
        // -->
        handlers.put("player_idle_timeout", (dat, obj) -> new IntegerTag(Sponge.getServer().getPlayerIdleTimeout()));
        // <--[tag]
        // @Name ServerBaseTag.has_whitelist
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType BooleanTag
        // @Returns whether there is currently an enabled whitelist on the server.
        // -->
        handlers.put("has_whitelist", (dat, obj) -> new BooleanTag(Sponge.getServer().hasWhitelist()));
        // <--[tag]
        // @Name ServerBaseTag.online_mode
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType BooleanTag
        // @Returns whether there the server is currently on online mode or not.
        // -->
        handlers.put("online_mode", (dat, obj) -> new BooleanTag(Sponge.getServer().getOnlineMode()));
        // <--[tag]
        // @Name ServerBaseTag.motd
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType FormattedTextTag
        // @Returns the current motd (message of the day) on the server.
        // -->
        handlers.put("motd", (dat, obj) -> new FormattedTextTag(Sponge.getServer().getMotd()));
        // <--[tag]
        // @Name ServerBaseTag.default_world_name
        // @Updated 2017/08/31
        // @Group Server Information
        // @ReturnType TextTag
        // @Returns the default world name that this server creates and loads.
        // -->
        handlers.put("default_world_name", (dat, obj) -> new TextTag(Sponge.getServer().getDefaultWorldName()));
        // <--[tag]
        // @Name ServerBaseTag.block_type_is_valid[<ListTag>]
        // @Updated 2016//11/24
        // @Group Data Safety
        // @ReturnType BooleanTag
        // @Returns whether the specified text is a valid block type, and can be read as a BlockTypeTag.
        // -->
        handlers.put("block_type_is_valid", (dat, obj) -> new BooleanTag(Sponge.getRegistry().getType(
                BlockType.class, dat.getNextModifier().toString()).isPresent()));
        // <--[tag]
        // @Name ServerBaseTag.cuboid_wrapping[<ListTag>]
        // @Updated 2016//11/24
        // @Group Mathematics
        // @ReturnType CuboidTag
        // @Returns a CuboidTag that contains and minimally wraps all locations in the list of locations.
        // -->
        handlers.put("cuboid_wrapping", (dat, obj) -> {
            ListTag lt = ListTag.getFor(dat.error, dat.getNextModifier());
            if (lt.getInternal().size() == 0) {
                if (!dat.hasFallback()) {
                    dat.error.run("Empty list tag, cannot wrap a cuboid around nothing!");
                }
                return new NullTag();
            }
            LocationTag one = LocationTag.getFor(dat.error, lt.getInternal().get(0));
            CuboidTag ct = new CuboidTag(one.getInternal(), one.getInternal());
            for (int i = 1; i < lt.getInternal().size(); i++) {
                LocationTag c = LocationTag.getFor(dat.error, lt.getInternal().get(i));
                ct.getInternal().min.x = Math.min(ct.getInternal().min.x, c.getInternal().x);
                ct.getInternal().min.y = Math.min(ct.getInternal().min.y, c.getInternal().y);
                ct.getInternal().min.z = Math.min(ct.getInternal().min.z, c.getInternal().z);
                ct.getInternal().max.x = Math.max(ct.getInternal().max.x, c.getInternal().x);
                ct.getInternal().max.y = Math.max(ct.getInternal().max.y, c.getInternal().y);
                ct.getInternal().max.z = Math.max(ct.getInternal().max.z, c.getInternal().z);
            }
            return ct;
        });
        // <--[tag]
        // @Name ServerBaseTag.has_flag[<TextTag>]
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType BooleanTag
        // @Returns whether the entity has a flag with the specified key.
        // -->
        handlers.put("has_flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags = Denizen2Sponge.instance.serverFlagMap;
            return new BooleanTag(Utilities.flagIsValidAndNotExpired(dat.error, flags, flagName));
        });
        // <--[tag]
        // @Name ServerBaseTag.dead_flags
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType ListTag
        // @Returns the list of invalid (expired) flags on the server.
        // -->
        handlers.put("dead_flags", (dat, obj) -> {
            MapTag flags = Denizen2Sponge.instance.serverFlagMap;
            ListTag invalid = new ListTag();
            for (Map.Entry<String, AbstractTagObject> flag : flags.getInternal().entrySet()) {
                if (!Utilities.flagIsValidAndNotExpired(dat.error, flags, flag.getKey())) {
                    invalid.getInternal().add(new TextTag(flag.getKey()));
                }
            }
            return invalid;
        });
        // <--[tag]
        // @Name ServerBaseTag.flag[<TextTag>]
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. May become TextTag regardless of input original type.
        // Optionally don't specify anything to get the entire flag map.
        // -->
        handlers.put("flag", (dat, obj) -> {
            MapTag flags = Denizen2Sponge.instance.serverFlagMap;
            if (!dat.hasNextModifier()) {
                MapTag valid = new MapTag();
                for (Map.Entry<String, AbstractTagObject> flag : flags.getInternal().entrySet()) {
                    if (Utilities.flagIsValidAndNotExpired(dat.error, flags, flag.getKey())) {
                        MapTag mt = MapTag.getFor(dat.error, flag.getKey());
                        valid.getInternal().put(flag.getKey(), mt.getInternal().get("value"));
                    }
                }
                return valid;
            }
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            if (!Utilities.flagIsValidAndNotExpired(dat.error, flags, flagName)) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on the server!");
                }
                return new NullTag();
            }
            MapTag smap = MapTag.getFor(dat.error, flags.getInternal().get(flagName));
            if (smap == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on the server!");
                }
                return new NullTag();
            }
            return smap.getInternal().get("value");
        });
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return new ServerTagBase.ServerBaseTag().handle(data.shrink());
    }

    public static class ServerBaseTag extends AbstractTagObject {

        @Override
        public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
            return handlers;
        }


        @Override
        public String getTagTypeName() {
            return "ServerBaseTag";
        }

        @Override
        public AbstractTagObject handleElseCase(TagData data) {
            return new TextTag("server");
        }
    }
}
