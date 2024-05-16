package kr.kro.izen.MiniGameSurvival.Game1;

import kr.kro.izen.MiniGameSurvival.MiniGameSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static kr.kro.izen.MiniGameSurvival.MiniGameSurvival.*;

public class AnvilDrop extends BukkitCommand {

    private BukkitTask anvilRun;
    private BossBar gameStartTimer;
    private BossBar gameRunTimer;
    public AnvilDrop(String name) {
        super(name);
        getAliases().add("anvil");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length == 0) return true;
        if (args.length == 1) {
            World world = Bukkit.getWorld("mini");
            Location location = new Location(world, 0, -60, 0);
            switch (args[0].toString()) {
                case "시작" -> {
                        createArea(location, 50,4, Material.WHITE_WOOL, Material.BARRIER);
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                            onlinePlayer.teleport(location);
                            onlinePlayer.sendMessage("모루피하기 미니게임 시작됩니다! 모두 준비해주세요!");
                        });
                        anvilDropStart(location, 1000, 49);
                        startTimer(5, 50);
                }
                case "제거" -> {
                    createArea(location, 50, 4, Material.AIR, Material.AIR);
                }
                case "종료" -> {
                    endAnvilDrop();
                }
            }
        }
        return false;
    }

    private void createArea(Location loc, int size, int height, Material material, Material material2) {
        World world = loc.getWorld();
        // 바닥
        for (int x = -size / 2; x <= size / 2; x++) {
            for (int z = -size / 2; z <= size / 2; z++) {
                Location location = new Location(world, loc.getX() + x, loc.getY(), loc.getZ() + z);
                world.getBlockAt(location).setType(material);
            }
        }
        // 벽
        int startX = (int) (loc.getX() - size / 2);
        int startY = (int) loc.getY();
        int startZ = (int) (loc.getZ() - size / 2);
        int endX = startX + size;
        int endZ = startZ + size;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                if (x == startX || x == endX || z == startZ || z == endZ) {
                    for (int y = startY; y <= startY + height; y++) {
                        Location location1 = new Location(world, x + 0.5, y, z + 0.5);
                        world.getBlockAt(location1).setType(material2);
                    }
                }
            }
        }
    }

    private void anvilDropStart(Location location, int tick, int size) {
        Random random = new Random();
        anvilRun = Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (++i >= tick || location.getWorld() == null) {
                    System.out.println("stop");
                    endAnvilDrop();
                }
                int x = (int) (location.getX() + random.nextInt(size) - size / 2);
                int z = (int) (location.getZ() + random.nextInt(size) - size / 2);
                Location dropZone = new Location(location.getWorld(), x, location.getY() + 40, z);
                location.getWorld().getBlockAt(dropZone).setType(Material.ANVIL);
            }
        }, 100L, 1L);
    }

    private void endAnvilDrop() {
        if (anvilRun != null) {
            anvilRun.cancel();
            anvilRun = null;
        }
    }

    private boolean start;
    private void startTimer(int ready, int time) {
        this.gameStartTimer = Bukkit.createBossBar("[ 시작 준비중 : " + ready + " ]", BarColor.RED, BarStyle.SOLID);
        this.gameRunTimer = Bukkit.createBossBar("[ 남은 시간 : " + time + " ]", BarColor.RED, BarStyle.SOLID);
        if (start) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                gameStartTimer.addPlayer(onlinePlayer);
            }
            for (int i = 0; i >= ready * 1000; i++) {
                if (i >= ready * 1000) {
                    start = false;
                    gameStartTimer.removeAll();
                }
                gameStartTimer.setTitle("[ 시작 준비중 : " + ready + " ]");
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                gameRunTimer.addPlayer(player);
            }
            for (int i = 0; i >= time * 1000; i++) {
                if (i >= time * 1000) {
                    start = true;
                    gameRunTimer.removeAll();
                }
                gameRunTimer.setTitle("[ 남은 시간 : " + time + " ]");
            }
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("시작", "종료", "제거");
        return null;
    }
}
