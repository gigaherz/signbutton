package gigaherz.signbutton.button;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import gigaherz.signbutton.ModSignButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getWoodType;

public class SignButtonTileEntityRenderer
        implements BlockEntityRenderer<SignButtonTileEntity>
{
    public static final Material SIGN_BUTTON_OVERLAY_MATERIAL = new Material(Sheets.SIGN_SHEET, new ResourceLocation("signbutton", "entity/sign_button"));

    public static final class SignModel extends Model
    {
        public final ModelPart root;

        public SignModel(ModelPart p_173657_) {
            super(RenderType::entityCutoutNoCull);
            this.root = p_173657_;
        }

        public void renderToBuffer(PoseStack p_112510_, VertexConsumer p_112511_, int p_112512_, int p_112513_, float p_112514_, float p_112515_, float p_112516_, float p_112517_) {
            this.root.render(p_112510_, p_112511_, p_112512_, p_112513_, p_112514_, p_112515_, p_112516_, p_112517_);
        }
    }

    public static ModelLayerLocation createSignButtonModelName(WoodType woodType) {
        return new ModelLayerLocation(new ResourceLocation("sign/" + woodType.name()), "signbutton_overlay");
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid= ModSignButton.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class Events
    {
        public static LayerDefinition createSignOverlayLayer() {
            CubeDeformation def = new CubeDeformation(0.01f);
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, def), PartPose.ZERO);
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @SubscribeEvent
        public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            WoodType.values().forEach(woodType -> event.registerLayerDefinition(createSignButtonModelName(woodType), Events::createSignOverlayLayer));
        }
    }

    private static Material createSignMaterial(WoodType p_173386_) {
        ResourceLocation location = new ResourceLocation(p_173386_.name());
        return new Material(Sheets.SIGN_SHEET, new ResourceLocation(location.getNamespace(), "entity/signs/" + location.getPath()));
    }

    private final Map<WoodType, SignRenderer.SignModel> signModels;
    private final Map<WoodType, SignModel> overlayModels;
    private final Map<WoodType, Material> signMaterials = new HashMap<>();
    private final Font font;

    public SignButtonTileEntityRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.signModels = WoodType.values().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignRenderer.SignModel(ctx.bakeLayer(ModelLayers.createSignModelName(woodType)))));
        this.overlayModels = WoodType.values().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignModel(ctx.bakeLayer(createSignButtonModelName(woodType)))));
        this.font = ctx.getFont();
    }

    @Override
    public void render(SignButtonTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        final float scale = 0.6666667F;

        matrixStackIn.pushPose();

        BlockState blockstate = tileEntityIn.getBlockState();

        boolean powered = blockstate.getValue(SignButtonBlock.POWERED);
        Direction facing = blockstate.getValue(SignButtonBlock.FACING);
        AttachFace face = blockstate.getValue(SignButtonBlock.FACE);

        int rotAroundX = 0;
        switch (face)
        {
            case CEILING:
                rotAroundX = 90;
                break;
            case FLOOR:
                rotAroundX = -90;
                break;
        }

        int rotAroundY = 0;
        switch (facing)
        {
            case SOUTH:
                break;
            case NORTH:
                rotAroundY = 180;
                break;
            case EAST:
                rotAroundY = 90;
                break;
            case WEST:
                rotAroundY = -90;
                break;
        }

        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotAroundY));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(rotAroundX));
        matrixStackIn.translate(0.0, -0.3125, -0.4375D - (powered ? 0.035 : 0));

        WoodType woodtype = getWoodType(blockstate.getBlock());
        SignRenderer.SignModel model = this.signModels.get(woodtype);
        SignModel overlayModel = this.overlayModels.get(woodtype);
        model.stick.visible=false;

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale, -scale, -scale);
        {
            Material material = signMaterials.computeIfAbsent(woodtype, woodType -> createSignMaterial(woodtype));
            VertexConsumer ivertexbuilder = material.buffer(bufferIn, ButtonRenderTypes::entityTranslucentUnsorted);
            model.root.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        //((MultiBufferSource.BufferSource)bufferIn).endBatch();
        {
            VertexConsumer ivertexbuilder = SIGN_BUTTON_OVERLAY_MATERIAL.buffer(bufferIn, ButtonRenderTypes::entityTranslucentUnsorted);
            overlayModel.root.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        matrixStackIn.popPose();
        matrixStackIn.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
        matrixStackIn.scale(0.010416667F, -0.010416667F, 0.010416667F);

        int color = tileEntityIn.getColor().getTextColor();
        int red = (int) ((double) NativeImage.getR(color) * 0.4D);
        int green = (int) ((double) NativeImage.getG(color) * 0.4D);
        int blue = (int) ((double) NativeImage.getB(color) * 0.4D);
        int adjustedColor = NativeImage.combine(0, blue, green, red);

        FormattedCharSequence[] lines = tileEntityIn.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, 90);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });
        for (int line = 0; line < 4; ++line)
        {
            FormattedCharSequence text = lines[line];
            if (text != null) {
                float f3 = (float)(-font.width(text) / 2);
                font.drawInBatch(text, f3, (float)(line * 10 - 20), adjustedColor, false, matrixStackIn.last().pose(), bufferIn, false, 0, combinedLightIn);
            }
        }

        matrixStackIn.popPose();
    }

    private static class ButtonRenderTypes extends RenderType
    {
        private ButtonRenderTypes(String name, VertexFormat fmt, VertexFormat.Mode glMode, int bufferSize, boolean useDelegate, boolean useSorting, Runnable setupRendering, Runnable cleanupRendering)
        {
            super(name, fmt, glMode, bufferSize, useDelegate, useSorting, setupRendering, cleanupRendering);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture, boolean doOverlay) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(doOverlay);
            return create("entity_translucent_unsorted", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false/*no sorting*/, compositeState);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture) {
            return entityTranslucentUnsorted(texture, true);
        }
    }
}