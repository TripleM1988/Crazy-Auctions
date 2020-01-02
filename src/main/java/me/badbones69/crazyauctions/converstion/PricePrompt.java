package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.Messages;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PricePrompt extends StringPrompt {

    private PlayerAuctionPrepare prep;

    public PricePrompt(PlayerAuctionPrepare prep) {
        this.prep = prep;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String message) {
        if (conversationContext.getForWhom() instanceof Player) {
            Player player = (Player) conversationContext.getForWhom();
            Number price = 0;
            if (message.equalsIgnoreCase("cancel")) {
                player.sendRawMessage(Messages.AUCTION_CREATE_CANCEL.getMessage());
                prep.ItemToSell.setAmount((int)prep.Amount);
                player.getInventory().addItem(prep.ItemToSell);
                return null;
            }
            else {
                if(!Methods.isInt(message)) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Arg%", message);
                    placeholders.put("%arg%", message);
                    player.sendRawMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
                    return new PricePrompt(prep);
                }
                price = Integer.parseInt(message);
            }
            if (prep.Type.equals("Bid")) {
                if (price.longValue() < FileManager.Files.CONFIG.getFile().getLong("Settings.Minimum-Bid-Price")) {
                    ((Player) conversationContext.getForWhom()).sendRawMessage(Messages.BID_PRICE_TO_LOW.getMessage());
                    return new PricePrompt(prep);
                }
                if (price.longValue() > FileManager.Files.CONFIG.getFile().getLong("Settings.Max-Beginning-Bid-Price")) {
                    ((Player) conversationContext.getForWhom()).sendRawMessage(Messages.BID_PRICE_TO_HIGH.getMessage());
                    return new PricePrompt(prep);
                }
            } else {
                if (price.longValue() < FileManager.Files.CONFIG.getFile().getLong("Settings.Minimum-Sell-Price")) {
                    ((Player) conversationContext.getForWhom()).sendRawMessage(Messages.SELL_PRICE_TO_LOW.getMessage());
                    return new PricePrompt(prep);
                }
                if (price.longValue() > FileManager.Files.CONFIG.getFile().getLong("Settings.Max-Beginning-Sell-Price")) {
                    ((Player) conversationContext.getForWhom()).sendRawMessage(Messages.SELL_PRICE_TO_HIGH.getMessage());
                    return new PricePrompt(prep);
                }
            }
            prep.Price = price;
            return new StartAuctionPrompt(prep);
        }
        return null;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Messages.PRICE_PROMPT_MESSAGE.getMessage();
    }
}
