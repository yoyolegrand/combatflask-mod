package com.combatflask.registry;

import com.combatflask.CombatFlaskMod;
import com.combatflask.content.entity.ThrownFlaskEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CombatFlaskMod.MODID);

    public static final RegistryObject<EntityType<ThrownFlaskEntity>> THROWN_FLASK =
            ENTITIES.register("thrown_flask",
                    () -> EntityType.Builder
                            .<ThrownFlaskEntity>of(ThrownFlaskEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build(CombatFlaskMod.MODID + ":thrown_flask")
            );
}
