package com.combatflask.content.entity;

import com.combatflask.content.item.CombatFlaskItem;
import com.combatflask.system.FlaskCloudManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class ThrownFlaskEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE =
            SynchedEntityData.defineId(ThrownFlaskEntity.class, EntityDataSerializers.INT);

    private CombatFlaskItem.FlaskType flaskType = CombatFlaskItem.FlaskType.FIRE;

    public ThrownFlaskEntity(EntityType<? extends ThrownFlaskEntity> type, Level level) {
        super(type, level);
    }

    public ThrownFlaskEntity(EntityType<? extends ThrownFlaskEntity> type, Level level, LivingEntity owner, CombatFlaskItem.FlaskType ftype) {
        super(type, owner, level);
        setFlaskType(ftype == null ? CombatFlaskItem.FlaskType.FIRE : ftype);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GLASS_BOTTLE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, CombatFlaskItem.FlaskType.FIRE.ordinal());
    }

    public void setFlaskType(CombatFlaskItem.FlaskType type) {
        this.flaskType = type;
        this.entityData.set(DATA_TYPE, type.ordinal());
    }

    public CombatFlaskItem.FlaskType getFlaskType() {
        int idx = Mth.clamp(this.entityData.get(DATA_TYPE), 0, CombatFlaskItem.FlaskType.values().length - 1);
        return CombatFlaskItem.FlaskType.values()[idx];
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_TYPE.equals(key)) {
            this.flaskType = getFlaskType();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("FlaskType", this.flaskType.name());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CombatFlaskItem.FlaskType type = CombatFlaskItem.FlaskType.FIRE;
        if (tag.contains("FlaskType")) {
            try {
                type = CombatFlaskItem.FlaskType.valueOf(tag.getString("FlaskType"));
            } catch (IllegalArgumentException ignored) {
            }
        }
        setFlaskType(type);
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