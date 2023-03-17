package dev.gigaherz.signbutton;

import dev.gigaherz.signbutton.button.*;
import dev.gigaherz.signbutton.client.ClientUtils;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;

@Mod(ModSignButton.MODID)
public class ModSignButton
{
    public static final String MODID = "signbutton";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static RegistryObject<Block> ACACIA_SIGN_BUTTON = BLOCKS.register("acacia_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.ACACIA)));
    public static RegistryObject<Block> BIRCH_SIGN_BUTTON = BLOCKS.register("birch_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.BIRCH)));
    public static RegistryObject<Block> DARK_OAK_SIGN_BUTTON = BLOCKS.register("dark_oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.DARK_OAK)));
    public static RegistryObject<Block> JUNGLE_SIGN_BUTTON = BLOCKS.register("jungle_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.JUNGLE)));
    public static RegistryObject<Block> OAK_SIGN_BUTTON = BLOCKS.register("oak_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.OAK)));
    public static RegistryObject<Block> SPRUCE_SIGN_BUTTON = BLOCKS.register("spruce_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.SPRUCE)));
    public static RegistryObject<Block> MANGROVE_SIGN_BUTTON = BLOCKS.register("mangrove_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.MANGROVE)));
    public static RegistryObject<Block> BAMBOO_SIGN_BUTTON = BLOCKS.register("bamboo_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.BAMBOO)));
    public static RegistryObject<Block> CRIMSON_SIGN_BUTTON = BLOCKS.register("crimson_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.CRIMSON)));
    public static RegistryObject<Block> WARPED_SIGN_BUTTON = BLOCKS.register("warped_sign_button", () -> new SignButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.WARPED)));

    public static RegistryObject<Item> ACACIA_SIGN_BUTTON_ITEM = ITEMS.register("acacia_sign_button", () -> new SignButtonItem(ACACIA_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> BIRCH_SIGN_BUTTON_ITEM = ITEMS.register("birch_sign_button", () -> new SignButtonItem(BIRCH_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> DARK_OAK_SIGN_BUTTON_ITEM = ITEMS.register("dark_oak_sign_button", () -> new SignButtonItem(DARK_OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> JUNGLE_SIGN_BUTTON_ITEM = ITEMS.register("jungle_sign_button", () -> new SignButtonItem(JUNGLE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> OAK_SIGN_BUTTON_ITEM = ITEMS.register("oak_sign_button", () -> new SignButtonItem(OAK_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> SPRUCE_SIGN_BUTTON_ITEM = ITEMS.register("spruce_sign_button", () -> new SignButtonItem(SPRUCE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> MANGROVE_SIGN_BUTTON_ITEM = ITEMS.register("mangrove_sign_button", () -> new SignButtonItem(MANGROVE_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> BAMBOO_SIGN_BUTTON_ITEM = ITEMS.register("bamboo_sign_button", () -> new SignButtonItem(BAMBOO_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> CRIMSON_SIGN_BUTTON_ITEM = ITEMS.register("crimson_sign_button", () -> new SignButtonItem(CRIMSON_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));
    public static RegistryObject<Item> WARPED_SIGN_BUTTON_ITEM = ITEMS.register("warped_sign_button", () -> new SignButtonItem(WARPED_SIGN_BUTTON.get(), new Item.Properties().stacksTo(16)));

    public static RegistryObject<BlockEntityType<SignButtonBlockEntity>> SIGN_BUTTON_BLOCK_ENTITY = BLOCK_ENTITIES.register("sign_button", () ->
            BlockEntityType.Builder.of(SignButtonBlockEntity::new,
                    ACACIA_SIGN_BUTTON.get(), BIRCH_SIGN_BUTTON.get(), DARK_OAK_SIGN_BUTTON.get(),
                    JUNGLE_SIGN_BUTTON.get(), OAK_SIGN_BUTTON.get(), SPRUCE_SIGN_BUTTON.get(),
                    MANGROVE_SIGN_BUTTON.get(), BAMBOO_SIGN_BUTTON.get(),
                    CRIMSON_SIGN_BUTTON.get(), WARPED_SIGN_BUTTON.get()).build(null));

    public static ModSignButton instance;

    public static final Logger logger = LogManager.getLogger(MODID);

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
        instance = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addItemsToTabs);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientUtils::initClient);
    }

    private void addItemsToTabs(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS)
        {
            event.accept(OAK_SIGN_BUTTON_ITEM);
        }
        if(event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(OAK_SIGN_BUTTON_ITEM);
            event.accept(SPRUCE_SIGN_BUTTON_ITEM);
            event.accept(BIRCH_SIGN_BUTTON_ITEM);
            event.accept(JUNGLE_SIGN_BUTTON_ITEM);
            event.accept(ACACIA_SIGN_BUTTON_ITEM);
            event.accept(DARK_OAK_SIGN_BUTTON_ITEM);
            event.accept(MANGROVE_SIGN_BUTTON_ITEM);
            if (event.getFlags().contains(FeatureFlags.UPDATE_1_20))
                event.accept(BAMBOO_SIGN_BUTTON_ITEM);
            event.accept(CRIMSON_SIGN_BUTTON_ITEM);
            event.accept(WARPED_SIGN_BUTTON_ITEM);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        int messageNumber = 0;
        channel.registerMessage(messageNumber++, OpenSignButtonEditor.class, OpenSignButtonEditor::encode, OpenSignButtonEditor::new, OpenSignButtonEditor::handle);
        logger.debug("Final message number: " + messageNumber);
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}