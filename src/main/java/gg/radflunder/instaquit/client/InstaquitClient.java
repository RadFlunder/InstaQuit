package gg.radflunder.instaquit.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class InstaquitClient implements ClientModInitializer {
    private static KeyBinding quitKey;
    private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");

    @Override
    public void onInitializeClient() {
        quitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.instaquit.quit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.instaquit.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> checkKeyPress());
    }

    public static void checkKeyPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        if (quitKey.wasPressed()) {
            if (client.world != null) {
                boolean bl = client.isInSingleplayer();
                ServerInfo serverInfo = client.getCurrentServerEntry();
                client.world.disconnect();
                if (bl) {
                    client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
                } else {
                    client.disconnect();
                }

                TitleScreen titleScreen = new TitleScreen();
                if (bl) {
                    client.setScreen(new SelectWorldScreen(titleScreen));
                } else if (serverInfo != null && serverInfo.isRealm()) {
                    client.setScreen(new RealmsMainScreen(titleScreen));
                } else {
                    client.setScreen(new MultiplayerScreen(titleScreen));
                }
            }
        }
    }
}