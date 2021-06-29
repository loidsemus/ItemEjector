package me.loidsemus.itemejector.messages;

import me.loidsemus.lingo.MessageKeyProvider;

public enum Messages implements MessageKeyProvider {
    PREFIX("prefix", "&6[ItemEjector]&r "),

    ADDED_ITEM("added-item", "Added &6<item>&r (max &c<maxAmount>&r) to blacklist"),
    REMOVED_ITEM("removed-item", "Removed &6<item>&r from blacklist"),
    ITEM_NOT_BLACKLISTED("item-not-blacklisted", "&6<item> &cis not blacklisted"),

    INVALID_ITEM("invalid-item", "&6<arg> &cis not a valid item name"),
    INSUFFICIENT_PERMISSION("insufficient-permission", "&cInsufficient permission"),
    CORRECT_USAGE("correct-usage", "&cCorrect usage: &6/ie <usage>"),
    NOT_A_NUMBER("not-a-number", "&6<arg> &cis not a number");

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
