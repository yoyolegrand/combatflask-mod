package com.combatflask.registry;

import com.combatflask.CombatFlaskMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {

    // Registry of attributes for the mod
    public static final DeferredRegister<Attribute> REGISTER =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, CombatFlaskMod.MODID);

    // Multiplier for flask power (0.0 – 10.0, default 1.0)
    public static final RegistryObject<Attribute> FLASK_EFFECTIVENESS = REGISTER.register(
            "flask_effectiveness",
            () -> new RangedAttribute(
                    "attribute.name." + CombatFlaskMod.MODID + ".flask_effectiveness",
                    1.0D,   // default
                    0.0D,   // min
                    10.0D   // max
            ).setSyncable(true)
    );

    // Multiplier for flask cloud duration (0.0 – 10.0, default 1.0)
    public static final RegistryObject<Attribute> FLASK_DURATION = REGISTER.register(
            "flask_duration",
            () -> new RangedAttribute(
                    "attribute.name." + CombatFlaskMod.MODID + ".flask_duration",
                    1.0D,   // default
                    0.0D,   // min
                    10.0D   // max
            ).setSyncable(true)
    );
}
