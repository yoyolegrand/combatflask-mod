package com.combatflask.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.particles.ParticleType;

public class CompatRefs {

    // Effets (à ajuster si vos IDs exacts diffèrent)
    public static final ResourceLocation IRON_STUN_ID = new ResourceLocation("irons_spellbooks", "stun");
    public static final ResourceLocation TMAG_ELECTROCUTE_ID = new ResourceLocation("to_magic_extra", "electrocute");

    // Particules (IDs à adapter si différents)
    public static final ResourceLocation IRON_SPARK_PARTICLE_ID = new ResourceLocation("irons_spellbooks", "lightning_spark");
    public static final ResourceLocation TMAG_ARC_PARTICLE_ID   = new ResourceLocation("to_magic_extra", "electro_arc");

    public static MobEffect getIronStun() {
        return BuiltInRegistries.MOB_EFFECT.get(IRON_STUN_ID);
    }

    public static MobEffect getElectrocute() {
        return BuiltInRegistries.MOB_EFFECT.get(TMAG_ELECTROCUTE_ID);
    }

    public static ParticleType<?> getIronSpark() {
        return BuiltInRegistries.PARTICLE_TYPE.get(IRON_SPARK_PARTICLE_ID);
    }

    public static ParticleType<?> getElectroArc() {
        return BuiltInRegistries.PARTICLE_TYPE.get(TMAG_ARC_PARTICLE_ID);
    }
}
