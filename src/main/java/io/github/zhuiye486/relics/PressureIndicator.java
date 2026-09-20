package io.github.zhuiye486.relics;

import java.util.Arrays;
import java.util.Iterator;

import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
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

    private boolean cardAddedAt100 = false;
    private boolean noBlockAtPlayerEndTurn = false;

    public PressureIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 0;
        this.cardAddedAt100 = false;
        this.noBlockAtPlayerEndTurn = false;
    }

    @Override
    public void atTurnStart() {
        this.noBlockAtPlayerEndTurn = false;
        removeAfflictionsIfPressureZero();
    }

    @Override
    public void onPlayerEndTurn() {
        this.noBlockAtPlayerEndTurn = AbstractDungeon.player != null
            && AbstractDungeon.player.currentBlock == 0;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        removeAfflictionsIfPressureZero();

        AbstractCreature attacker = info.owner;
        int baseAmount = 7;
        if (attacker instanceof AbstractMonster && ((AbstractMonster) attacker).type == AbstractMonster.EnemyType.BOSS) {
            baseAmount = 10;
        }

        final int pressureBase = baseAmount;
        final boolean guaranteedPressure = noBlockAtPlayerEndTurn;
        final int hpBeforeAttack = AbstractDungeon.player.currentHealth;
        addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                // Check HP after the attack action fully resolves (block/buffer/etc already applied).
                if (AbstractDungeon.player.currentHealth < hpBeforeAttack) {
                    addPressure(pressureBase, guaranteedPressure);
                }
                this.isDone = true;
            }
        });

        return damageAmount;
    }

    private void addPressure(int baseAmount, boolean guaranteed) {
        int pressureAdded = guaranteed
            ? generateGuaranteedNumber(baseAmount)
            : generateRandomNumber(baseAmount);
        changePressure(pressureAdded);
    }

    public void changePressure(int amount) {
        counter = Math.max(0, counter + amount);
        if (counter >= TEST_AMOUNT && !cardAddedAt100) {
            addRandomVirtueOrAffliction();
            cardAddedAt100 = true;
        }
        if (counter >= DIE_AMOUNT) {
            addToTop(new LoseHPAction(AbstractDungeon.player, null, 99999));
        }
        removeAfflictionsIfPressureZero();
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

        // 70% chance to gain a random value in [1x, 2x] of baseNum.
        int minAdded = (int) Math.floor(baseNum * 1.0f);
        int maxAdded = (int) Math.ceil(baseNum * 2.0f);
        return AbstractDungeon.miscRng.random(minAdded, maxAdded);
    }

    private int generateGuaranteedNumber(int baseNum) {
        int minAdded = (int) Math.floor(baseNum * 1.0f);
        int maxAdded = (int) Math.ceil(baseNum * 2.0f);
        return AbstractDungeon.miscRng.random(minAdded, maxAdded);
    }

    private void addRandomVirtueOrAffliction() {
        boolean pickVirtue = AbstractDungeon.miscRng.randomBoolean(VirtueChance);
        if (pickVirtue) {
            String cardId = VIRTUE_CARD_IDS[AbstractDungeon.miscRng.random(VIRTUE_CARD_IDS.length - 1)];
            AbstractCard cardToAdd = CardLibrary.getCopy(cardId);
            if (cardToAdd != null) {
                addToBot(new AddCardToDeckAction(cardToAdd));
            }
        } else {
            if (!hasAfflictionInMasterDeck()) {
                String cardId = AFFLICTION_CARD_IDS[AbstractDungeon.miscRng.random(AFFLICTION_CARD_IDS.length - 1)];
                AbstractCard cardToAdd = CardLibrary.getCopy(cardId);
                if (cardToAdd != null) {
                    addToBot(new AddCardToDeckAction(cardToAdd));
                }
            }
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
        // Reset the one-time trigger flag when pressure returns to 0
        this.cardAddedAt100 = false;
    }
}
