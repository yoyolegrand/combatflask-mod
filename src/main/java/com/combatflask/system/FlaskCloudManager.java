package com.combatflask.system;

import com.combatflask.CombatFlaskMod;
import com.combatflask.content.item.CombatFlaskItem;
import com.combatflask.registry.ModAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.Objects;

/**
 * Gestion des nuages (flasques) + ticks de dégâts.
 */
@Mod.EventBusSubscriber(modid = CombatFlaskMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlaskCloudManager {

    // ----- Réglages généraux -----
    private static final float CLOUD_RADIUS_BASE = 3.5F;
    private static final float CLOUD_RADIUS_PER_TICK = 0.0F;
    private static final int   CLOUD_DURATION_TICKS = 200; // ~10s

    // Dégâts par tick (tous les PERIOD_TICKS) — valeur “brute” avant multiplicateurs
    private static final int PERIOD_TICKS = 10; // 0.5s
    private static final float FIRE_DMG  = 2.0F;
    private static final float FROST_DMG = 2.0F;
    private static final float STORM_DMG = 3.0F;

    // Clés NBT internes
    private static final String TAG_TYPE       = "combatflask_type";
    private static final String TAG_NEXT_HURT  = "combatflask_nextHurt";

    // Neutre : garder le cloud vivant (Luck sans particules/icône)
    private static final ResourceLocation LUCK_ID = new ResourceLocation("minecraft", "luck");

    /* ---------- Spawns ---------- */

    public static void spawnFlaskCloud(Level level,
                                       double x, double y, double z,
                                       LivingEntity owner,
                                       CombatFlaskItem.FlaskType type)
    {
        if (level.isClientSide()) return;

        AreaEffectCloud cloud = new AreaEffectCloud(level, x, y, z);
        if (owner != null) cloud.setOwner(owner);

        cloud.setRadius(CLOUD_RADIUS_BASE);
        cloud.setRadiusPerTick(CLOUD_RADIUS_PER_TICK);
        cloud.setDuration(CLOUD_DURATION_TICKS);
        cloud.setWaitTime(0);

        // Effet “neutre” invisible pour maintenir le cloud
        cloud.addEffect(new MobEffectInstance(
                Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(LUCK_ID)),
                40, 0, true, false, false
        ));

        switch (type) {
            case FIRE -> {
                cloud.setParticle(ParticleTypes.FLAME);
                // On marque pour le tick manager
                cloud.getPersistentData().putString(TAG_TYPE, "FIRE");
            }
            case FROST -> {
                cloud.setParticle(ParticleTypes.CLOUD);
                cloud.getPersistentData().putString(TAG_TYPE, "FROST");
            }
            case STORM -> {
                // Rayon légèrement modulé pour le feeling
                float durScale = (float) getDur(owner);
                float startR   = clamp(0.9F + 0.1F * durScale, 0.9F, 1.25F) * CLOUD_RADIUS_BASE;
                cloud.setRadius(startR);
                cloud.setRadiusPerTick( (float)(CLOUD_RADIUS_PER_TICK * durScale) );
                cloud.setDuration(scaleTicks(CLOUD_DURATION_TICKS, owner));

                // Particules : si une particule compat est dispo (ParticleOptions), on l'utilise
                ParticleOptions ironSpark = CompatRefs.getIronSpark(); // peut être null si pas de dépendance
                cloud.setParticle(ironSpark != null ? ironSpark : ParticleTypes.ELECTRIC_SPARK);

                cloud.getPersistentData().putString(TAG_TYPE, "STORM");
            }
        }

        level.addFreshEntity(cloud);
    }

    /* ---------- Dégâts / effets immédiats à l’impact ---------- */

    public static void applyImmediateImpact(ServerLevel sl,
                                            double x, double y, double z,
                                            LivingEntity owner,
                                            CombatFlaskItem.FlaskType type)
    {
        float eff = (float) getEff(owner);
        AABB box = new AABB(x - 2.0, y - 1.0, z - 2.0, x + 2.0, y + 1.0, z + 2.0);

        List<LivingEntity> list = sl.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive);
        for (LivingEntity le : list) {
            switch (type) {
                case FIRE -> {
                    le.setSecondsOnFire(3);
                    le.hurt(sl.damageSources().inFire(), Math.max(1.0F, 2.0F * eff));
                }
                case FROST -> {
                    // petit burst de “froid” immédiat
                    le.hurt(sl.damageSources().freeze(), Math.max(1.0F, 1.0F * eff));
                }
                case STORM -> {
                    // impact électrique initial
                    le.hurt(sl.damageSources().lightningBolt(), Math.max(1.0F, 2.0F * eff));
                }
            }
        }
    }

    /* ---------- Tick global : dégâts continus tant qu’on reste dans le nuage ---------- */

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        long now = server.getTickCount();
        for (ServerLevel level : server.getAllLevels()) {
            tickClouds(level, now);
        }
    }

    private static void tickClouds(ServerLevel level, long now) {
        List<AreaEffectCloud> clouds = level.getEntitiesOfClass(
                AreaEffectCloud.class,
                new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        );

        for (AreaEffectCloud cloud : clouds) {
            String tag = cloud.getPersistentData().getString(TAG_TYPE);
            if (tag == null || tag.isEmpty()) continue;

            float r = cloud.getRadius();
            if (r <= 0.0F) continue;

            // tick des entités dans le cylindre (±r en X/Z ; ±1 bloc en Y)
            AABB aabb = new AABB(
                    cloud.getX() - r, cloud.getY() - 1.0, cloud.getZ() - r,
                    cloud.getX() + r, cloud.getY() + 1.0, cloud.getZ() + r
            );
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isAlive);

            int next = cloud.getPersistentData().getInt(TAG_NEXT_HURT);
            if (now < next) continue; // encore trop tôt

            // programmer le prochain tick de dégâts
            cloud.getPersistentData().putInt(TAG_NEXT_HURT, (int) (now + PERIOD_TICKS));

            // appliquer les dégâts/effets
            for (LivingEntity le : targets) {
                switch (tag) {
                    case "FIRE" -> {
                        float dmg = FIRE_DMG * (float) getEff(cloud.getOwner());
                        le.setSecondsOnFire(1);
                        le.hurt(level.damageSources().inFire(), dmg);
                    }
                    case "FROST" -> {
                        float dmg = FROST_DMG * (float) getEff(cloud.getOwner());
                        le.hurt(level.damageSources().indirectMagic(le, null), dmg);
                        // Weakness I pendant 2s, “refreshed” tant qu’on reste dedans
                        le.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS,
                                40, 0, true, false, true));
                        // Slowness I pour le ‘feeling’ de givre
                        le.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                                40, 0, true, false, true));
                    }
                    case "STORM" -> {
                        float dmg = STORM_DMG * (float) getEff(cloud.getOwner());
                        le.hurt(level.damageSources().lightningBolt(), dmg);
                    }
                }
            }
        }
    }

    /* ---------- Helpers ---------- */

    private static double getEff(LivingEntity owner) {
        if (owner == null) return 1.0;
        var attr = owner.getAttribute(ModAttributes.FLASK_EFFECTIVENESS.get());
        return attr != null ? attr.getValue() : 1.0;
    }

    private static double getDur(LivingEntity owner) {
        if (owner == null) return 1.0;
        var attr = owner.getAttribute(ModAttributes.FLASK_DURATION.get());
        return attr != null ? attr.getValue() : 1.0;
    }

    private static int scaleTicks(int base, LivingEntity owner) {
        return (int) Math.max(1, Math.round(base * getDur(owner)));
    }

    private static float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    /* ---- Compatibilité facultative : retourne une particule si dispo, sinon null ---- */
    public static final class CompatRefs {
        /**
         * Si tu ajoutes Iron’s Spells (ou autre), retourne ici leur particule
         * en tant que ParticleOptions (souvent SimpleParticleType).
         * Retourne null si non trouvée.
         */
        public static ParticleOptions getIronSpark() {
            try {
                // Exemple si une particule existe: new ResourceLocation("modid","particle_name")
                // et que Registry<ParticleType> expose une SimpleParticleType qui implémente ParticleOptions.
                ResourceLocation id = new ResourceLocation("irons_spells", "electric_spark");
                var pType = BuiltInRegistries.PARTICLE_TYPE.getOptional(id).orElse(null);
                // La plupart des SimpleParticleType *sont* des ParticleOptions utilisables directement:
                if (pType instanceof ParticleOptions opts) return opts;
            } catch (Throwable ignored) {}
            return null;
        }
    }
}
