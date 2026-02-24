package io.github.zhuiye486.events;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.map.MapEdge;

public class ChooseHerosEventsTrigger {
    private static String targetEventName = null;

    public static void triggerChooseHerosEvent() {
        AbstractDungeon.effectList.add(new AbstractGameEffect() {  // 天才!!
            @Override
            public void update() {
                this.isDone = true;

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
                    this.event = new ChooseHeros();
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
