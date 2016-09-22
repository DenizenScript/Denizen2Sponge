package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;

public class PlayerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base player[<PlayerTag>]
    // @Group Sponge Base Types
    // @ReturnType PlayerTag
    // @Returns the input as a PlayerTag.
    // @Other If no input is given, and the value of definition [player] is a PlayerTag, will return that instead!
    // This is for backwards compatibility.
    // -->

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier() && data.currentQueue != null
                && data.currentQueue.commandStack.peek().hasDefinition("player")) {
            AbstractTagObject ato = data.currentQueue.commandStack.peek().getDefinition("player");
            if (ato instanceof PlayerTag) {
                return ato.handle(data.shrink());
            }
        }
        return PlayerTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
