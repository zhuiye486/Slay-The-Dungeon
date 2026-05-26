package io.github.zhuiye486.relics;

import java.util.Arrays;
import java.util.Iterator;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;
import io.github.zhuiye486.helpers.ModHelper;

public class PressureIndicator extends CustomRelic{
    public static final String ID = ModHelper.makePath(PressureIndicator.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/ExampleRelic.png";
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public static final int TEST_AMOUNT = 100;
    public static final int DIE_AMOUNT = 200;
    public static float VirtueChance = 0.25f;
    public static float AfflictionChance = 0.75f;
    private static final String[] VIRTUE_CARD_IDS = new String[] {
        ModHelper.makePath("Courageous"),
        ModHelper.makePath("Focused"),
        ModHelper.makePath("Powerful"),
        ModHelper.makePath("Stalwart"),
        ModHelper.makePath("Vigorous")
    };
    private static final String[] AFFLICTION_CARD_IDS = new String[] {
        ModHelper.makePath("Abusive"),
        ModHelper.makePath("Fearful"),
        ModHelper.makePath("Hopeless"),
        ModHelper.makePath("Irrational"),
        ModHelper.makePath("Masochistic"),
        ModHelper.makePath("Paranoid"),
        ModHelper.makePath("Selfish")
    };

    public PressureIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        removeAfflictionsIfPressureZero();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        removeAfflictionsIfPressureZero();

        AbstractCreature attacker = info.owner;
        int baseAmount = 10;
        if (attacker instanceof AbstractMonster && ((AbstractMonster) attacker).type == AbstractMonster.EnemyType.BOSS) {
            baseAmount = 20;
        }

        final int pressureBase = baseAmount;
        final int hpBeforeAttack = AbstractDungeon.player.currentHealth;
        addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                // Check HP after the attack action fully resolves (block/buffer/etc already applied).
                if (AbstractDungeon.player.currentHealth < hpBeforeAttack) {
                    int pressureAdded = generateRandomNumber(pressureBase);
                    counter += pressureAdded;
                    if (counter >= DIE_AMOUNT) {
                        addToTop(new LoseHPAction(AbstractDungeon.player, null, 99999));
                    } else if (counter >= TEST_AMOUNT && !hasAfflictionInMasterDeck()) {
                        addRandomVirtueOrAffliction();
                    }
                    removeAfflictionsIfPressureZero();
                }
                this.isDone = true;
            }
        });

        return damageAmount;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new PressureIndicator();
    }

    public int generateRandomNumber(int baseNum) {
        // 30% chance to avoid pressure gain entirely.
        if (AbstractDungeon.miscRng.randomBoolean(0.3f)) {
            return 0;
        }

        // 70% chance to gain a random value in [0.5x, 1.5x] of baseNum.
        int minAdded = (int) Math.floor(baseNum * 0.5f);
        int maxAdded = (int) Math.ceil(baseNum * 1.5f);
        return AbstractDungeon.miscRng.random(minAdded, maxAdded);
    }

    private void addRandomVirtueOrAffliction() {
        String[] pool = AbstractDungeon.miscRng.randomBoolean(0.3f) ? VIRTUE_CARD_IDS : AFFLICTION_CARD_IDS;
        String cardId = pool[AbstractDungeon.miscRng.random(pool.length - 1)];
        AbstractCard cardToAdd = CardLibrary.getCopy(cardId);

        if (cardToAdd != null) {
            AbstractDungeon.player.masterDeck.addToTop(cardToAdd);
        }
    }

    private boolean hasAfflictionInMasterDeck() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return false;
        }

        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (Arrays.asList(AFFLICTION_CARD_IDS).contains(card.cardID)) {
                return true;
            }
        }
        return false;
    }

    private void removeAfflictionsIfPressureZero() {
        if (this.counter != 0 || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return;
        }

        Iterator<AbstractCard> iterator = AbstractDungeon.player.masterDeck.group.iterator();
        while (iterator.hasNext()) {
            AbstractCard card = iterator.next();
            if (Arrays.asList(AFFLICTION_CARD_IDS).contains(card.cardID)) {
                iterator.remove();
            }
        }
    }
}
