package me.badbones69.crazyauctions.controllers;

import me.badbones69.crazyauctions.Main;
import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.Messages;
import me.badbones69.crazyauctions.converstion.ConversationHelper;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CreateAuctionGUI implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            if (e.getClickedInventory() == null)
                return;
        } catch (Error | Exception e2) {
            if (e.getInventory() == null)
                return;
        }
        try {
            if (!e.getClickedInventory().equals(e.getWhoClicked().getOpenInventory().getTopInventory()))
                return;
        } catch (Error | Exception e2) {
            if (!e.getInventory().equals(e.getWhoClicked().getOpenInventory().getTopInventory()))
                return;
        }
        FileConfiguration config = FileManager.Files.CONFIG.getFile();
        if (e.getWhoClicked().getOpenInventory().getTitle().equalsIgnoreCase((Methods.color(config.getString("Settings.CreateGUIName"))))) {
            if(e.getClickedInventory().equals(e.getWhoClicked().getOpenInventory().getTopInventory())) {
                if (e.getSlot() == 4) { //its the slot where the sellable item lies
                    return;
                }
                if (e.getSlot() < 4) { // all accept slots!
                    e.setCancelled(true);
                    Player player = (Player) e.getWhoClicked();
                    boolean itemnull;
                    ItemStack slotis;
                    try {
                        if (e.getClickedInventory().getSize() <= e.getSlot() || e.getSlot() < 0)
                            return;
                    } catch (Error | Exception er) {
                        if (e.getView().getBottomInventory().getSize() <= e.getSlot() || e.getSlot() < 0)
                            return;
                    }

                    try {
                        itemnull = e.getClickedInventory().getItem(4) != null;
                        slotis = e.getClickedInventory().getItem(4);
                    } catch (Error | Exception er) {
                        itemnull = e.getView().getBottomInventory().getItem(e.getSlot()) != null;
                        slotis = e.getView().getBottomInventory().getItem(e.getSlot());
                    }
                    if (itemnull && slotis != null) {
                        //blacklist check
                        for (String id : FileManager.Files.CONFIG.getFile().getStringList("Settings.BlackList")) {
                            if (slotis.getType() == Methods.makeItem(id, 1).getType()) {
                                player.sendRawMessage(Messages.ITEM_BLACKLISTED.getMessage());
                                return;
                            }
                        }
                        // item damage check
                        if (!FileManager.Files.CONFIG.getFile().getBoolean("Settings.Allow-Damaged-Items")) {
                            for (Material i : Methods.getDamageableItems()) {
                                if (slotis.getType() == i) {
                                    if (slotis.hasItemMeta() && !slotis.getItemMeta().isUnbreakable()) {
                                        if (((Damageable) slotis).hasDamage()) {
                                            player.sendRawMessage(Messages.ITEM_DAMAGED.getMessage());
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        PlayerAuctionPrepare prep = new PlayerAuctionPrepare();
                        prep.ItemToSell = slotis;
                        prep.Amount = slotis.getAmount();

                        e.getWhoClicked().closeInventory();

                        ConversationHelper.startConversation(player, Main.getMainInstance(), prep);
                    }
                }
                if(e.getSlot() > 4) { //all cancel buttons
                    e.setCancelled(true);
                    e.getWhoClicked().closeInventory();
                    if (e.getClickedInventory().getItem(4) != null) {
                        ItemStack returnItem = e.getClickedInventory().getItem(4);
                        returnItem.setAmount(e.getClickedInventory().getItem(4).getAmount());
                        Player player = (Player) e.getWhoClicked();
                        player.getInventory().addItem(returnItem);
                    }
                    e.getWhoClicked().sendMessage(Messages.AUCTION_CREATE_CANCEL.getMessage());
                }
            }
        }
    }
}

