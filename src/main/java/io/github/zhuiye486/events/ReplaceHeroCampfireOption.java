package io.github.zhuiye486.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import io.github.zhuiye486.characters.Heroes;

public class ReplaceHeroCampfireOption extends AbstractCampfireOption {
    private static final int MAIN = 0;
    private static final int SELECT_REPLACEMENT_HERO = 1;
    private static final int SELECT_ROSTER_HERO = 2;
    private static final String ID = "SlayTheDungeon:CampfireHeroSwap";
    private static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static int stage = MAIN;
    private static int selectedIndex = -1;
    private static int replacementHero = -1;
    private static List<Integer> replacementOptions = new ArrayList<>();
    private static RestRoom activeRoom;
    private static RestRoom completedRoom;
    private static final Texture BUTTON_IMAGE = ImageMaster.CAMPFIRE_DIG_BUTTON;

    private final int value;

    private ReplaceHeroCampfireOption(int value, String optionLabel) {
        this.value = value;
        this.label = optionLabel;
        this.description = STRINGS.DESCRIPTIONS[Math.min(stage, STRINGS.DESCRIPTIONS.length - 1)];
        this.img = BUTTON_IMAGE;
    }

    public static void addOptions(ArrayList<AbstractCampfireOption> options) {
        if (Heroes.selectedHeroes == null || Heroes.selectedHeroes.size() != 4) {
            return;
        }
        RestRoom currentRoom = (RestRoom) AbstractDungeon.getCurrRoom();
        if (activeRoom != currentRoom) {
            activeRoom = currentRoom;
            reset();
        }
        if (completedRoom == currentRoom) {
            return;
        }

        if (stage == MAIN) {
            options.add(new ReplaceHeroCampfireOption(-1, STRINGS.OPTIONS[0]));
            return;
        }

        if (stage == SELECT_REPLACEMENT_HERO) {
            replacementOptions = getReplacementOptions();
            for (int hero : replacementOptions) {
                options.add(new ReplaceHeroCampfireOption(hero,
                        STRINGS.OPTIONS[1] + ChooseHeroes.getHeroOption(hero)));
            }
            return;
        }

        if (stage == SELECT_ROSTER_HERO) {
            for (int index = 0; index < Heroes.selectedHeroes.size(); index++) {
                int hero = Heroes.selectedHeroes.get(index);
                options.add(new ReplaceHeroCampfireOption(index,
                        STRINGS.OPTIONS[2] + ChooseHeroes.getHeroOption(hero)));
            }
        }
    }

    @Override
    public void useOption() {
        boolean replacementCompleted = false;
        if (stage == MAIN) {
            stage = SELECT_REPLACEMENT_HERO;
        } else if (stage == SELECT_REPLACEMENT_HERO) {
            replacementHero = value;
            stage = SELECT_ROSTER_HERO;
        } else if (stage == SELECT_ROSTER_HERO) {
            selectedIndex = value;
            Heroes.replaceSelectedHero(selectedIndex, replacementHero);
            completedRoom = activeRoom;
            reset();
            replacementCompleted = true;
        }
        scheduleCampfireRefresh();
    }

    private static List<Integer> getReplacementOptions() {
        ArrayList<Integer> available = new ArrayList<>();
        for (int hero = 1; hero <= 18; hero++) {
            if (!Heroes.selectedHeroes.contains(hero)) {
                available.add(hero);
            }
        }

        Collections.shuffle(available, new Random(AbstractDungeon.miscRng.randomLong()));
        return new ArrayList<>(available.subList(0, Math.min(4, available.size())));
    }

    private static void reset() {
        stage = MAIN;
        selectedIndex = -1;
        replacementHero = -1;
        replacementOptions.clear();
    }

    private static void scheduleCampfireRefresh() {
        AbstractDungeon.effectList.add(new AbstractGameEffect() {
            {
                this.duration = 0.1f;
            }

            @Override
            public void update() {
                this.duration -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
                if (this.duration > 0.0f) {
                    return;
                }
                RestRoom room = (RestRoom) AbstractDungeon.getCurrRoom();
                room.campfireUI = new CampfireUI();
                this.isDone = true;
            }

            @Override
            public void render(SpriteBatch spriteBatch) {
            }

            @Override
            public void dispose() {
            }
        });
    }

}
