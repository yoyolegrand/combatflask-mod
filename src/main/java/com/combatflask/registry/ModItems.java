package com.combatflask.registry;

import com.combatflask.CombatFlaskMod;
import com.combatflask.content.item.CombatFlaskItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, CombatFlaskMod.MODID);

    public static final RegistryObject<Item> FIRE_FLASK = REGISTER.register(
            "fire_flask",
            () -> new CombatFlaskItem(
                    CombatFlaskItem.FlaskType.FIRE,
                    new Item.Properties().stacksTo(16))
    );

    public static final RegistryObject<Item> FROST_FLASK = REGISTER.register(
            "frost_flask",
            () -> new CombatFlaskItem(
                    CombatFlaskItem.FlaskType.FROST,
                    new Item.Properties().stacksTo(16))
    );
}
