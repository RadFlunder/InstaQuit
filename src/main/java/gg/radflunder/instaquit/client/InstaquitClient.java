package gg.radflunder.instaquit.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class InstaquitClient implements ClientModInitializer {
    private static KeyBinding quitKey;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("instaquit", "keys"));

    @Override
    public void onInitializeClient() {
        quitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.instaquit.quit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> checkKeyPress());
    }

    public static void checkKeyPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        if (quitKey.wasPressed()) {
            boolean bl = client.isInSingleplayer();
            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (client.world != null) {
                client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
            }

            if (bl) {
                client.disconnectWithSavingScreen();
            } else {
                client.disconnectWithProgressScreen();
            }

            TitleScreen titleScreen = new TitleScreen();
            if (bl) {
                client.setScreen(titleScreen);
            } else if (serverInfo != null && serverInfo.isRealm()) {
                client.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                client.setScreen(new MultiplayerScreen(titleScreen));
            }
        }
    }
}