package code.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
public class KindredMapGenerationPatch {

    @SpirePrefixPatch
    public static void resetRoomsProcessed() {
        KindredMarkNodePatch.resetRoomsProcessed();
        System.out.println("Rooms processed flag reset for new map generation.");
    }
}