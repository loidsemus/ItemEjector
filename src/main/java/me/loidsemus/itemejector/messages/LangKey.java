package me.loidsemus.itemejector.messages;

public enum LangKey {
    PREFIX("&6[ItemEjector]&r"),

    ADDED_ITEM("Added &e{item}&r (max &c{maxAmount}&r) to blacklist", "item", "maxAmount"),
    REMOVED_ITEM("Removed &e{item}&r from blacklist", "item"),
    ITEM_NOT_BLACKLISTED("&e{item} &cis not blacklisted", "item"),

    INVALID_ITEM("&e{arg} &cis not a valid item name", "arg"),
    INSUFFICIENT_PERMISSION("&cInsufficient permission"),
    CORRECT_USAGE("&cCorrect usage: &e/ie {usage}", "usage"),
    NOT_A_NUMBER("&e{arg} &cis not a number!", "arg"),

    LIST_HEADER("Blacklisted items: (&eitem &7/ &cmax&r)"),
    LIST_ITEM("&e{item} &7/ &c{maxAmount}&r", "item", "maxAmount");

    private final String defaultValue;
    private final String[] args;

    LangKey(String defaultValue, String... args) {
        this.defaultValue = defaultValue;
        this.args = args;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String[] getArgs() {
        return args;
    }

    public String getKey() {
        return this.toString().toLowerCase();
    }
}
