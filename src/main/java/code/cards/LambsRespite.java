package code.cards;

import code.powers.LambsRespitePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.ModFile.makeID;

public class LambsRespite extends AbstractEasyCard {
    public final static String ID = makeID("LambsRespite");
    // intellij stuff skill, self, basic, , ,  5, 3, ,

    public LambsRespite() {
        super(ID, 2, CardType.SKILL, CardRarity.BASIC, CardTarget.ALL);
        baseMagicNumber = 12;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters)
            addToBot(new ApplyPowerAction(monster, p, new LambsRespitePower(monster, this.magicNumber)));
        addToBot(new ApplyPowerAction(p, p, new LambsRespitePower(p, this.magicNumber)));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(8);
    }
}