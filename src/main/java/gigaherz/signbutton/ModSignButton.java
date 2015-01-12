package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = ModSignButton.MODID, name = ModSignButton.MODNAME, version = ModSignButton.VERSION)
public class ModSignButton {
    public static final String MODID = "SignButton";
    public static final String MODNAME = "SignButton";
    public static final String VERSION = "1.0";

    public static Block signButton;
    public static Item itemSignButton;

    @Mod.Instance(value = ModSignButton.MODID)
    public static ModSignButton instance;

    @SidedProxy(clientSide = "gigaherz.signbutton.client.ClientProxy", serverSide = "gigaherz.signbutton.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        itemSignButton = new ItemSignButton().setUnlocalizedName("signButtonItem");
        GameRegistry.registerItem(itemSignButton, "signButtonItem");

        signButton = new BlockSignButton().setHardness(0.5F).setStepSound(Block.soundTypeWood).setUnlocalizedName("signButton");
        GameRegistry.registerBlock(signButton, "signButton");

        GameRegistry.registerTileEntity(TileSignButton.class, "signButtonTile");

        ItemBlock b;

        proxy.registerPreRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.registerRenderers();

        // Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(signButton, 1), Items.sign, Blocks.stone_button);
        GameRegistry.addShapelessRecipe(new ItemStack(signButton, 1), Items.sign, Blocks.wooden_button);
    }
}
