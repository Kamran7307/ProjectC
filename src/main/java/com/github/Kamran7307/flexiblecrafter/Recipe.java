
package com.github.Kamran7307.flexiblecrafter;

import mindustry.type.*;

public class Recipe {
    public ItemStack[] itemInput = new ItemStack[0];
    public LiquidStack[] liquidInput = new LiquidStack[0];
    public float powerInput = 0f;

    public ItemStack itemOutput = null;
    public LiquidStack liquidOutput = null;
    public float powerOutput = 0f;

    public float craftTime = 60f;
}
