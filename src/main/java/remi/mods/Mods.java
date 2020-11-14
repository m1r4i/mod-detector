package remi.mods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Mods extends JavaPlugin implements Listener
{
    public static PluginMessageListener pcl;
    public static HashMap<String,List> modsA = new HashMap();
    public static HashMap<String,String> modsB = new HashMap();

    public void onEnable() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)this, (Plugin)this);
        Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)this, "FML|HS", Mods.pcl = (PluginMessageListener)new PluginMessageListener() {
            public synchronized void onPluginMessageReceived(String channel, Player player, byte[] data) {
                if (data[0] == 2) {
                    String mods = new String(data);
                    List<String> mods2 = Arrays.asList(mods.split("\u0003"));
                    modsA.put(player.getName(),mods2);
                    for(int i = 0; i < mods2.size(); ++i){
                        String s = mods2.get(i).replace("\u0005","\n").replace("\u0002","").replace("\u0003","");
                        System.out.println(s);
                    }
                }
            }
        });
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FML|HS");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FML");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FML|MP");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FORGE");
    }
    public void onDisable() {
    }
    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (event.getChannel().equals("FORGE")) {
            event.getPlayer().sendPluginMessage((Plugin)this, "FML|HS", new byte[] { -2, 0 });
            event.getPlayer().sendPluginMessage((Plugin)this, "FML|HS", new byte[] { 0, 2, 0, 0, 0, 0 });
            event.getPlayer().sendPluginMessage((Plugin)this, "FML|HS", new byte[] { 2, 0, 0, 0, 0 });
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("modlist")) {
            if(args.length < 1) return false;
            if(modsA.containsKey(args[0])) {
                List<String> mod = modsA.get(args[0]);
                sender.sendMessage("§a"+args[0]+"'s Mods:");
                if (!(mod.isEmpty())) {
                    for (int i = 0; i < mod.size(); ++i) {
                        String s = mod.get(i).replace("\u0005", "\n").replace("\u0002", "").replace("", "").replace("\u0006", "").replace("\u0004", "-");
                        sender.sendMessage("- "+s);
                    }
                }
            }else{
                sender.sendMessage("§cそのプレイヤーはForgeを使用していません");
                return true;
            }
        }
        return true;
    }
}