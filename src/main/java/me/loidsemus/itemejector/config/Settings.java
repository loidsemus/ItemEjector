package me.loidsemus.itemejector.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Settings implements SettingsHolder {

    public static final Property<String> LANGUAGE = newProperty("lang", "default");

    private Settings() {}

}
