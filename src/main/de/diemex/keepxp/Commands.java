package de.diemex.keepxp;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Diemex
 */
public class Commands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        //TODO command with player as arg
        if (sender.hasPermission("keepxp.admin") && sender instanceof Player && args.length >= 1)
        {
            Player player = (Player) sender;
            PlayerInventory inv = player.getInventory();
            if (args[0].equalsIgnoreCase("us") && args.length >= 3)
            {
                if (args.length >= 4)
                    player = Bukkit.getPlayer(args[3]);
                if (player == null)
                    sender.sendMessage("Couldn't find player with name " + args[3]);
                else
                {
                    inv.addItem(ScrollOfKeeping.makeScroll(Regex.parseNumber(args[1]), Regex.parseNumber(args[2])));
                    sender.sendMessage("Added Used Scroll with " + Regex.parseNumber(args[1]) + " to " + player.getName());
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("eb"))
            {
                if (args.length >= 2)
                    player = Bukkit.getPlayer(args[1]);
                if (player == null)
                    sender.sendMessage("Couldn't find player with name " + args[1]);
                else
                {
                    inv.addItem(ExpBottle.makeEmptyBottle());
                    sender.sendMessage("Added Empty KeepXp Bottle to " + player.getName());
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("fb") && args.length >= 2)
            {
                if (args.length >= 3)
                    player = Bukkit.getPlayer(args[2]);
                if (player == null)
                    sender.sendMessage("Couldn't find player with name " + args[2]);
                else
                {
                    inv.addItem(ExpBottle.makeBottle(ExpBottle.lvlToExp(Regex.parseNumber(args[1])), Regex.parseNumber(args[1]), player.getName()));
                    sender.sendMessage("Added Filled KeepXp Bottle with " + Regex.parseNumber(args[1]) + " lvls to " + player.getName());
                    return true;
                }
            }

        }
        return false;
    }
}
