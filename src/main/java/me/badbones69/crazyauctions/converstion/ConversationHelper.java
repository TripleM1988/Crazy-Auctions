package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Main;
import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.CrazyAuctions;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.Messages;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConversationHelper {
    public static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();

    public static void startConversation(Player player, Main mainInstance) {
        ItemStack item = Methods.getItemInHand(player);
        int amount = item.getAmount();

        if(Methods.getItemInHand(player).getType() == Material.AIR) {
            player.sendRawMessage(Messages.DOSENT_HAVE_ITEM_IN_HAND.getMessage());
            return;
        }

        boolean sellingAllowed = true;
        boolean biddingAllowed = true;

        if(!crazyAuctions.isSellingEnabled()) {
            sellingAllowed = false;
        }
        if(!Methods.hasPermission(player, "Sell")) {
            sellingAllowed = false;
        }
        if(!crazyAuctions.isBiddingEnabled()) {
            biddingAllowed = false;
        }
        if(!Methods.hasPermission(player, "Bid")) {
            biddingAllowed = false;
        }

        for(String id : FileManager.Files.CONFIG.getFile().getStringList("Settings.BlackList")) {
            if(item.getType() == Methods.makeItem(id, 1).getType()) {
                player.sendRawMessage(Messages.ITEM_BLACKLISTED.getMessage());
                return;
            }
        }
        if(!FileManager.Files.CONFIG.getFile().getBoolean("Settings.Allow-Damaged-Items")) {
            for(Material i : Methods.getDamageableItems()) {
                if(item.getType() == i) {
                    if(item.getDurability() > 0) {
                        player.sendRawMessage(Messages.ITEM_DAMAGED.getMessage());
                        return;
                    }
                }
            }
        }


        ConversationFactory confFactory = new ConversationFactory(mainInstance);
        Conversation conversation;
        PlayerAuctionPrepare prep = new PlayerAuctionPrepare();
        prep.ItemToSell = item;
        if(biddingAllowed && sellingAllowed) {
            conversation = confFactory.withFirstPrompt(new TypePrompt(prep)).withLocalEcho(false).buildConversation(player);
        }
        else {
            if(biddingAllowed) {
                prep.Type = "bid";
            }else if(sellingAllowed){
                prep.Type = "sell";
            }
            else {
                player.sendRawMessage("§4Plugin disabled!");
                return;
            }
            conversation = confFactory.withFirstPrompt(new PricePrompt(prep)).withLocalEcho(false).buildConversation(player);
        }
        conversation.begin();
    }

}
