package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2core.utilities.AbstractSender;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

public class PlayerSender extends AbstractSender {

    public PlayerSender(Player pl) {
        player = pl;
    }

    public Player player;

    @Override
    public void sendColoredMessage(String message) {
        player.sendMessage(TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(message));
    }
}
