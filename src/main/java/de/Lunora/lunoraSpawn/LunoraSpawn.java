package de.Lunora.lunoraSpawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class LunoraSpawn extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        if (getCommand("spawn") != null) getCommand("spawn").setExecutor(this);
        if (getCommand("lobby") != null) getCommand("lobby").setExecutor(this);
        if (getCommand("setspawn") != null) getCommand("setspawn").setExecutor(this);
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LunoraSpawn wurde erfolgreich aktiviert!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("lunora.setspawn")) {
                player.sendMessage("§cDu hast keine Berechtigung dazu!");
                return true;
            }
            saveLocation(player.getLocation());
            player.sendMessage("§aSpawn-Punkt wurde erfolgreich gesetzt!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawn") || command.getName().equalsIgnoreCase("lobby")) {
            Location spawnLocation = getSpawnLocation();
            if (spawnLocation == null) {
                player.sendMessage("§cEs wurde noch kein Spawn-Punkt gesetzt! Nutze /setspawn");
                return true;
            }
            player.teleport(spawnLocation);
            player.sendMessage("§aDu wurdest zum Spawn teleportiert!");
            return true;
        }

        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Location spawnLocation = getSpawnLocation();
        if (spawnLocation != null) {
            event.getPlayer().teleport(spawnLocation);
        }
    }

    private void saveLocation(Location loc) {
        getConfig().set("spawn.world", loc.getWorld().getName());
        getConfig().set("spawn.x", loc.getX());
        getConfig().set("spawn.y", loc.getY());
        getConfig().set("spawn.z", loc.getZ());
        getConfig().set("spawn.yaw", (double) loc.getYaw());
        getConfig().set("spawn.pitch", (double) loc.getPitch());
        saveConfig();
    }

    private Location getSpawnLocation() {
        if (getConfig() == null || !getConfig().contains("spawn.world")) return null;
        
        String worldName = getConfig().getString("spawn.world");
        if (worldName == null) return null;
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            getLogger().log(Level.WARNING, "Welt '" + worldName + "' wurde nicht gefunden!");
            return null;
        }

        double x = getConfig().getDouble("spawn.x");
        double y = getConfig().getDouble("spawn.y");
        double z = getConfig().getDouble("spawn.z");
        float yaw = (float) getConfig().getDouble("spawn.yaw");
        float pitch = (float) getConfig().getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}
