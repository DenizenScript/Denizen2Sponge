package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;

public class PlayerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base player[<PlayerTag>]
    // @Group Sponge Base Types
    // @Modifier optional
    // @ReturnType PlayerTag
    // @Returns the input as a PlayerTag.
    // @Note If no input is given, and the value of definition [player],
    // or context definition context.[player] is a PlayerTag, will return that instead!
    // This is for backwards compatibility.
    // -->

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier() && data.currentQueue != null) {
            if (data.currentQueue.commandStack.peek().hasDefinition("player")) {
                AbstractTagObject ato = data.currentQueue.commandStack.peek().getDefinition("player");
                if (ato instanceof PlayerTag) {
                    return ato.handle(data.shrink());
                }
                data.error.run("Tried to read connected player, but failed (improperly typed player).");
                return NullTag.NULL;
            }
            else if (data.currentQueue.commandStack.peek().hasDefinition("context")) {
                AbstractTagObject ato = data.currentQueue.commandStack.peek().getDefinition("context");
                if (ato instanceof MapTag) {
                    if (((MapTag) ato).getInternal().containsKey("player")) {
                        AbstractTagObject plt = ((MapTag) ato).getInternal().get("player");
                        if (plt instanceof PlayerTag) {
                            return plt.handle(data.shrink());
                        }
                        data.error.run("Tried to read connected player, but failed (improperly typed context -> player).");
                        return NullTag.NULL;
                    }
                }
                data.error.run("Tried to read connected player, but failed (context isn't a map?!).");
                return NullTag.NULL;
            }
            data.error.run("Tried to read connected player, but failed (no connected player).");
            return NullTag.NULL;
        }
        if (!data.hasNextModifier()) {
            data.error.run("Invalid player tag-base: expected a modifier! See documentation for this tag! (No local player ref found)");
            return null;
        }
        return PlayerTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
