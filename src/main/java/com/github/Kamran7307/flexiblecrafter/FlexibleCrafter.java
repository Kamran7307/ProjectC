package com.github.Kamran7307.flexiblecrafter;

import arc.struct.Seq;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.type.*;
import mindustry.world.blocks.production.GenericCrafter;

public class FlexibleCrafter extends GenericCrafter {
    public Seq<Recipe> recipes = new Seq<>();

    public FlexibleCrafter(String name) {
        super(name);
        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        outputsLiquid = true;
    }

    public void load(Jval json) {
        if (json.has("recipes")) {
            for (Jval recipeJson : json.get("recipes").asArray()) {
                Recipe r = new Recipe();

                if (recipeJson.has("itemInput")) {
                    Seq<ItemStack> inputs = new Seq<>();
                    for (Jval input : recipeJson.get("itemInput").asArray()) {
                        String itemName = input.asArray().get(0).asString();
                        int amount = input.asArray().get(1).asInt();
                        Item item = Vars.content.items().find(i -> i.name.equals(itemName));
                        inputs.add(new ItemStack(item, amount));
                    }
                    r.itemInput = inputs.toArray(ItemStack.class);
                }

                if (recipeJson.has("liquidInput")) {
                    Seq<LiquidStack> inputs = new Seq<>();
                    for (Jval input : recipeJson.get("liquidInput").asArray()) {
                        String liquidName = input.asArray().get(0).asString();
                        float amount = input.asArray().get(1).asFloat();
                        Liquid liquid = Vars.content.liquids().find(l -> l.name.equals(liquidName));
                        inputs.add(new LiquidStack(liquid, amount));
                    }
                    r.liquidInput = inputs.toArray(LiquidStack.class);
                }

                r.powerInput = recipeJson.getFloat("powerInput", 0f);

                if (recipeJson.has("itemOutput")) {
                    String outName = recipeJson.get("itemOutput").asArray().get(0).asString();
                    int amount = recipeJson.get("itemOutput").asArray().get(1).asInt();
                    Item item = Vars.content.items().find(i -> i.name.equals(outName));
                    r.itemOutput = new ItemStack(item, amount);
                }

                if (recipeJson.has("liquidOutput")) {
                    String outName = recipeJson.get("liquidOutput").asArray().get(0).asString();
                    float amount = recipeJson.get("liquidOutput").asArray().get(1).asFloat();
                    Liquid liquid = Vars.content.liquids().find(l -> l.name.equals(outName));
                    r.liquidOutput = new LiquidStack(liquid, amount);
                }

                r.powerOutput = recipeJson.getFloat("powerOutput", 0f);
                r.craftTime = recipeJson.getFloat("craftTime", 60f);

                recipes.add(r);
            }
        }
    }
                          }
