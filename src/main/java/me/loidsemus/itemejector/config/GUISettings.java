package me.loidsemus.itemejector.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class GUISettings implements SettingsHolder {

    @Comment("If enabled, /ie will open a management GUI, it is work in progress and is therefore disabled by default.")
    public static final Property<Boolean> ENABLED = newProperty("gui.enabled", false);

    @Comment("Currently doesn't do anything, but will be used to show commonly blacklisted items in the add item menu in the GUI.")
    public static final Property<List<String>> COMMONS = newListProperty("gui.commons", "DIRT", "COBBLESTONE", "NETHERRACK", "END_STONE");

    private GUISettings() {}

}
