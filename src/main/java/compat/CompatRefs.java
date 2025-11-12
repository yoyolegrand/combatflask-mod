package com.combatflask.compat;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class CompatRefs {

    // Effets (à ajuster si vos IDs exacts diffèrent)
    public static final ResourceLocation IRON_STUN_ID = new ResourceLocation("irons_spellbooks", "stun");
    public static final ResourceLocation TMAG_ELECTROCUTE_ID = new ResourceLocation("to_magic_extra", "electrocute");

    // Particules (IDs à adapter si différents)
    public static final ResourceLocation IRON_SPARK_PARTICLE_ID = new ResourceLocation("irons_spellbooks", "lightning_spark");
    public static final ResourceLocation TMAG_ARC_PARTICLE_ID   = new ResourceLocation("to_magic_extra", "electro_arc");

    private CompatRefs() {}

    public static MobEffect getIronStun() {
        return BuiltInRegistries.MOB_EFFECT.getOptional(IRON_STUN_ID).orElse(null);
    }

    public static MobEffect getElectrocute() {
        return BuiltInRegistries.MOB_EFFECT.getOptional(TMAG_ELECTROCUTE_ID).orElse(null);
    }

    public static ParticleOptions getIronSpark() {
        var type = BuiltInRegistries.PARTICLE_TYPE.getOptional(IRON_SPARK_PARTICLE_ID).orElse(null);
        return type instanceof ParticleOptions options ? options : null;
    }

    public static ParticleOptions getElectroArc() {
        var type = BuiltInRegistries.PARTICLE_TYPE.getOptional(TMAG_ARC_PARTICLE_ID).orElse(null);
        return type instanceof ParticleOptions options ? options : null;
    }
}
