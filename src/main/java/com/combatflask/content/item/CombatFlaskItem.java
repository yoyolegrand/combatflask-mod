package com.combatflask.content.item;

import com.combatflask.registry.ModEntities;
import com.combatflask.content.entity.ThrownFlaskEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CombatFlaskItem extends Item {
    public enum FlaskType { FIRE, FROST, STORM }

    private final FlaskType type;

    // >>> ORDRE FIXE: (type, props)
    public CombatFlaskItem(FlaskType type, Properties props) {
        super(props);
        this.type = type;
    }

    public FlaskType getType() { return type; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            ThrownFlaskEntity proj = new ThrownFlaskEntity(
                    ModEntities.THROWN_FLASK.get(), level, player, this.type);
            proj.setItem(stack.copyWithCount(1));
            proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.9F, 1.0F);
            level.addFreshEntity(proj);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        player.swing(hand, true);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
