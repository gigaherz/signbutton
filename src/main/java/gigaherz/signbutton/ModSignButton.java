package gigaherz.signbutton;

import gigaherz.signbutton.button.SignButtonBlock;
import gigaherz.signbutton.button.SignButtonItem;
import gigaherz.signbutton.button.SignButtonTileEntity;
import gigaherz.signbutton.client.ClientUtils;
import gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModSignButton.MODID)
public class ModSignButton
{
    public static final String MODID = "signbutton";

    public static class Blocks
    {
        @ObjectHolder("signbutton:acacia_sign_button")
        public static Block ACACIA_SIGN_BUTTON;
        @ObjectHolder("signbutton:birch_sign_button")
        public static Block BIRCH_SIGN_BUTTON;
        @ObjectHolder("signbutton:dark_oak_sign_button")
        public static Block DARK_OAK_SIGN_BUTTON;
        @ObjectHolder("signbutton:jungle_sign_button")
        public static Block JUNGLE_SIGN_BUTTON;
        @ObjectHolder("signbutton:oak_sign_button")
        public static Block OAK_SIGN_BUTTON;
        @ObjectHolder("signbutton:spruce_sign_button")
        public static Block SPRUCE_SIGN_BUTTON;
    }

    public static class Items
    {
        @ObjectHolder("signbutton:acacia_sign_button")
        public static Item ACACIA_SIGN_BUTTON;
        @ObjectHolder("signbutton:birch_sign_button")
        public static Item BIRCH_SIGN_BUTTON;
        @ObjectHolder("signbutton:dark_oak_sign_button")
        public static Item DARK_OAK_SIGN_BUTTON;
        @ObjectHolder("signbutton:jungle_sign_button")
        public static Item JUNGLE_SIGN_BUTTON;
        @ObjectHolder("signbutton:oak_sign_button")
        public static Item OAK_SIGN_BUTTON;
        @ObjectHolder("signbutton:spruce_sign_button")
        public static Item SPRUCE_SIGN_BUTTON;
    }

    public static ModSignButton instance;

    public static final Logger logger = LogManager.getLogger(MODID);

    public static final String CHANNEL = MODID;
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, CHANNEL))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public ModSignButton()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTEs);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::lateInitThings);
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("acacia_sign_button"),
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("birch_sign_button"),
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("dark_oak_sign_button"),
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("jungle_sign_button"),
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("oak_sign_button"),
                new SignButtonBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)).setRegistryName("spruce_sign_button")
        );
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new SignButtonItem(Blocks.ACACIA_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("acacia_sign_button"),
                new SignButtonItem(Blocks.BIRCH_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("birch_sign_button"),
                new SignButtonItem(Blocks.DARK_OAK_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("dark_oak_sign_button"),
                new SignButtonItem(Blocks.JUNGLE_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("jungle_sign_button"),
                new SignButtonItem(Blocks.OAK_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("oak_sign_button"),
                new SignButtonItem(Blocks.SPRUCE_SIGN_BUTTON, new Item.Properties().maxStackSize(16).group(ItemGroup.REDSTONE)).setRegistryName("spruce_sign_button")
        );
    }

    public void registerTEs(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TileEntityType.Builder.create(SignButtonTileEntity::new,
                Blocks.ACACIA_SIGN_BUTTON, Blocks.BIRCH_SIGN_BUTTON, Blocks.DARK_OAK_SIGN_BUTTON,
                Blocks.JUNGLE_SIGN_BUTTON, Blocks.OAK_SIGN_BUTTON, Blocks.SPRUCE_SIGN_BUTTON).build(null).setRegistryName("sign_button"));
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        int messageNumber = 0;
        channel.registerMessage(messageNumber++, OpenSignButtonEditor.class, OpenSignButtonEditor::encode, OpenSignButtonEditor::new, OpenSignButtonEditor::handle);
        logger.debug("Final message number: " + messageNumber);
    }

    public void lateInitThings(FMLLoadCompleteEvent event)
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientUtils::registerTESR);
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
