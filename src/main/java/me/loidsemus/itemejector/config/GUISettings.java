package me.loidsemus.itemejector.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;

public class GUISettings implements SettingsHolder {

    @Comment("Currently doesn't do anything, but will be used to show commonly blacklisted items in the add item menu in the GUI.")
    public static final Property<List<String>> COMMONS = newListProperty("gui.commons", "DIRT", "COBBLESTONE", "NETHERRACK", "END_STONE");

    private GUISettings() {}

}
