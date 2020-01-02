package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.CrazyAuctions;
import me.badbones69.crazyauctions.api.Messages;
import me.badbones69.crazyauctions.api.ShopType;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class TypePrompt extends StringPrompt {

    public static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();
    private PlayerAuctionPrepare prep;

    public TypePrompt(PlayerAuctionPrepare prep) {
        this.prep = prep;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Auktion (bid) oder Sofort-Kaufen (sell)?";  //TODO: move to messages
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String message) {
        if (conversationContext.getForWhom() instanceof Player) {
            Player player = (Player) conversationContext.getForWhom();

            if (!player.hasPermission("crazyauctions.bypass")) {
                int SellLimit = 0;
                int BidLimit = 0;
                for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
                    String perm = permission.getPermission();
                    if (perm.startsWith("crazyauctions.sell.")) {
                        perm = perm.replace("crazyauctions.sell.", "");
                        if (Methods.isInt(perm)) {
                            if (Integer.parseInt(perm) > SellLimit) {
                                SellLimit = Integer.parseInt(perm);
                            }
                        }
                    }
                    if (perm.startsWith("crazyauctions.bid.")) {
                        perm = perm.replace("crazyauctions.bid.", "");
                        if (Methods.isInt(perm)) {
                            if (Integer.parseInt(perm) > BidLimit) {
                                BidLimit = Integer.parseInt(perm);
                            }
                        }
                    }
                }
                for (int i = 1; i < 100; i++) {
                    if (SellLimit < i) {
                        if (player.hasPermission("crazyauctions.sell." + i)) {
                            SellLimit = i;
                        }
                    }
                    if (BidLimit < i) {
                        if (player.hasPermission("crazyauctions.bid." + i)) {
                            BidLimit = i;
                        }
                    }
                }
                if (message.equals("sell")) {
                    if (crazyAuctions.getItems(player, ShopType.SELL).size() >= SellLimit) {
                        player.sendRawMessage(Messages.MAX_ITEMS.getMessage());
                        return null;
                    }
                }
                if (message.equals("bid")) {
                    if (crazyAuctions.getItems(player, ShopType.BID).size() >= BidLimit) {
                        player.sendRawMessage(Messages.MAX_ITEMS.getMessage());
                        return null;
                    }
                }
            }


            if (message.equals("bid")) {
                prep.Type = "Bid";
            } else if (message.equals("sell")) {
                prep.Type = "Sell";
            } else if (message.equals("cancel")) {
                player.sendRawMessage("§eAuction cancled!"); //TODO: move to messages
                return null;
            } else {
                return new TypePrompt(prep);
            }
            return new PricePrompt(prep);
        }
        return null;
    }
}
