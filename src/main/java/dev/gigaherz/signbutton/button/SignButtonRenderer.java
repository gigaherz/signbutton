package dev.gigaherz.signbutton.button;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.gigaherz.signbutton.SignButtonMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SignButtonRenderer
        implements BlockEntityRenderer<SignButtonBlockEntity, SignButtonRenderer.SignButtonRenderState>
{
    public static final Material SIGN_BUTTON_OVERLAY_MATERIAL = new Material(Sheets.SIGN_SHEET, Identifier.fromNamespaceAndPath("signbutton", "entity/sign_button"));
    public static final float SIGN_SCALE = 0.6666667F;

    public static final class SignButtonModel extends Model.Simple
    {
        public SignButtonModel(ModelPart rootPart) {
            super(rootPart, (Identifier tex) -> RenderTypes.entityTranslucent(tex, false));
        }
    }

    public static class SignButtonRenderState extends SignRenderState
    {
        public boolean powered;
        public Direction facing;
        public AttachFace face;
        public WoodType woodtype;
    }

    public static ModelLayerLocation createSignButtonModelName(WoodType p_171292_) {
        Identifier location = Identifier.parse(p_171292_.name());
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(location.getNamespace(), "sign/" + location.getPath()), "signbutton_overlay");
    }

    @EventBusSubscriber(value= Dist.CLIENT, modid= SignButtonMod.MODID)
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
        Identifier location = Identifier.parse(p_173386_.name());
        return new Material(Sheets.SIGN_SHEET, Identifier.fromNamespaceAndPath(location.getNamespace(), "entity/signs/" + location.getPath()));
    }

    private final Map<WoodType, SignRenderer.Models> signModels;
    private final Map<WoodType, Model.Simple> overlayModels;
    private final Map<WoodType, Material> signMaterials = new HashMap<>();
    private final Font font;
    private final MaterialSet materials;

    public SignButtonRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.signModels = SignButtonWoodTypes.supported().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignRenderer.Models(
                        SignRenderer.createSignModel(ctx.entityModelSet(), woodType, true),
                        SignRenderer.createSignModel(ctx.entityModelSet(), woodType, false)
                )));
        this.overlayModels = SignButtonWoodTypes.supported().collect(ImmutableMap.toImmutableMap(Function.identity(),
                woodType -> new SignButtonModel(ctx.bakeLayer(createSignButtonModelName(woodType))))
        );
        this.font = ctx.font();
        this.materials = ctx.materials();
    }

    @Override
    public SignButtonRenderState createRenderState()
    {
        return new SignButtonRenderState();
    }

    @Override
    public void extractRenderState(SignButtonBlockEntity signButtonBlockEntity, SignButtonRenderState state, float p_446851_, Vec3 p_445788_, @Nullable ModelFeatureRenderer.CrumblingOverlay p_446944_)
    {
        BlockEntityRenderer.super.extractRenderState(signButtonBlockEntity, state, p_446851_, p_445788_, p_446944_);

        // from AbstractSignRenderer
        state.maxTextLineWidth = signButtonBlockEntity.getMaxTextLineWidth();
        state.textLineHeight = signButtonBlockEntity.getTextLineHeight();
        state.frontText = signButtonBlockEntity.getFrontText();
        state.backText = signButtonBlockEntity.getBackText();
        state.isTextFilteringEnabled = Minecraft.getInstance().isTextFilteringEnabled();
        state.drawOutline = AbstractSignRenderer.isOutlineVisible(signButtonBlockEntity.getBlockPos());
        // end from

        BlockState blockstate = signButtonBlockEntity.getBlockState();

        state.powered = blockstate.getValue(SignButtonBlock.POWERED);
        state.facing = blockstate.getValue(SignButtonBlock.FACING);
        state.face = blockstate.getValue(SignButtonBlock.FACE);

        state.woodtype = signButtonBlockEntity.getWoodType();
    }

    @Override
    public void submit(SignButtonRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState)
    {
        int rotAroundX = switch (state.face)
        {
            case CEILING -> 90;
            case FLOOR -> -90;
            default -> 0;
        };

        int rotAroundY = switch (state.facing)
        {
            case NORTH -> 180;
            case EAST -> 90;
            case WEST -> -90;
            default -> 0;
        };

        var models = this.signModels.get(state.woodtype);

        var baseModel = models.wall();
        var overlayModel = this.overlayModels.get(state.woodtype);

        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotAroundY));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotAroundX));
            poseStack.translate(0.0, -0.3125, -0.4375D - (state.powered ? 0.035 : 0));

            poseStack.pushPose();
            {
                poseStack.scale(SIGN_SCALE, -SIGN_SCALE, -SIGN_SCALE);

                var signMaterial = Sheets.getSignMaterial(state.woodtype);
                var baseRenderType = signMaterial.renderType(baseModel::renderType);
                collector.order(1).submitModel(
                        baseModel, Unit.INSTANCE, poseStack, baseRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1,
                        this.materials.get(signMaterial), 0, state.breakProgress
                );

                var overlayMaterial = SIGN_BUTTON_OVERLAY_MATERIAL;
                var overlayRenderType = overlayMaterial.renderType(RenderTypes::entityTranslucent);
                collector.order(1).submitModel(
                        overlayModel, Unit.INSTANCE, poseStack, overlayRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1,
                        this.materials.get(overlayMaterial), 0, state.breakProgress
                );
            }
            poseStack.popPose();

            this.submitSignText(state, poseStack, collector, true);
            this.submitSignText(state, poseStack, collector, false);
        }
        poseStack.popPose();
    }

    // copied from SignRenderer
    private void submitSignText(SignRenderState state, PoseStack poseStack, SubmitNodeCollector collector, boolean front) {
        SignText signtext = front ? state.frontText : state.backText;
        if (signtext != null) {
            poseStack.pushPose();
            Vec3 offset = SignRenderer.TEXT_OFFSET;
            if (!front) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }

            float f1 = 0.015625F * SIGN_SCALE;
            poseStack.translate(offset);
            poseStack.scale(f1, -f1, f1);
            int i = AbstractSignRenderer.getDarkColor(signtext);
            int j = 4 * state.textLineHeight / 2;
            FormattedCharSequence[] aformattedcharsequence = signtext.getRenderMessages(state.isTextFilteringEnabled, p_445219_ -> {
                List<FormattedCharSequence> list = this.font.split(p_445219_, state.maxTextLineWidth);
                return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
            });
            int k;
            boolean flag;
            int l;
            if (signtext.hasGlowingText()) {
                k = signtext.getColor().getTextColor();
                flag = k == DyeColor.BLACK.getTextColor() || state.drawOutline;
                l = 15728880;
            } else {
                k = i;
                flag = false;
                l = state.lightCoords;
            }

            for (int i1 = 0; i1 < 4; i1++) {
                FormattedCharSequence formattedcharsequence = aformattedcharsequence[i1];
                float f = -this.font.width(formattedcharsequence) / 2;
                collector.submitText(
                        poseStack, f, i1 * state.textLineHeight - j, formattedcharsequence, false, Font.DisplayMode.POLYGON_OFFSET, l, k, 0, flag ? i : 0
                );
            }

            poseStack.popPose();
        }
    }
}

