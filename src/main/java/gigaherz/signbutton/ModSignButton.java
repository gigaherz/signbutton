package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid = ModSignButton.MODID,
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = ModSignButton.VERSION)
public class ModSignButton
{
    public static final String MODID = "signbutton";
    public static final String VERSION = "@VERSION@";

    public static Block signButton;
    public static Item itemSignButton;

    @Mod.Instance(value = ModSignButton.MODID)
    public static ModSignButton instance;

    @SidedProxy(clientSide = "gigaherz.signbutton.client.ClientProxy", serverSide = "gigaherz.signbutton.CommonProxy")
    public static CommonProxy proxy;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                signButton = new BlockSignButton()
        );

        GameRegistry.registerTileEntity(TileSignButton.class, signButton.getRegistryName().toString());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                itemSignButton = new ItemSignButton()
        );
    }
}
