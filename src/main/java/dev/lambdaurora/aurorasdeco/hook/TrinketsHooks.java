/*
 * Copyright (c) 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.lambdaurora.aurorasdeco.hook;

import dev.emi.trinkets.api.TrinketsApi;
import dev.lambdaurora.aurorasdeco.registry.AurorasDecoRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;

/**
 * Represents hooks for Trinkets.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public final class TrinketsHooks {
    private static final boolean HAS_TRINKETS = FabricLoader.getInstance().isModLoaded("trinkets");

    private TrinketsHooks() {
        throw new UnsupportedOperationException("Someone tried to instantiate a class only containing static definitions. How?");
    }

    public static void renderBlackboardInTrinketSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                                     int light, LivingEntity entity,
                                                     float scaleX, float scaleY, float scaleZ,
                                                     ModelPart head) {
        if (!HAS_TRINKETS) return;

        // Renders a blackboard if present on a trinket slot (should be the face slot).
        TrinketsApi.getTrinketComponent(entity).ifPresent(trinketComponent -> {
            var res = trinketComponent.getEquipped(stack ->
                    stack.isIn(AurorasDecoRegistry.BLACKBOARD_ITEMS)
            );

            if (res.isEmpty()) return;

            matrices.push();
            matrices.scale(scaleX, scaleY, scaleZ);

            boolean villager = entity instanceof VillagerEntity || entity instanceof ZombieVillagerEntity;
            if (entity.isBaby() && !(entity instanceof VillagerEntity)) {
                matrices.translate(0.0, 0.03125, 0.0);
                matrices.scale(.7f, .7f, .7f);
                matrices.translate(0.0, 1.0, 0.0);
            }
            head.rotate(matrices);

            var stack = res.get(0).getRight();
            HeadFeatureRenderer.translate(matrices, villager);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, ModelTransformation.Mode.HEAD,
                    false,
                    matrices, vertexConsumers, light);
            matrices.pop();
        });
    }
}
