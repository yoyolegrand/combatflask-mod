package com.combatflask.registry;

import com.combatflask.CombatFlaskMod;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    // Même convention : REGISTER
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CombatFlaskMod.MODID);

    // (aucun block obligatoire ici ; laisse vide si tu n’en as pas)
}
