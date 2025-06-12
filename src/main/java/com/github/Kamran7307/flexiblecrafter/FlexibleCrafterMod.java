package com.github.Kamran7307.flexiblecrafter;

import arc.files.Fi;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.world.Block;

public class FlexibleCrafterMod extends Mod {
    public FlexibleCrafterMod() {
        for (Block block : Vars.content.blocks()) {
            if (block instanceof FlexibleCrafter fc) {
                Jval json = findJsonDefinitionFor(block);
                if (json != null && json.has("recipes")) {
                    fc.load(json);
                }
            }
        }
    }

    private Jval findJsonDefinitionFor(Block block) {
        for (var mod : Vars.mods.list()) {
            if (mod.name.equals("flexible-crafter-recipes")) {
                Fi folder = mod.root.child("content/blocks/production");
                if (folder.exists() && folder.isDirectory()) {
                    for (Fi file : folder.list()) {
                        if (file.name().endsWith(".json")) {
                            Jval json = Jval.read(file.readString());
                            if (json.getString("name").equals(block.name)) {
                                return json;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}