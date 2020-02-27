package me.loidsemus.itemejector.listeners;

import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemActionListener implements Listener {

    private ItemEjector plugin;

    public PlayerItemActionListener(ItemEjector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        String uuid = player.getUniqueId().toString();
        Item pickedUpItem = event.getItem();
        Material pickupMaterial = pickedUpItem.getItemStack().getType();

        if(!plugin.getPlayerManager().getPlayer(uuid).getBlacklistedItems().containsKey(pickupMaterial)) {
            return;
        }

        int maxItems = plugin.getPlayerManager().getPlayer(uuid).getBlacklistedItems().get(pickupMaterial);
        // Get amount of the Material that is already in the player's inventory
        int amountInInventory = 0;
        for(ItemStack item : player.getInventory().all(pickupMaterial).values()) {
            amountInInventory += item.getAmount();
        }

        //System.out.println(pickupMaterial.toString() + ": " + pickedUpItem.getItemStack().getAmount() + "/" + maxItems + " INV " + amountInInventory);

        if(maxItems == 0) {
            event.setCancelled(true);
            return;
        } else if(pickedUpItem.getItemStack().getAmount() + amountInInventory <= maxItems) {
            return;
        }

        int itemsToPickUp = maxItems - amountInInventory;
        if(itemsToPickUp <= 0) {
            event.setCancelled(true);
            return;
        }

        //System.out.println("Has to pick up " + itemsToPickUp + "/" + pickedUpItem.getItemStack().getAmount());
        event.setCancelled(true);
        ItemStack modifiedItemStack = pickedUpItem.getItemStack();
        modifiedItemStack.setAmount(modifiedItemStack.getAmount() - itemsToPickUp);
        pickedUpItem.setItemStack(modifiedItemStack);

        ItemStack pickedUpItemStack = pickedUpItem.getItemStack().clone();
        pickedUpItemStack.setAmount(itemsToPickUp);
        player.getInventory().addItem(pickedUpItemStack);
    }

}
