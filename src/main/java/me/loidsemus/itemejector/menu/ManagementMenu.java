package me.loidsemus.itemejector.menu;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.loidsemus.itemejector.ItemEjector;
import me.loidsemus.itemejector.database.DataPlayer;
import me.loidsemus.itemejector.messages.LangKey;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ManagementMenu extends InventoryGui {

    private final ItemEjector plugin;
    private final DataPlayer dataPlayer;

    private static final String[] layout = {
            "ggggggggg",
            "ggggggggg",
            "ggggggggg",
            "ggggggggg",
            "ggggggggg",
            "    a  pn",
    };

    public ManagementMenu(ItemEjector plugin, DataPlayer dataPlayer) {
        super(plugin, "ItemEjector Management", layout);
        this.plugin = plugin;
        this.dataPlayer = dataPlayer;

        GuiElementGroup group = new GuiElementGroup('g');
        populate(group);
        addElement(group);

        addElement(new StaticGuiElement('a', new ItemStack(Material.EMERALD), click -> {
            close(true);
            click.getWhoClicked().sendMessage(ChatColor.GREEN + "Use " + ChatColor.RESET + "/ie add <item> [max]" + ChatColor.GREEN + " to add an item");
            return true;
        }, "&aAdd"));

        addElement(new GuiPageElement('p',
                new ItemStack(Material.FEATHER), GuiPageElement.PageAction.PREVIOUS, "&7Previous page"
        ));
        addElement(new GuiPageElement('n',
                new ItemStack(Material.FEATHER), GuiPageElement.PageAction.NEXT, "&7Next page"
        ));

    }

    private void populate(GuiElementGroup group) {
        group.clearElements();
        dataPlayer.getBlacklistedItems().forEach((material, max) -> {
            group.addElement(new StaticGuiElement('r', new ItemStack(material, max), click -> {
                if (click.getType() == ClickType.RIGHT) {
                    dataPlayer.removeBlacklistedItem(material);
                    click.getWhoClicked().sendMessage(plugin.getMessages().get(LangKey.REMOVED_ITEM, true, material.toString()));
                    populate(group);
                }
                return true;
            },
                    ChatColor.GOLD + StringUtils.capitalize(material.name().toLowerCase()) + " &r/&c " + max,
                    "&cRight-click &7to remove"));
        });
        draw();
    }
}
