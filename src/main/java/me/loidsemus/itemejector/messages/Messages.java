package me.loidsemus.itemejector.messages;

import me.loidsemus.lingo.MessageKeyProvider;

public enum Messages implements MessageKeyProvider {
    PREFIX("prefix", "<gradient:yellow:gold>[ItemEjector]</gradient> "),

    ADDED_ITEM("added-item", "Added <gradient:yellow:gold><item></gradient> (max <red><maxAmount></red>) to blacklist"),
    REMOVED_ITEM("removed-item", "Removed <gradient:yellow:gold><item></gradient> from blacklist"),
    ITEM_NOT_BLACKLISTED("item-not-blacklisted", "<gradient:yellow:gold><item></gradient> <red>is not blacklisted"),

    INVALID_ITEM("invalid-item", "<gradient:yellow:gold><arg></gradient> <red>is not a valid item name"),
    INSUFFICIENT_PERMISSION("insufficient-permission", "<red>Insufficient permission"),
    CORRECT_USAGE("correct-usage", "<red>Correct usage: <gradient:yellow:gold>/ie <usage></gradient>"),
    NOT_A_NUMBER("not-a-number", "<gradient:yellow:gold><arg></gradient> <red>is not a number");

    private final String key;
    private final String def;

    Messages(String key, String def) {
        this.key = key;
        this.def = def;
    }


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDefault() {
        return def;
    }


}
