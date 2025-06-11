package mymod;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.struct.ObjectMap.OrderedMap;
import mindustry.Vars;
import mindustry.type.ItemStack;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;

public abstract class FlexibleCrafter extends GenericCrafter {

    public Seq<Recipe> recipes = new Seq<>();

    public FlexibleCrafter(String name) {
        super(name);
        hasItems = true;
        hasPower = true;
        update = true;
        solid = true;
    }

    public FlexibleCrafter addRecipe(ItemStack[] input, ItemStack[] output, float craftTime) {
        recipes.add(new Recipe(input, output, craftTime));
        return this;
    }

    @Override
    public void init() {
        super.init();

        if (config != null && config instanceof Seq<?> outer && !outer.isEmpty()) {
            for (Object raw : outer) {
                if (raw instanceof OrderedMap<?, ?> rawRecipe) {
                    Seq<ItemStack> input = parseItemStackArray((Seq<?>) rawRecipe.get("input"));
                    Seq<ItemStack> output = parseItemStackArray((Seq<?>) rawRecipe.get("output"));
                    float craftTime = ((Number) rawRecipe.get("craftTime")).floatValue();

                    addRecipe(input.toArray(ItemStack.class), output.toArray(ItemStack.class), craftTime);
                }
            }
        }
    }

    private Seq<ItemStack> parseItemStackArray(Seq<?> seq) {
        Seq<ItemStack> stacks = new Seq<>();
        for (Object obj : seq) {
            if (obj instanceof Seq<?> pair && pair.size >= 2) {
                String itemName = (String) pair.get(0);
                int amount = ((Number) pair.get(1)).intValue();
                stacks.add(new ItemStack(Vars.content.items().getByName(itemName), amount));
            }
        }
        return stacks;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (FlexibleCrafterBuild e) -> new Bar("bar.progress", Pal.ammo, () -> e.recipeProgress));
        addBar("warmup", (FlexibleCrafterBuild e) -> new Bar("bar.warmup", Pal.power, () -> e.warmup));
    }

    public static class Recipe {
        public ItemStack[] input;
        public ItemStack[] output;
        public float craftTime;

        public Recipe(ItemStack[] input, ItemStack[] output, float craftTime) {
            this.input = input;
            this.output = output;
            this.craftTime = craftTime;
        }
    }

    public abstract class FlexibleCrafterBuild extends GenericCrafterBuild {
        public Recipe currentRecipe = null;
        public float recipeProgress = 0f;

        @Override
        public void updateTile() {
            if (currentRecipe == null) selectRecipe();

            if (currentRecipe != null && canCraft(currentRecipe)) {
                consumeInputs(currentRecipe);

                recipeProgress += getProgressIncrease(currentRecipe.craftTime);
                warmup = Mathf.approachDelta(warmup, 1f, 0.02f);

                if (recipeProgress >= 1f) {
                    produceOutputs(currentRecipe);
                    recipeProgress %= 1f;
                    selectRecipe();
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
                recipeProgress = Mathf.approachDelta(recipeProgress, 0f, 0.05f);
            }

            dumpOutputs();
        }

        public void selectRecipe() {
            for (Recipe r : recipes) {
                if (canCraft(r)) {
                    currentRecipe = r;
                    return;
                }
            }
            currentRecipe = null;
        }

        public boolean canCraft(Recipe r) {
            for (ItemStack stack : r.input) {
                if (items.get(stack.item) < stack.amount) return false;
            }
            return enabled;
        }

        public void consumeInputs(Recipe r) {
            for (ItemStack stack : r.input) {
                items.remove(stack.item, stack.amount);
            }
        }

        public void produceOutputs(Recipe r) {
            for (ItemStack stack : r.output) {
                offload(stack.item, stack.amount);
            }
        }
    }
          }
