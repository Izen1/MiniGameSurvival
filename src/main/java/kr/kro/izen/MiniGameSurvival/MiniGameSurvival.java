package kr.kro.izen.MiniGameSurvival;

import kr.kro.izen.MiniGameSurvival.Game1.AnvilDrop;
import kr.kro.izen.MiniGameSurvival.MainCommand.MiniGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniGameSurvival extends JavaPlugin {

    public static MiniGameSurvival instance;
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getCommandMap().register("미니게임", new MiniGameWorld("미니게임"));
        Bukkit.getCommandMap().register("모루피하기", new AnvilDrop("모루피하기"));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            onlinePlayer.teleport(MiniGameWorld.playerMap.get(onlinePlayer));
        });
        // Plugin shutdown logic
    }
}
