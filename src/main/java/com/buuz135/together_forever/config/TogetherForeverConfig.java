/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.together_forever.config;


import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class TogetherForeverConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.BooleanValue advancementSync = BUILDER.comment("Enables advancement syncing").define("advancementSync", true);

    public static ForgeConfigSpec.BooleanValue gamestagesSync = BUILDER.comment("Enables gamestages syncing").define("gamestagesSync", true);
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> backlistedGamestages = BUILDER.comment("How delayed ").defineListAllowEmpty("backlistedGamestages", new ArrayList<>(), o -> true);

    public static ForgeConfigSpec.ConfigValue<Integer> syncDataSecondsDelay = BUILDER.comment("How delayed the syncing is done after logging in").define("syncDataSecondsDelay", 20);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}
