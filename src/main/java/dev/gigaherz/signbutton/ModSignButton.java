package dev.gigaherz.signbutton;

import dev.gigaherz.signbutton.button.SignButtonBlock;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonItem;
import dev.gigaherz.signbutton.button.SignButtonWoodTypes;
import dev.gigaherz.signbutton.client.ClientUtils;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(ModSignButton.MODID)
public class ModSignButton
{
    public static final String MODID = "signbutton";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static DeferredBlock<SignButtonBlock> ACACIA_SIGN_BUTTON = BLOCKS.registerBlock("acacia_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.ACACIA)));
    public static DeferredBlock<SignButtonBlock> BIRCH_SIGN_BUTTON = BLOCKS.registerBlock("birch_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.BIRCH_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.BIRCH)));
    public static DeferredBlock<SignButtonBlock> DARK_OAK_SIGN_BUTTON = BLOCKS.registerBlock("dark_oak_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.DARK_OAK)));
    public static DeferredBlock<SignButtonBlock> JUNGLE_SIGN_BUTTON = BLOCKS.registerBlock("jungle_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.JUNGLE)));
    public static DeferredBlock<SignButtonBlock> OAK_SIGN_BUTTON = BLOCKS.registerBlock("oak_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.OAK)));
    public static DeferredBlock<SignButtonBlock> SPRUCE_SIGN_BUTTON = BLOCKS.registerBlock("spruce_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.SPRUCE)));
    public static DeferredBlock<SignButtonBlock> MANGROVE_SIGN_BUTTON = BLOCKS.registerBlock("mangrove_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.MANGROVE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().sound(SoundType.WOOD), SignButtonWoodTypes.setSupported(WoodType.MANGROVE)));
    public static DeferredBlock<SignButtonBlock> BAMBOO_SIGN_BUTTON = BLOCKS.registerBlock("bamboo_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava(), SignButtonWoodTypes.setSupported(WoodType.BAMBOO)));
    public static DeferredBlock<SignButtonBlock> CHERRY_SIGN_BUTTON = BLOCKS.registerBlock("cherry_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CHERRY)));
    public static DeferredBlock<SignButtonBlock> CRIMSON_SIGN_BUTTON = BLOCKS.registerBlock("crimson_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.CRIMSON)));
    public static DeferredBlock<SignButtonBlock> WARPED_SIGN_BUTTON = BLOCKS.registerBlock("warped_sign_button", props -> new SignButtonBlock(props.mapColor(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F), SignButtonWoodTypes.setSupported(WoodType.WARPED)));

    public static DeferredItem<SignButtonItem> ACACIA_SIGN_BUTTON_ITEM = ITEMS.registerItem("acacia_sign_button", props -> new SignButtonItem(ACACIA_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> BIRCH_SIGN_BUTTON_ITEM = ITEMS.registerItem("birch_sign_button", props -> new SignButtonItem(BIRCH_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> DARK_OAK_SIGN_BUTTON_ITEM = ITEMS.registerItem("dark_oak_sign_button", props -> new SignButtonItem(DARK_OAK_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> JUNGLE_SIGN_BUTTON_ITEM = ITEMS.registerItem("jungle_sign_button", props -> new SignButtonItem(JUNGLE_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> OAK_SIGN_BUTTON_ITEM = ITEMS.registerItem("oak_sign_button", props -> new SignButtonItem(OAK_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> SPRUCE_SIGN_BUTTON_ITEM = ITEMS.registerItem("spruce_sign_button", props -> new SignButtonItem(SPRUCE_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> MANGROVE_SIGN_BUTTON_ITEM = ITEMS.registerItem("mangrove_sign_button", props -> new SignButtonItem(MANGROVE_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> BAMBOO_SIGN_BUTTON_ITEM = ITEMS.registerItem("bamboo_sign_button", props -> new SignButtonItem(BAMBOO_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> CHERRY_SIGN_BUTTON_ITEM = ITEMS.registerItem("cherry_sign_button", props -> new SignButtonItem(CHERRY_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> CRIMSON_SIGN_BUTTON_ITEM = ITEMS.registerItem("crimson_sign_button", props -> new SignButtonItem(CRIMSON_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));
    public static DeferredItem<SignButtonItem> WARPED_SIGN_BUTTON_ITEM = ITEMS.registerItem("warped_sign_button", props -> new SignButtonItem(WARPED_SIGN_BUTTON.get(), props.stacksTo(16).useBlockDescriptionPrefix()));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<SignButtonBlockEntity>> SIGN_BUTTON_BLOCK_ENTITY = BLOCK_ENTITIES.register("sign_button", () ->
            new BlockEntityType<>(SignButtonBlockEntity::new, true,
                    ACACIA_SIGN_BUTTON.get(), BIRCH_SIGN_BUTTON.get(), DARK_OAK_SIGN_BUTTON.get(),
                    JUNGLE_SIGN_BUTTON.get(), OAK_SIGN_BUTTON.get(), SPRUCE_SIGN_BUTTON.get(),
                    MANGROVE_SIGN_BUTTON.get(), BAMBOO_SIGN_BUTTON.get(), CHERRY_SIGN_BUTTON.get(),
                    CRIMSON_SIGN_BUTTON.get(), WARPED_SIGN_BUTTON.get()));

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

    private void gatherData(GatherDataEvent.Client event)
    {
        Datagen.gatherData(event);
    }

    private static class Datagen
    {
        public static void gatherData(GatherDataEvent.Client event)
        {
            DataGenerator gen = event.getGenerator();

            gen.addProvider(true, new ModelsAndClientItems(gen.getPackOutput()));
            gen.addProvider(true, new Recipes(gen.getPackOutput(), event.getLookupProvider()));
            gen.addProvider(true, Loot.create(gen.getPackOutput(), event.getLookupProvider()));
            gen.addProvider(true, new BlockTags(gen.getPackOutput(), event.getLookupProvider()));
            gen.addProvider(true, new ItemTags(gen.getPackOutput(), event.getLookupProvider()));
        }

        private static class ModelsAndClientItems extends ModelProvider
        {
            public ModelsAndClientItems(PackOutput output)
            {
                super(output, MODID);
            }

            @Override
            protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
            {
                generateForType(OAK_SIGN_BUTTON, Blocks.OAK_PLANKS, blockModels, OAK_SIGN_BUTTON_ITEM, Items.OAK_SIGN, itemModels);
                generateForType(SPRUCE_SIGN_BUTTON, Blocks.SPRUCE_PLANKS, blockModels, SPRUCE_SIGN_BUTTON_ITEM, Items.SPRUCE_SIGN, itemModels);
                generateForType(BIRCH_SIGN_BUTTON, Blocks.BIRCH_PLANKS, blockModels, BIRCH_SIGN_BUTTON_ITEM, Items.BIRCH_SIGN, itemModels);
                generateForType(JUNGLE_SIGN_BUTTON, Blocks.JUNGLE_PLANKS, blockModels, JUNGLE_SIGN_BUTTON_ITEM, Items.JUNGLE_SIGN, itemModels);
                generateForType(ACACIA_SIGN_BUTTON, Blocks.ACACIA_PLANKS, blockModels, ACACIA_SIGN_BUTTON_ITEM, Items.ACACIA_SIGN, itemModels);
                generateForType(DARK_OAK_SIGN_BUTTON, Blocks.DARK_OAK_PLANKS, blockModels, DARK_OAK_SIGN_BUTTON_ITEM, Items.DARK_OAK_SIGN, itemModels);
                generateForType(MANGROVE_SIGN_BUTTON, Blocks.MANGROVE_PLANKS, blockModels, MANGROVE_SIGN_BUTTON_ITEM, Items.MANGROVE_SIGN, itemModels);
                generateForType(BAMBOO_SIGN_BUTTON, Blocks.BAMBOO_PLANKS, blockModels, BAMBOO_SIGN_BUTTON_ITEM, Items.BAMBOO_SIGN, itemModels);
                generateForType(CHERRY_SIGN_BUTTON, Blocks.CHERRY_PLANKS, blockModels, CHERRY_SIGN_BUTTON_ITEM, Items.CHERRY_SIGN, itemModels);
                generateForType(CRIMSON_SIGN_BUTTON, Blocks.CRIMSON_PLANKS, blockModels, CRIMSON_SIGN_BUTTON_ITEM, Items.CRIMSON_SIGN, itemModels);
                generateForType(WARPED_SIGN_BUTTON, Blocks.WARPED_PLANKS, blockModels, WARPED_SIGN_BUTTON_ITEM, Items.WARPED_SIGN, itemModels);
            }

            private void generateForType(DeferredBlock<SignButtonBlock> block, Block blockParticlesFrom, BlockModelGenerators blockModels,
                                         DeferredItem<SignButtonItem> item, Item itemTextureFrom, ItemModelGenerators itemModels)
            {
                var blockModel = blockModels.createParticleOnlyBlockModel(block.get(), blockParticlesFrom);

                blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(),
                        Variant.variant().with(VariantProperties.MODEL, blockModel)));

                itemModels.generateLayeredItem(item.get(),
                        ModelLocationUtils.getModelLocation(itemTextureFrom),
                        ResourceLocation.fromNamespaceAndPath("signbutton", "item/sign_button"));

                itemModels.itemModelOutput.accept(item.get(),
                        ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item.get())));
            }
        }


        private static class Recipes extends RecipeProvider.Runner
        {
            public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
            {
                super(output, lookupProvider);
            }

            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookup, RecipeOutput output)
            {
                return new VanillaRecipeProvider(lookup, output)
                {

                    @Override
                    protected void buildRecipes()
                    {
                        signRecipe(output, OAK_SIGN_BUTTON_ITEM, Items.OAK_SIGN);
                        signRecipe(output, SPRUCE_SIGN_BUTTON_ITEM, Items.SPRUCE_SIGN);
                        signRecipe(output, BIRCH_SIGN_BUTTON_ITEM, Items.BIRCH_SIGN);
                        signRecipe(output, JUNGLE_SIGN_BUTTON_ITEM, Items.JUNGLE_SIGN);
                        signRecipe(output, ACACIA_SIGN_BUTTON_ITEM, Items.ACACIA_SIGN);
                        signRecipe(output, DARK_OAK_SIGN_BUTTON_ITEM, Items.DARK_OAK_SIGN);
                        signRecipe(output, MANGROVE_SIGN_BUTTON_ITEM, Items.MANGROVE_SIGN);
                        signRecipe(output, BAMBOO_SIGN_BUTTON_ITEM, Items.BAMBOO_SIGN);
                        signRecipe(output, CHERRY_SIGN_BUTTON_ITEM, Items.CHERRY_SIGN);
                        signRecipe(output, CRIMSON_SIGN_BUTTON_ITEM, Items.CRIMSON_SIGN);
                        signRecipe(output, WARPED_SIGN_BUTTON_ITEM, Items.WARPED_SIGN);
                    }

                    private void signRecipe(RecipeOutput consumer, Supplier<SignButtonItem> signButton, Item originalSign)
                    {
                        shapeless(RecipeCategory.MISC, signButton.get())
                                .requires(originalSign)
                                .requires(Items.REDSTONE_TORCH)
                                .unlockedBy("has_torch", has(Items.REDSTONE_TORCH))
                                .unlockedBy("has_sign", has(originalSign))
                                .save(consumer);
                    }
                };
            }

            @Override
            public String getName()
            {
                return "Recipes";
            }
        }

        private static class Loot
        {
            public static LootTableProvider create(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookup)
            {
                return new LootTableProvider(gen, Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK)
                ), lookup);
            }

            public static class BlockTables extends BlockLootSubProvider
            {
                protected BlockTables(HolderLookup.Provider lookup)
                {
                    super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
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
            public BlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup)
            {
                super(packOutput, Registries.BLOCK, lookup,
                        (block) -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow(), MODID);
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
            public ItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup)
            {
                super(packOutput, Registries.ITEM, lookup,
                        (item) -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow(), MODID);
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