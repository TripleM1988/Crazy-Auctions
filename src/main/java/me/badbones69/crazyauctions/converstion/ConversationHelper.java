package me.badbones69.crazyauctions.converstion;

import me.badbones69.crazyauctions.Main;
import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.PlayerAuctionPrepare;
import me.badbones69.crazyauctions.api.CrazyAuctions;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class ConversationHelper {
    public static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();

    public static void startConversation(Player player, Main mainInstance, PlayerAuctionPrepare prep) {

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


        ConversationFactory confFactory = new ConversationFactory(mainInstance);
        Conversation conversation;
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
