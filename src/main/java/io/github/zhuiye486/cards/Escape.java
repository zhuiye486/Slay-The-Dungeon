package io.github.zhuiye486.cards;

import java.util.Random;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.patches.CardTagsEnum;
import io.github.zhuiye486.powers.LuckyPower;

public class Escape extends AbstractDungeonCard{
    public static final String ID = ModHelper.makePath(Escape.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/Escape.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.NONE;

    public static float chance = 0.5f;

    public Escape() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
        this.tags.add(CardTagsEnum.Lucky);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random random = AbstractDungeon.cardRandomRng.random;
        if (random.nextFloat() < LuckyPower.getLuckyChance(chance)) {   
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                (AbstractDungeon.getCurrRoom()).smoked = true;
                addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new SmokeBombEffect(((AbstractCreature)p).hb.cX, ((AbstractCreature)p).hb.cY)));
                p.hideHealthBar();
                p.isEscaping = true;
                p.flipHorizontal = !p.flipHorizontal;
                AbstractDungeon.overlayMenu.endTurnButton.disable();
                p.escapeTimer = 2.5F;
            }
        } else {
            addToBot(
                new EndTurnAction()
            );
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Escape();
    }
    
}
