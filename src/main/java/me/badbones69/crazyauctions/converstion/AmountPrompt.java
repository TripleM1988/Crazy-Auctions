package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.Messages;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Deprecated
public class AmountPrompt extends StringPrompt {

    private PlayerAuctionPrepare prep;

    public AmountPrompt(PlayerAuctionPrepare prep) {
        this.prep = prep;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String message) {
        if(conversationContext.getForWhom() instanceof Player) {
            Player player = (Player) conversationContext.getForWhom();

            Number amount = 0;
            if (message.equalsIgnoreCase("cancel")) {
                player.sendRawMessage("§eAuction cancled!"); //TODO: move to messages
                return null;
            }
            else {
                if(!Methods.isInt(message)) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Arg%", message);
                    placeholders.put("%arg%", message);
                    player.sendRawMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
                    return new AmountPrompt(prep);
                }
                amount = Integer.parseInt(message);
                prep.Amount = amount;
            }
            return new StartAuctionPrompt(prep); //TODO
        }

        return null;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Wie viele Items sollen angeboten werden?"; //TODO: move to messages
    }
}
