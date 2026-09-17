package io.github.zhuiye486.cards;

import java.util.Random;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import io.github.zhuiye486.characters.Heroes.SlayTheDungeonColorEnum;
import io.github.zhuiye486.helpers.ModHelper;

public class FromBeyond extends AbstractDungeonCard {
    public static final String ID = ModHelper.makePath(FromBeyond.class.getSimpleName());
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = CARD_STRINGS.NAME;
    private static final String IMG_PATH = "SlayTheDungeonResources/img/cards/ItsukiS.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION;
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = SlayTheDungeonColorEnum.DEEP_RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    public FromBeyond() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random random = AbstractDungeon.cardRandomRng.random;
        int number = random.nextInt(18) + 1;
        switch (number) {
            case 1:
                addCardById("SlayTheDungeon:Transform");
                addCardById("SlayTheDungeon:Rake");
                addCardById("SlayTheDungeon:Slam");
                break;
            case 2:
                addCardById("SlayTheDungeon:FesteringVapours");
                addCardById("SlayTheDungeon:FortifyingVapours");
                addCardById("SlayTheDungeon:InvigoratingVapours");
                break;
            case 3:
                addCardById("SlayTheDungeon:SniperShot");
                addCardById("SlayTheDungeon:SnipersMark");
                addCardById("SlayTheDungeon:SuppressingFire");
                break;
            case 4:
                addCardById("SlayTheDungeon:FinishHim");
                addCardById("SlayTheDungeon:MarkForDeath");
                addCardById("SlayTheDungeon:Uppercut");
                break;
            case 5:
                addCardById("SlayTheDungeon:BulwarkOfFaith");
                addCardById("SlayTheDungeon:InspiringCry");
                addCardById("SlayTheDungeon:StunningBlow");
                break;
            case 6:
                addCardById("SlayTheDungeon:RainOfSorrows");
                addCardById("SlayTheDungeon:Reclaim");
                addCardById("SlayTheDungeon:Redeem");
                break;
            case 7:
                addCardById("SlayTheDungeon:ShadowFade");
                addCardById("SlayTheDungeon:PoisonDart");
                addCardById("SlayTheDungeon:ToxinTrickery");
                break;
            case 8:
                addCardById("SlayTheDungeon:IronSawn");
                addCardById("SlayTheDungeon:BleedOut");
                addCardById("SlayTheDungeon:IfItBleeds");
                break;
            case 9:
                addCardById("SlayTheDungeon:DuelistsAdvance");
                addCardById("SlayTheDungeon:OpenVein");
                addCardById("SlayTheDungeon:PointBlankShoot");
                break;
            case 10:
                addCardById("SlayTheDungeon:CryHavoc");
                addCardById("SlayTheDungeon:HoundsHurry");
                addCardById("SlayTheDungeon:HoundsRush");
                break;
            case 11:
                addCardById("SlayTheDungeon:Finale");
                addCardById("SlayTheDungeon:BattleBallad");
                addCardById("SlayTheDungeon:InspiringTune");
                break;
            case 12:
                addCardById("SlayTheDungeon:Intimidate");
                addCardById("SlayTheDungeon:Revenge");
                addCardById("SlayTheDungeon:Withstand");
                break;
            case 13:
                addCardById("SlayTheDungeon:Bolster");
                addCardById("SlayTheDungeon:Defender");
                addCardById("SlayTheDungeon:Retribution");
                break;
            case 14:
                addCardById("SlayTheDungeon:AimedShot");
                addCardById("SlayTheDungeon:PatchUp");
                addCardById("SlayTheDungeon:SkeetShot");
                break;
            case 15:
                addCardById("SlayTheDungeon:HandsFromTheAbyss");
                addCardById("SlayTheDungeon:WeakeningCurse");
                addCardById("SlayTheDungeon:WyrdReconstruction");
                break;
            case 16:
                addCardById("SlayTheDungeon:PlagueGrenade");
                addCardById("SlayTheDungeon:NoxiousBlast");
                addCardById("SlayTheDungeon:BattlefieldMedicine");
                break;
            case 17:
                addCardById("SlayTheDungeon:Captivate");
                addCardById("SlayTheDungeon:Puncture");
                addCardById("SlayTheDungeon:SerpentSway");
                break;
            case 18:
                addCardById("SlayTheDungeon:DazzlingLight");
                addCardById("SlayTheDungeon:DivineGrace");
                addCardById("SlayTheDungeon:DivineComfort");
                break;
            default:
                break;
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    private void addCardById(String cardId) {
        AbstractCard card = CardLibrary.getCopy(cardId);
        if (card != null) {
            card.setCostForTurn(0);
            card.isCostModified = true;
            addToBot(new MakeTempCardInDiscardAction(card, 1));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FromBeyond();
    }
    
}
