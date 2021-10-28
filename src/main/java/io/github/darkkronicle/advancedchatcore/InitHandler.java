/*
 * Mozilla Public License v2.0
 *
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.darkkronicle.advancedchatcore.chat.ChatHistoryProcessor;
import io.github.darkkronicle.advancedchatcore.chat.ChatScreenSectionHolder;
import io.github.darkkronicle.advancedchatcore.chat.DefaultChatSuggestor;
import io.github.darkkronicle.advancedchatcore.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        // Setup modules
        ModuleHandler.getInstance().registerModules();
        ConfigManager.getInstance()
                .registerConfigHandler(AdvancedChatCore.MOD_ID, new ConfigStorage());
        // Setup chat history
        MessageDispatcher.getInstance().register(new ChatHistoryProcessor(), -1);
        List<IConfigBase> configBases = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> saveable :
                ConfigStorage.General.OPTIONS) {
            configBases.add(saveable.config);
        }
        GuiConfigHandler.getInstance()
                .addGuiSection(
                        GuiConfigHandler.createGuiConfigSection(
                                "advancedchat.config.tab.general", ConfigStorage.General.OPTIONS));

        GuiConfigHandler.getInstance()
                .addGuiSection(
                        GuiConfigHandler.createGuiConfigSection(
                                "advancedchat.config.tab.chatscreen",
                                ConfigStorage.ChatScreen.OPTIONS));

        // This constructs the default chat suggestor
        ChatScreenSectionHolder.getInstance()
                .addSectionSupplier(
                        (advancedChatScreen -> {
                            if (AdvancedChatCore.CREATE_SUGGESTOR) {
                                return new DefaultChatSuggestor(advancedChatScreen);
                            }
                            return null;
                        }));
    }
}
