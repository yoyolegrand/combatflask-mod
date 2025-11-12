package com.combatflask.registry;

import com.combatflask.CombatFlaskMod;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CombatFlaskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributesHook {

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        // Give the attributes to the player (add others here if you want)
        event.add(EntityType.PLAYER, ModAttributes.FLASK_EFFECTIVENESS.get());
        event.add(EntityType.PLAYER, ModAttributes.FLASK_DURATION.get());
    }
}
