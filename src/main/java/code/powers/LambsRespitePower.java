package code.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static code.ModFile.makeID;

public class LambsRespitePower extends AbstractEasyPower {
    public static String ID = makeID(LambsRespitePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);

    public LambsRespitePower(AbstractCreature owner, int heal) {
        super(ID, powerStrings.NAME, PowerType.BUFF, true, owner, heal);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {

        int minHP = (int)(this.owner.maxHealth * 0.1f);
        if (this.owner.currentHealth - damageAmount >= minHP || damageAmount <= 0)
            return damageAmount;
        this.flash();
        return this.owner.currentHealth - minHP;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            if (AbstractDungeon.player.hasPower(LambsRespitePower.ID))
                addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, LambsRespitePower.ID));
        }
    }

    @Override
    public void onRemove() {
        this.owner.heal(this.amount);
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }
}