package com.combatflask.content.entity;

import com.combatflask.content.item.CombatFlaskItem;
import com.combatflask.system.FlaskCloudManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownFlaskEntity extends ThrowableItemProjectile {
    private CombatFlaskItem.FlaskType flaskType = CombatFlaskItem.FlaskType.FIRE;

    public ThrownFlaskEntity(EntityType<? extends ThrownFlaskEntity> type, Level level) {
        super(type, level);
    }

    public ThrownFlaskEntity(EntityType<? extends ThrownFlaskEntity> type, Level level, LivingEntity owner, CombatFlaskItem.FlaskType ftype) {
        super(type, owner, level);
        this.flaskType = (ftype == null) ? CombatFlaskItem.FlaskType.FIRE : ftype;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GLASS_BOTTLE;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            ServerLevel sl = (ServerLevel) level();
            LivingEntity owner = (getOwner() instanceof LivingEntity le) ? le : null;

            // 1) Burst d'impact (signature SANS 'hit')
            FlaskCloudManager.applyImmediateImpact(sl, getX(), getY(), getZ(), owner, this.flaskType);

            // 2) Nuage persistant
            FlaskCloudManager.spawnFlaskCloud(sl, getX(), getY(), getZ(), owner, this.flaskType);

            this.discard();
        }
    }
}