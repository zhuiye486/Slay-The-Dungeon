package io.github.zhuiye486.events;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.map.MapEdge;

public class ChooseHeroesEventsTrigger {
    private static String targetEventName = null;

    public static void triggerChooseHeroesEvent() {
        AbstractDungeon.effectList.add(new AbstractGameEffect() {  // 天才!!
            @Override
            public void update() {
                this.isDone = true;

                removeSpeechBubbles();
                boolean isTargetEventExists = checkEventExists(targetEventName);
                RoomEventDialog.optionList.clear();

                MapRoomNode currNode = AbstractDungeon.getCurrMapNode();
                MapRoomNode newNode = new MapRoomNode(currNode.x, currNode.y);

                newNode.setRoom(createEventRoom(isTargetEventExists));
                copyMapEdges(currNode, newNode);
                switchToNewRoom(newNode);
            }

            @Override
            public void render(SpriteBatch spriteBatch) {

            }

            @Override
            public void dispose() {

            }
        });
    }

    // 移除原生涅奥的对话气泡（含本帧原版逻辑新加入的），避免遮挡第一个选项
    public static void removeSpeechBubbles() {
        // 只标记 isDone，由游戏在遍历时自行移除，避免 ConcurrentModificationException
        markDone(AbstractDungeon.effectList);
        markDone(AbstractDungeon.topLevelEffects);
    }

    private static void markDone(java.util.List<AbstractGameEffect> effects) {
        for (AbstractGameEffect f : effects) {
            if (f instanceof SpeechBubble || f instanceof InfiniteSpeechBubble) {
                f.isDone = true;
            }
        }
    }

    private static boolean checkEventExists(String eventName) {
        if (eventName == null) {
            return false;
        }
        try {
            AbstractEvent event = EventHelper.getEvent(eventName);
            return event != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static AbstractRoom createEventRoom(boolean isTargetEventExists) {
        if (isTargetEventExists) {
            return new SDAbstractEventRoom();
        } else {
            return new SDAbstractEventRoom() {
                @Override
                public void onPlayerEntry() {
                    AbstractDungeon.overlayMenu.proceedButton.hide();
                    this.event = new ChooseHeroes();
                    this.event.onEnterRoom();
                }
            };
        }
    }

    private static void copyMapEdges(MapRoomNode sourceNode, MapRoomNode targetNode) {
        for (MapEdge edge : sourceNode.getEdges()) {
            targetNode.addEdge(edge);
        }
    }

    private static void switchToNewRoom(MapRoomNode newNode) {
        AbstractDungeon.nextRoom = newNode;
        AbstractDungeon.setCurrMapNode(newNode);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.scene.nextRoom(newNode.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
    }
}
