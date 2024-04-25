package dev.gigaherz.signbutton;

import com.mojang.logging.LogUtils;
import dev.gigaherz.signbutton.button.SignButtonBlock;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonItem;
import dev.gigaherz.signbutton.button.SignButtonWoodTypes;
import dev.gigaherz.signbutton.client.ClientUtils;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public ModSignButton(IEventBus modEventBus)
    {

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addItemsToTabs);
        modEventBus.addListener(this::gatherData);

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

    private void commonSetup(RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0");
        registrar.playToClient(OpenSignButtonEditor.TYPE, OpenSignButtonEditor.CODEC, OpenSignButtonEditor::handle);
    }

    private void gatherData(GatherDataEvent event)
    {
        Datagen.gatherData(event);
    }

    private static class Datagen
    {
        public static void gatherData(GatherDataEvent event)
        {
            DataGenerator gen = event.getGenerator();

            gen.addProvider(event.includeServer(), new Recipes(gen.getPackOutput(), event.getLookupProvider()));
            gen.addProvider(event.includeServer(), Loot.create(gen.getPackOutput(), event.getLookupProvider()));
            gen.addProvider(event.includeServer(), new BlockTags(gen.getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));
            gen.addProvider(event.includeServer(), new ItemTags(gen.getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));
        }

        private static class Recipes extends RecipeProvider
        {
            public Recipes(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookup)
            {
                super(gen, lookup);
            }

            @Override
            protected void buildRecipes(RecipeOutput consumer)
            {
                signRecipe(consumer, OAK_SIGN_BUTTON_ITEM, Items.OAK_SIGN);
                signRecipe(consumer, SPRUCE_SIGN_BUTTON_ITEM, Items.SPRUCE_SIGN);
                signRecipe(consumer, BIRCH_SIGN_BUTTON_ITEM, Items.BIRCH_SIGN);
                signRecipe(consumer, JUNGLE_SIGN_BUTTON_ITEM, Items.JUNGLE_SIGN);
                signRecipe(consumer, ACACIA_SIGN_BUTTON_ITEM, Items.ACACIA_SIGN);
                signRecipe(consumer, DARK_OAK_SIGN_BUTTON_ITEM, Items.DARK_OAK_SIGN);
                signRecipe(consumer, MANGROVE_SIGN_BUTTON_ITEM, Items.MANGROVE_SIGN);
                signRecipe(consumer, BAMBOO_SIGN_BUTTON_ITEM, Items.BAMBOO_SIGN);
                signRecipe(consumer, CHERRY_SIGN_BUTTON_ITEM, Items.CHERRY_SIGN);
                signRecipe(consumer, CRIMSON_SIGN_BUTTON_ITEM, Items.CRIMSON_SIGN);
                signRecipe(consumer, WARPED_SIGN_BUTTON_ITEM, Items.WARPED_SIGN);
            }

            private static void signRecipe(RecipeOutput consumer, Supplier<SignButtonItem> signButton, Item originalSign)
            {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, signButton.get())
                        .requires(originalSign)
                        .requires(Items.REDSTONE_TORCH)
                        .unlockedBy("has_torch", has(Items.REDSTONE_TORCH))
                        .unlockedBy("has_sign", has(originalSign))
                        .save(consumer);
            }
        }

        private static class Loot
        {
            public static LootTableProvider create(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookup)
            {
                return new LootTableProvider(gen, Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(Loot.BlockTables::new, LootContextParamSets.BLOCK)
                ), lookup);
            }

            public static class BlockTables extends BlockLootSubProvider
            {
                protected BlockTables()
                {
                    super(Set.of(), FeatureFlags.REGISTRY.allFlags());
                }

                @Override
                protected void generate()
                {
                    dropSelf(OAK_SIGN_BUTTON.get());
                    dropSelf(SPRUCE_SIGN_BUTTON.get());
                    dropSelf(BIRCH_SIGN_BUTTON.get());
                    dropSelf(JUNGLE_SIGN_BUTTON.get());
                    dropSelf(ACACIA_SIGN_BUTTON.get());
                    dropSelf(DARK_OAK_SIGN_BUTTON.get());
                    dropSelf(MANGROVE_SIGN_BUTTON.get());
                    dropSelf(BAMBOO_SIGN_BUTTON.get());
                    dropSelf(CHERRY_SIGN_BUTTON.get());
                    dropSelf(CRIMSON_SIGN_BUTTON.get());
                    dropSelf(WARPED_SIGN_BUTTON.get());
                }

                @Override
                protected Iterable<Block> getKnownBlocks()
                {
                    return BuiltInRegistries.BLOCK.entrySet().stream()
                            .filter(e -> e.getKey().location().getNamespace().equals(MODID))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                }
            }
        }


        private static class BlockTags extends IntrinsicHolderTagsProvider<Block>
        {
            public BlockTags(PackOutput packOutput, ExistingFileHelper existingFileHelper, CompletableFuture<HolderLookup.Provider> lookup)
            {
                super(packOutput, Registries.BLOCK, lookup,
                        (block) -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow(), MODID, existingFileHelper);
            }

            @Override
            protected void addTags(HolderLookup.Provider lookup)
            {
                tag(net.minecraft.tags.BlockTags.BUTTONS)
                        .add(OAK_SIGN_BUTTON.get())
                        .add(SPRUCE_SIGN_BUTTON.get())
                        .add(BIRCH_SIGN_BUTTON.get())
                        .add(JUNGLE_SIGN_BUTTON.get())
                        .add(ACACIA_SIGN_BUTTON.get())
                        .add(DARK_OAK_SIGN_BUTTON.get())
                        .add(MANGROVE_SIGN_BUTTON.get())
                        .add(BAMBOO_SIGN_BUTTON.get())
                        .add(CHERRY_SIGN_BUTTON.get())
                        .add(CRIMSON_SIGN_BUTTON.get())
                        .add(WARPED_SIGN_BUTTON.get());
            }
        }


        private static class ItemTags extends IntrinsicHolderTagsProvider<Item>
        {
            public ItemTags(PackOutput packOutput, ExistingFileHelper existingFileHelper, CompletableFuture<HolderLookup.Provider> lookup)
            {
                super(packOutput, Registries.ITEM, lookup,
                        (item) -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow(), MODID, existingFileHelper);
            }

            @Override
            protected void addTags(HolderLookup.Provider lookup)
            {
                tag(net.minecraft.tags.ItemTags.BUTTONS)
                        .add(OAK_SIGN_BUTTON_ITEM.get())
                        .add(SPRUCE_SIGN_BUTTON_ITEM.get())
                        .add(BIRCH_SIGN_BUTTON_ITEM.get())
                        .add(JUNGLE_SIGN_BUTTON_ITEM.get())
                        .add(ACACIA_SIGN_BUTTON_ITEM.get())
                        .add(DARK_OAK_SIGN_BUTTON_ITEM.get())
                        .add(MANGROVE_SIGN_BUTTON_ITEM.get())
                        .add(BAMBOO_SIGN_BUTTON_ITEM.get())
                        .add(CHERRY_SIGN_BUTTON_ITEM.get())
                        .add(CRIMSON_SIGN_BUTTON_ITEM.get())
                        .add(WARPED_SIGN_BUTTON_ITEM.get());
            }
        }
    }
}