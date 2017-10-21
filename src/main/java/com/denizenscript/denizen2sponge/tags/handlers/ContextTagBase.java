package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.NullTag;

public class ContextTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base context
    // @Group Sponge Base Types
    // @ReturnType MapTag
    // @Returns the content of the definition 'context'.
    // This is for backwards compatibility.
    // -->

    @Override
    public String getName() {
        return "context";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier() && data.currentQueue != null
                && data.currentQueue.commandStack.peek().hasDefinition("context")) {
            AbstractTagObject ato = data.currentQueue.commandStack.peek().getDefinition("context");
            if (ato instanceof MapTag) {
                return ato.handle(data.shrink());
            }
        }
        data.error.run("No context present!");
        return new NullTag();
    }
}
