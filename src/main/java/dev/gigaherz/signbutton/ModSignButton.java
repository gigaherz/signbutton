package dev.gigaherz.signbutton;

import com.mojang.logging.LogUtils;
import dev.gigaherz.signbutton.button.SignButtonBlock;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonItem;
import dev.gigaherz.signbutton.button.SignButtonWoodTypes;
import dev.gigaherz.signbutton.client.ClientUtils;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(ModSignButton.MODID)
public class ModSignButton
{
    private static final Logger logger = LogUtils.getLogger();

    public static final String MODID = "signbutton";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static DeferredBlock<SignButtonBlock> ACACIA_SIGN_BUTTON = BLOCKS.register("acacia_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.ACACIA)));
    public static DeferredBlock<SignButtonBlock> BIRCH_SIGN_BUTTON = BLOCKS.register("birch_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.BIRCH_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.BIRCH)));
    public static DeferredBlock<SignButtonBlock> DARK_OAK_SIGN_BUTTON = BLOCKS.register("dark_oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.DARK_OAK)));
    public static DeferredBlock<SignButtonBlock> JUNGLE_SIGN_BUTTON = BLOCKS.register("jungle_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.JUNGLE)));
    public static DeferredBlock<SignButtonBlock> OAK_SIGN_BUTTON = BLOCKS.register("oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.OAK)));
    public static DeferredBlock<SignButtonBlock> SPRUCE_SIGN_BUTTON = BLOCKS.register("spruce_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.SPRUCE)));
    public static DeferredBlock<SignButtonBlock> MANGROVE_SIGN_BUTTON = BLOCKS.register("mangrove_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.MANGROVE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.MANGROVE)));
    public static DeferredBlock<SignButtonBlock> BAMBOO_SIGN_BUTTON = BLOCKS.register("bamboo_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava(), SignButtonWoodTypes.setSupported(WoodType.BAMBOO)));
    public static DeferredBlock<SignButtonBlock> CHERRY_SIGN_BUTTON = BLOCKS.register("cherry_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CHERRY)));
    public static DeferredBlock<SignButtonBlock> CRIMSON_SIGN_BUTTON = BLOCKS.register("crimson_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CRIMSON)));
    public static DeferredBlock<SignButtonBlock> WARPED_SIGN_BUTTON = BLOCKS.register("warped_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of().mapColor(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.WARPED)));

    public static DeferredItem<SignButtonItem> ACACIA_SIGN_BUTTON_ITEM = ITEMS.register("acacia_sign_button", () -> new SignButtonItem(ACACIA_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> BIRCH_SIGN_BUTTON_ITEM = ITEMS.register("birch_sign_button", () -> new SignButtonItem(BIRCH_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> DARK_OAK_SIGN_BUTTON_ITEM = ITEMS.register("dark_oak_sign_button", () -> new SignButtonItem(DARK_OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> JUNGLE_SIGN_BUTTON_ITEM = ITEMS.register("jungle_sign_button", () -> new SignButtonItem(JUNGLE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> OAK_SIGN_BUTTON_ITEM = ITEMS.register("oak_sign_button", () -> new SignButtonItem(OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> SPRUCE_SIGN_BUTTON_ITEM = ITEMS.register("spruce_sign_button", () -> new SignButtonItem(SPRUCE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> MANGROVE_SIGN_BUTTON_ITEM = ITEMS.register("mangrove_sign_button", () -> new SignButtonItem(MANGROVE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> BAMBOO_SIGN_BUTTON_ITEM = ITEMS.register("bamboo_sign_button", () -> new SignButtonItem(BAMBOO_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> CHERRY_SIGN_BUTTON_ITEM = ITEMS.register("cherry_sign_button", () -> new SignButtonItem(CHERRY_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> CRIMSON_SIGN_BUTTON_ITEM = ITEMS.register("crimson_sign_button", () -> new SignButtonItem(CRIMSON_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static DeferredItem<SignButtonItem> WARPED_SIGN_BUTTON_ITEM = ITEMS.register("warped_sign_button", () -> new SignButtonItem(WARPED_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<SignButtonBlockEntity>> SIGN_BUTTON_BLOCK_ENTITY = BLOCK_ENTITIES.register("sign_button", () ->
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

    public ModSignButton(IEventBus modEventBus)
    {

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addItemsToTabs);

        if (FMLEnvironment.dist == Dist.CLIENT) ClientUtils.initClient(modEventBus);
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
        channel.messageBuilder (OpenSignButtonEditor.class, messageNumber++, PlayNetworkDirection.PLAY_TO_CLIENT).encoder(OpenSignButtonEditor::encode).decoder(OpenSignButtonEditor::new).consumerNetworkThread(OpenSignButtonEditor::handle).add();
        logger.trace("Final message number: " + messageNumber);
    }
}