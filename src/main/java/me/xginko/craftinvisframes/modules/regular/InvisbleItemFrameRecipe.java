package me.xginko.craftinvisframes.modules.regular;

import me.xginko.craftinvisframes.utils.Keys;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public final class InvisbleItemFrameRecipe extends ShapedRecipe {

    private static final RecipeChoice.ExactChoice INVISIBILITY_POTION;

    static {
        List<ItemStack> centerOptions = new ArrayList<>(6);
        for (Material potionMaterial : List.of(Material.LINGERING_POTION, Material.SPLASH_POTION, Material.POTION)) {
            for (PotionType potionType : List.of(PotionType.INVISIBILITY, PotionType.LONG_INVISIBILITY)) {
                ItemStack potion = new ItemStack(potionMaterial, 1);
                potion.editMeta(PotionMeta.class, potionMeta -> potionMeta.setBasePotionType(potionType));
                centerOptions.add(potion);
            }
        }
        INVISIBILITY_POTION = new RecipeChoice.ExactChoice(centerOptions);
    }

    public InvisbleItemFrameRecipe(InvisibleItemFrame invisibleItemFrame) {
        super(Keys.INVISIBLE_ITEM_FRAME.get(), invisibleItemFrame);
        shape("FFF", "FPF", "FFF");
        setIngredient('F', Material.ITEM_FRAME);
        setIngredient('P', INVISIBILITY_POTION);
    }

    public static boolean isInvisFrameRecipe(Recipe recipe) {
        return recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().equals(Keys.INVISIBLE_ITEM_FRAME_RECIPE.get());
    }
}
