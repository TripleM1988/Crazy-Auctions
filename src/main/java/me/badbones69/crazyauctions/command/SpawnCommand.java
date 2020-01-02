package me.badbones69.crazyauctions.command;

import me.badbones69.crazyauctions.Main;
import me.badbones69.crazyauctions.VillagerAuction;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand implements CommandExecutor {

    private Main mainInstance;

    public SpawnCommand(Main mainInstance) {
        this.mainInstance = mainInstance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Location location = ((Player)commandSender).getLocation();
            VillagerAuction.spawnVillager(location, mainInstance);

            List<Location> locList = new ArrayList<>();
            locList = (List<Location>)mainInstance.getConfig().getList("villagers", new ArrayList<Location>());
            locList.add(location);

            mainInstance.getConfig().set("villagers", locList);
            mainInstance.saveConfig();
        }
        return true;
    }
}
