package com.combatflask;

import com.combatflask.registry.ModAttributes;
import com.combatflask.registry.ModBlocks;
import com.combatflask.registry.ModEntities;
import com.combatflask.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CombatFlaskMod.MODID)
public class CombatFlaskMod {
    public static final String MODID = "combatflask";

    public CombatFlaskMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.REGISTER.register(bus);
        ModBlocks.REGISTER.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModAttributes.REGISTER.register(bus);
    }
}
