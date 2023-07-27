package mine.plugins.lunar.expbalance.cmds;

import mine.plugins.lunar.expbalance.config.XpConfig;
import mine.plugins.lunar.plugin_framework.cmds.args.Arg;
import mine.plugins.lunar.plugin_framework.cmds.args.LinkArg;
import mine.plugins.lunar.plugin_framework.config.ConfigArg;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public class BlockInfoLink extends LinkArg {

    public BlockInfoLink(XpConfig xpConfig) {
        super("block_info", List.of(new ConfigArg(xpConfig, "xpb.config")));
    }

    @Override
    public String info() {
        return "Block info config commands";
    }

    @Override
    protected List<Arg> tabComplete(CommandSender sender, Collection<Arg> args) {
        return args.stream().toList();
    }
}
