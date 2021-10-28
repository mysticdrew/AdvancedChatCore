/*
 * Mozilla Public License v2.0
 *
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.config.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class GuiConfigHandler {

    private static final GuiConfigHandler INSTANCE = new GuiConfigHandler();

    public String activeTab = "";

    @Getter private final List<Tab> tabs = new ArrayList<>();

    private GuiConfigHandler() {}

    public boolean isTabActive(GuiConfigHandler.Tab button) {
        return button.getName().equals(activeTab);
    }

    public static GuiConfigHandler getInstance() {
        return INSTANCE;
    }

    public void addGuiSection(Tab section) {
        tabs.add(section);
    }

    public List<TabButton> getButtons() {
        int x = 10;
        int y = 26;
        int rows = 1;
        ArrayList<TabButton> buttons = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        int windowWidth = client.getWindow().getScaledWidth();
        for (Tab tab : tabs) {
            int width = client.textRenderer.getWidth(tab.getName()) + 10;

            if (x >= windowWidth - width - 10) {
                x = 10;
                y += 22;
                rows++;
            }

            ButtonGeneric button = this.createButton(x, y, width, tab);
            x += button.getWidth() + 2;
            buttons.add(new TabButton(tab, button));
        }
        return buttons;
    }

    private ButtonGeneric createButton(int x, int y, int width, Tab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getName());
        button.setEnabled(!isTabActive(tab));
        return button;
    }

    public Tab getTab(String name) {
        for (Tab b : tabs) {
            if (b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

    public Screen getDefaultScreen() {
        if (activeTab.isEmpty()) {
            activeTab = tabs.get(0).getName();
        }
        for (Tab tab : tabs) {
            if (tab.getName().equalsIgnoreCase(activeTab)) {
                return tab.getScreen(getButtons());
            }
        }
        return tabs.get(0).getScreen(getButtons());
    }

    public interface Tab {
        String getName();

        Screen getScreen(List<TabButton> buttons);
    }

    public static GuiConfigSection createGuiConfigSection(
            String name, List<ConfigStorage.SaveableConfig<? extends IConfigBase>> configs) {
        List<IConfigBase> configBases = new ArrayList<>();
        for (ConfigStorage.SaveableConfig<? extends IConfigBase> saveable : configs) {
            configBases.add(saveable.config);
        }
        return new GuiConfigSection() {
            @Override
            public List<IConfigBase> getOptions() {
                return configBases;
            }

            @Override
            public String getName() {
                return StringUtils.translate(name);
            }
        };
    }

    public interface GuiConfigSection extends Tab {
        List<IConfigBase> getOptions();

        String getName();

        @Override
        default Screen getScreen(List<TabButton> buttons) {
            GuiConfigHandler.getInstance().activeTab = this.getName();
            return new GuiConfig(buttons, getOptions());
        }
    }

    @AllArgsConstructor
    @Value
    public static class TabButton {

        Tab tab;
        ButtonGeneric button;
    }
}
