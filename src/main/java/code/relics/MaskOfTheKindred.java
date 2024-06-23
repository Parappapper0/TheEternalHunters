package code.relics;

import code.CharacterFile;

import static code.ModFile.makeID;

public class MaskOfTheKindred extends AbstractEasyRelic {
    public static final String ID = makeID("MaskOfTheKindred");
    public static int MarksCollected = 0;
    public MaskOfTheKindred() {
        super(ID, RelicTier.STARTER, LandingSound.FLAT, CharacterFile.Enums.KINDRED_COLOR);
    }
}
