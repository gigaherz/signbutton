package dev.gigaherz.signbutton;

import com.mojang.logging.LogUtils;
import dev.gigaherz.signbutton.button.SignButtonBlock;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonItem;
import dev.gigaherz.signbutton.button.SignButtonWoodTypes;
import dev.gigaherz.signbutton.client.ClientUtils;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(ModSignButton.MODID)
public class ModSignButton
{
    private static final Logger logger = LogUtils.getLogger();

    public static final String MODID = "signbutton";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static RegistryObject<Block> ACACIA_SIGN_BUTTON = BLOCKS.register("acacia_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.ACACIA)));
    public static RegistryObject<Block> BIRCH_SIGN_BUTTON = BLOCKS.register("birch_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.BIRCH_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.BIRCH)));
    public static RegistryObject<Block> DARK_OAK_SIGN_BUTTON = BLOCKS.register("dark_oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.DARK_OAK)));
    public static RegistryObject<Block> JUNGLE_SIGN_BUTTON = BLOCKS.register("jungle_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.JUNGLE)));
    public static RegistryObject<Block> OAK_SIGN_BUTTON = BLOCKS.register("oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.OAK)));
    public static RegistryObject<Block> SPRUCE_SIGN_BUTTON = BLOCKS.register("spruce_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.SPRUCE)));
    public static RegistryObject<Block> MANGROVE_SIGN_BUTTON = BLOCKS.register("mangrove_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.MANGROVE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.MANGROVE)));
    public static RegistryObject<Block> BAMBOO_SIGN_BUTTON = BLOCKS.register("bamboo_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava(), SignButtonWoodTypes.setSupported(WoodType.BAMBOO)));
    public static RegistryObject<Block> CHERRY_SIGN_BUTTON = BLOCKS.register("cherry_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CHERRY)));
    public static RegistryObject<Block> CRIMSON_SIGN_BUTTON = BLOCKS.register("crimson_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CRIMSON)));
    public static RegistryObject<Block> WARPED_SIGN_BUTTON = BLOCKS.register("warped_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.WARPED)));

    public static RegistryObject<Item> ACACIA_SIGN_BUTTON_ITEM = ITEMS.register("acacia_sign_button", () -> new SignButtonItem(ACACIA_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> BIRCH_SIGN_BUTTON_ITEM = ITEMS.register("birch_sign_button", () -> new SignButtonItem(BIRCH_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> DARK_OAK_SIGN_BUTTON_ITEM = ITEMS.register("dark_oak_sign_button", () -> new SignButtonItem(DARK_OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> JUNGLE_SIGN_BUTTON_ITEM = ITEMS.register("jungle_sign_button", () -> new SignButtonItem(JUNGLE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> OAK_SIGN_BUTTON_ITEM = ITEMS.register("oak_sign_button", () -> new SignButtonItem(OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> SPRUCE_SIGN_BUTTON_ITEM = ITEMS.register("spruce_sign_button", () -> new SignButtonItem(SPRUCE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> MANGROVE_SIGN_BUTTON_ITEM = ITEMS.register("mangrove_sign_button", () -> new SignButtonItem(MANGROVE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> BAMBOO_SIGN_BUTTON_ITEM = ITEMS.register("bamboo_sign_button", () -> new SignButtonItem(BAMBOO_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> CHERRY_SIGN_BUTTON_ITEM = ITEMS.register("cherry_sign_button", () -> new SignButtonItem(CHERRY_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> CRIMSON_SIGN_BUTTON_ITEM = ITEMS.register("crimson_sign_button", () -> new SignButtonItem(CRIMSON_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> WARPED_SIGN_BUTTON_ITEM = ITEMS.register("warped_sign_button", () -> new SignButtonItem(WARPED_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));

    public static RegistryObject<BlockEntityType<SignButtonBlockEntity>> SIGN_BUTTON_BLOCK_ENTITY = BLOCK_ENTITIES.register("sign_button", () ->
            BlockEntityType.Builder.of(SignButtonBlockEntity::new,
                    ACACIA_SIGN_BUTTON.get(), BIRCH_SIGN_BUTTON.get(), DARK_OAK_SIGN_BUTTON.get(),
                    JUNGLE_SIGN_BUTTON.get(), OAK_SIGN_BUTTON.get(), SPRUCE_SIGN_BUTTON.get(),
                    MANGROVE_SIGN_BUTTON.get(), BAMBOO_SIGN_BUTTON.get(), CHERRY_SIGN_BUTTON.get(),
                    CRIMSON_SIGN_BUTTON.get(), WARPED_SIGN_BUTTON.get()).build(null));

    public static final String CHANNEL = "main";
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, CHANNEL))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public ModSignButton()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addItemsToTabs);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientUtils::initClient);
    }

    private void addItemsToTabs(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS)
        {
            event.accept(OAK_SIGN_BUTTON_ITEM);
        }
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(OAK_SIGN_BUTTON_ITEM);
            event.accept(SPRUCE_SIGN_BUTTON_ITEM);
            event.accept(BIRCH_SIGN_BUTTON_ITEM);
            event.accept(JUNGLE_SIGN_BUTTON_ITEM);
            event.accept(ACACIA_SIGN_BUTTON_ITEM);
            event.accept(DARK_OAK_SIGN_BUTTON_ITEM);
            event.accept(MANGROVE_SIGN_BUTTON_ITEM);
            event.accept(BAMBOO_SIGN_BUTTON_ITEM);
            event.accept(CHERRY_SIGN_BUTTON_ITEM);
            event.accept(CRIMSON_SIGN_BUTTON_ITEM);
            event.accept(WARPED_SIGN_BUTTON_ITEM);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        int messageNumber = 0;
        channel.messageBuilder (OpenSignButtonEditor.class, messageNumber++, NetworkDirection.PLAY_TO_CLIENT).encoder(OpenSignButtonEditor::encode).decoder(OpenSignButtonEditor::new).consumerNetworkThread(OpenSignButtonEditor::handle).add();
        logger.trace("Final message number: " + messageNumber);
    }
}