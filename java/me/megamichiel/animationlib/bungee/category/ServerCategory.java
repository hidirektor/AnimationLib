package me.megamichiel.animationlib.bungee.category;

import me.megamichiel.animationlib.bungee.AnimLibPlugin;
import me.megamichiel.animationlib.bungee.RegisteredPlaceholder;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCategory extends PlaceholderCategory {

    private final AnimLibPlugin plugin;
    private final Map<String, DateFormat> dateFormats = new ConcurrentHashMap<>();

    public ServerCategory(AnimLibPlugin plugin) {
        super("server");
        this.plugin = plugin;

        put("online",    (n, p) -> Integer.toString(getServer().getOnlineCount()));
        put("uptime",    (n, p) -> plugin.uptime());

        Runtime runtime = Runtime.getRuntime();
        put("ram_used",  (n, p) -> Long.toString((runtime.totalMemory() - runtime.freeMemory()) >>> 20));
        put("ram_free",  (n, p) -> Long.toString(runtime.freeMemory() >>> 20));
        put("ram_total", (n, p) -> Long.toString(runtime.totalMemory() >>> 20));
        put("ram_max",   (n, p) -> Long.toString(runtime.maxMemory() >>> 20));
    }

    @Override
    public RegisteredPlaceholder get(String value) {
        RegisteredPlaceholder result = super.get(value);
        if (result != null) return result;
        if (value.startsWith("online_")) {
            String server = value.substring(7);
            ServerInfo info = BungeeCord.getInstance().getServerInfo(server);
            if (info != null) return (n, p) -> Integer.toString(info.getPlayers().size());
            return (n, p) -> "<invalid_server>";
        }
        if (value.startsWith("date_")) {
            String str = value.substring(5);
            DateFormat df = dateFormats.get(str);
            if (df == null) {
                try {
                    dateFormats.put(str, df = new SimpleDateFormat(str));
                } catch (IllegalArgumentException ex) {
                    plugin.nag("Invalid date format: " + str);
                    return (n, p) -> "<invalid_date_format>";
                }
            }
            DateFormat format = df;
            return (n, p) -> format.format(new Date());
        }
        return null;
    }
}
