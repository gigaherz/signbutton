package dev.gigaherz.signbutton.button;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import dev.gigaherz.signbutton.ModSignButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SignButtonRenderer
        implements BlockEntityRenderer<SignButtonBlockEntity>
{
    public static final Material SIGN_BUTTON_OVERLAY_MATERIAL = new Material(Sheets.SIGN_SHEET, ResourceLocation.fromNamespaceAndPath("signbutton", "entity/sign_button"));

    public static final class SignButtonModel extends Model
    {
        public SignButtonModel(ModelPart rootPart) {
            super(rootPart, RenderType::entityCutoutNoCull);
        }
    }

    public static ModelLayerLocation createSignButtonModelName(WoodType p_171292_) {
        ResourceLocation location = ResourceLocation.parse(p_171292_.name());
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "sign/" + location.getPath()), "signbutton_overlay");
    }

    @EventBusSubscriber(value= Dist.CLIENT, modid= ModSignButton.MODID, bus= EventBusSubscriber.Bus.MOD)
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
            SignButtonWoodTypes.supported().forEach(woodType -> {
                event.registerLayerDefinition(createSignButtonModelName(woodType), Events::createSignOverlayLayer);
            });
        }
    }

    private static Material createSignMaterial(WoodType p_173386_) {
        ResourceLocation location = ResourceLocation.parse(p_173386_.name());
        return new Material(Sheets.SIGN_SHEET, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "entity/signs/" + location.getPath()));
    }

    private final Map<WoodType, SignRenderer.Models> signModels;
    private final Map<WoodType, Model> overlayModels;
    private final Map<WoodType, Material> signMaterials = new HashMap<>();
    private final Font font;

    public SignButtonRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.signModels = SignButtonWoodTypes.supported().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignRenderer.Models(
                        SignRenderer.createSignModel(ctx.getModelSet(), woodType, true),
                        SignRenderer.createSignModel(ctx.getModelSet(), woodType, false)
                )));
        this.overlayModels = SignButtonWoodTypes.supported().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignButtonModel(ctx.bakeLayer(createSignButtonModelName(woodType))))
        );
        this.font = ctx.getFont();
    }

    @Override
    public void render(SignButtonBlockEntity signButtonBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLightIn, int combinedOverlayIn, Vec3 vec3)
    {
        final float scale = 0.6666667F;

        poseStack.pushPose();

        BlockState blockstate = signButtonBlockEntity.getBlockState();

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

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotAroundY));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotAroundX));
        poseStack.translate(0.0, -0.3125, -0.4375D - (powered ? 0.035 : 0));

        WoodType woodtype = signButtonBlockEntity.getWoodType();
        var models = this.signModels.get(woodtype);
        var model = models.wall();
        var overlayModel = this.overlayModels.get(woodtype);
        //model.stick.visible=false;

        poseStack.pushPose();
        poseStack.scale(scale, -scale, -scale);
        {
            Material material = signMaterials.computeIfAbsent(woodtype, woodType -> createSignMaterial(woodtype));
            VertexConsumer ivertexbuilder = material.buffer(bufferSource, NeoForgeRenderTypes::getUnsortedTranslucent);
            model.root().render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        //((MultiBufferSource.BufferSource)bufferSource).endBatch();
        {
            VertexConsumer ivertexbuilder = SIGN_BUTTON_OVERLAY_MATERIAL.buffer(bufferSource, NeoForgeRenderTypes::getUnsortedTranslucent);
            overlayModel.root().render(poseStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        poseStack.popPose();
        poseStack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
        poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        var frontText=signButtonBlockEntity.getFrontText();
        int adjustedColor = SignRenderer.getDarkColor(frontText);

        FormattedCharSequence[] lines = frontText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, 90);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });


        int color1;
        boolean drawOutline;
        int glowCombinedLight;
        if (frontText.hasGlowingText()) {
            color1 = frontText.getColor().getTextColor();
            drawOutline = SignRenderer.isOutlineVisible(signButtonBlockEntity.getBlockPos(), color1);
            glowCombinedLight = LightTexture.FULL_BRIGHT;
        } else {
            color1 = adjustedColor;
            drawOutline = false;
            glowCombinedLight = combinedLightIn;
        }

        for (int line = 0; line < 4; ++line)
        {
            FormattedCharSequence text = lines[line];
            if (text != null) {
                float f3 = (float)(-font.width(text) / 2);
                if (drawOutline) {
                    font.drawInBatch8xOutline(text, f3, (float)(line * 10 - 20), color1, adjustedColor, poseStack.last().pose(), bufferSource, glowCombinedLight);
                } else {
                    font.drawInBatch(text, f3, (float)(line * 10 - 20), color1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, glowCombinedLight);
                }
            }
        }

        poseStack.popPose();
    }
}

