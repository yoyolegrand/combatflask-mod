package com.combatflask;

import com.combatflask.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@Mod.EventBusSubscriber(modid = CombatFlaskMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CombatFlaskClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Utilise le renderer standard des items lancés (bouteilles/potions)
        event.registerEntityRenderer(ModEntities.THROWN_FLASK.get(),
                (EntityRendererProvider.Context ctx) -> new ThrownItemRenderer<>(ctx, 1.0f, true));
        // Le 2e paramètre (1.0f) = échelle du rendu; true = utilise le "item in hand model"
    }
}


