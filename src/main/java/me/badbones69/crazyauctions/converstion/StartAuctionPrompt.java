package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.Messages;
import me.badbones69.crazyauctions.api.ShopType;
import me.badbones69.crazyauctions.api.events.AuctionListEvent;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class StartAuctionPrompt extends StringPrompt {

    private PlayerAuctionPrepare prep;

    public StartAuctionPrompt(PlayerAuctionPrepare prep) {
        this.prep = prep;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        String nameOfItem;
        if(prep.ItemToSell.getItemMeta().hasDisplayName()) {
            nameOfItem = prep.ItemToSell.getItemMeta().getDisplayName();
        }
        else if(prep.ItemToSell.getItemMeta().hasLocalizedName()) {
            nameOfItem = prep.ItemToSell.getItemMeta().getLocalizedName();
        }
        else {
            nameOfItem = prep.ItemToSell.getType().name();
        }
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%auctiontype%", prep.Type);
        placeholders.put("%Auctiontype%", prep.Type);
        placeholders.put("%auctionitem%", nameOfItem);
        placeholders.put("%Auctionitem%", nameOfItem);
        placeholders.put("%auctionamount%", prep.Amount.toString());
        placeholders.put("%Auctionamount%", prep.Amount.toString());
        placeholders.put("%auctionprice%", prep.Price.toString());
        placeholders.put("%Auctionprice%", prep.Price.toString());
        return Messages.START_AUCTION_PROMPT_MESSAGE.getMessage(placeholders);
//        return "§2Are you sure you want to create the following: " + prep.Type + " of " + prep.Amount + " " + nameOfItem + " for a price of " + prep.Price + "? Type YES to start or CANCEL to cancel.";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String message) {
        if (conversationContext.getForWhom() instanceof Player) {
            Player player = (Player) conversationContext.getForWhom();
            if (message.equalsIgnoreCase("cancel")) {
                player.sendRawMessage(Messages.AUCTION_CREATE_CANCEL.getMessage());
                prep.ItemToSell.setAmount((int)prep.Amount);
                player.getInventory().addItem(prep.ItemToSell);
                return null;
            } else if (message.equalsIgnoreCase("yes")) {
                startAuction(player, prep);
            } else {
                return new StartAuctionPrompt(prep);
            }
        }
        return null;
    }

    private void startAuction(Player player, PlayerAuctionPrepare prep) {
        String seller = player.getName();
        // For testing as another player
        //String seller = "Test-Account";
        int num = 1;
        Random r = new Random();
        for(; FileManager.Files.DATA.getFile().contains("Items." + num); num++) ;
        FileManager.Files.DATA.getFile().set("Items." + num + ".Price", prep.Price);
        FileManager.Files.DATA.getFile().set("Items." + num + ".Seller", seller);
        if(prep.Type.equalsIgnoreCase("Bid")) {
            FileManager.Files.DATA.getFile().set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(FileManager.Files.CONFIG.getFile().getString("Settings.Bid-Time")));
        }else {
            FileManager.Files.DATA.getFile().set("Items." + num + ".Time-Till-Expire", Methods.convertToMill(FileManager.Files.CONFIG.getFile().getString("Settings.Sell-Time")));
        }
        FileManager.Files.DATA.getFile().set("Items." + num + ".Full-Time", Methods.convertToMill(FileManager.Files.CONFIG.getFile().getString("Settings.Full-Expire-Time")));
        int id = r.nextInt(999999);
        // Runs 3x to check for same ID.
        for(String i : FileManager.Files.DATA.getFile().getConfigurationSection("Items").getKeys(false))
            if(FileManager.Files.DATA.getFile().getInt("Items." + i + ".StoreID") == id) id = r.nextInt(Integer.MAX_VALUE);
        for(String i : FileManager.Files.DATA.getFile().getConfigurationSection("Items").getKeys(false))
            if(FileManager.Files.DATA.getFile().getInt("Items." + i + ".StoreID") == id) id = r.nextInt(Integer.MAX_VALUE);
        for(String i : FileManager.Files.DATA.getFile().getConfigurationSection("Items").getKeys(false))
            if(FileManager.Files.DATA.getFile().getInt("Items." + i + ".StoreID") == id) id = r.nextInt(Integer.MAX_VALUE);
        FileManager.Files.DATA.getFile().set("Items." + num + ".StoreID", id);
        ShopType type = ShopType.SELL;
        if(prep.Type.equalsIgnoreCase("Bid")) {
            FileManager.Files.DATA.getFile().set("Items." + num + ".Biddable", true);
            type = ShopType.BID;
        }else {
            FileManager.Files.DATA.getFile().set("Items." + num + ".Biddable", false);
        }
        FileManager.Files.DATA.getFile().set("Items." + num + ".TopBidder", "None");
        ItemStack I = prep.ItemToSell.clone();
        I.setAmount(prep.Amount.intValue());
        FileManager.Files.DATA.getFile().set("Items." + num + ".Item", I);
        FileManager.Files.DATA.saveFile();
        Bukkit.getPluginManager().callEvent(new AuctionListEvent(player, type, I, prep.Price.intValue()));
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Price%", prep.Price + "");
        placeholders.put("%price%", prep.Price + "");
        player.sendRawMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(placeholders));
    }
}
