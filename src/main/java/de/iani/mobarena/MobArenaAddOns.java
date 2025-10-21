package de.iani.mobarena;

import de.iani.cubesidestats.api.CubesideStatisticsAPI;
import de.iani.cubesidestats.api.GlobalStatisticKey;
import de.iani.cubesidestats.api.GlobalStatistics;
import de.iani.cubesidestats.api.PlayerStatistics;
import de.iani.cubesidestats.api.StatisticKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.logging.Level;

public class MobArenaAddOns extends JavaPlugin {

    private @Nullable CubesideStatisticsAPI cubesideStatistics;
    private long eventStartMillis = 0L;
    private long eventEndMillis = 0L;
    private StatisticKey eventPlayerWonRoundsStatsKey;
    private GlobalStatisticKey eventCommunityWonRoundsStatsKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        cubesideStatistics = getServer().getServicesManager().load(CubesideStatisticsAPI.class);
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);

        if (cubesideStatistics == null) return;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConfigurationSection eventSection = getConfig().getConfigurationSection("event");
        if (eventSection != null) {
            try {
                c.setTime(format.parse(eventSection.getString("start")));
                eventStartMillis = c.getTimeInMillis();
                c.setTime(format.parse(eventSection.getString("end")));
                eventEndMillis = c.getTimeInMillis();
            } catch (ParseException e) {
                getLogger().log(Level.SEVERE, "Date and Time could not be parsed from config.");
                eventStartMillis = 0L;
                eventEndMillis = 0L;
            }

            try {
                eventPlayerWonRoundsStatsKey = cubesideStatistics.getStatisticKey(eventSection.getString("statsKey"));
                eventCommunityWonRoundsStatsKey = cubesideStatistics.getGlobalStatisticKey(eventSection.getString("statsKey"));
            } catch (NullPointerException e) {
                getLogger().log(Level.SEVERE, "(Global-)StatisticKeys could not be created from config.");
                eventPlayerWonRoundsStatsKey = cubesideStatistics.getStatisticKey("mobarena.wonRounds");
                eventCommunityWonRoundsStatsKey = cubesideStatistics.getGlobalStatisticKey("mobarena.community.WonRounds");
            } finally {
                eventPlayerWonRoundsStatsKey.setDisplayName("Mobarena-Runden gewonnen");
                eventCommunityWonRoundsStatsKey.setDisplayName("Mobarena-Runden gemeinsam gewonnen");
            }
        }
    }

    public boolean isEventTime() {
        long now = System.currentTimeMillis();
        return now >= eventStartMillis && now < eventEndMillis;
    }

    public void addRoundWonScore(Set<Player> playerList, int points) {

        if (cubesideStatistics == null) return;

        playerList.forEach(p -> {
            PlayerStatistics playerStats = cubesideStatistics.getStatistics(p.getUniqueId());
            playerStats.increaseScore(eventPlayerWonRoundsStatsKey, points);
        });

        GlobalStatistics globalStatistic = cubesideStatistics.getGlobalStatistics();
        globalStatistic.increaseValue(eventCommunityWonRoundsStatsKey, points);
    }
}
