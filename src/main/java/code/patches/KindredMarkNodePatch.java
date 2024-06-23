package code.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static code.ModFile.makeImagePath;
import basemod.ReflectionHacks;

@SpirePatch(clz = MapRoomNode.class, method = "render", paramtypez = SpriteBatch.class)
public class KindredMarkNodePatch {

    private static final Texture MARK_OVERLAY = ImageMaster.loadImage(makeImagePath("map/kindredmark.png"));
    private static final Map<MapRoomNode, Boolean> isMarked = new HashMap<>();
    private static boolean roomsProcessed = false;
    private static final int NUM_MARKED_NODES = 7;
    private static final int MIN_DISTANCE = 3; // Minimum distance between marked nodes

    @SpirePrefixPatch
    public static void mapRenderingPatch(MapRoomNode __instance, SpriteBatch sb) {
        if (!roomsProcessed) {
            processRooms();
            roomsProcessed = true;
        }

        float offsetX = (float)ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
        float offsetY = (float)ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
        float spacingX = (float)ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");
        float scale = (float)ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");

        if (__instance.room instanceof MonsterRoom || __instance.room instanceof MonsterRoomElite) {
            if (!isMarked.getOrDefault(__instance, false)) return;

            if (MARK_OVERLAY != null) {
                Color originalColor = sb.getColor();
                sb.setColor(1, 1, 1, 0.50f); // Set opacity to 75%

                sb.setBlendFunction(770, 1); // Set the blend function for additive blending

                // Draw the mark overlay
                sb.draw(MARK_OVERLAY,
                        __instance.x * spacingX + offsetX - 64.0f + __instance.offsetX,
                        __instance.y * Settings.MAP_DST_Y + offsetY + DungeonMapScreen.offsetY - 64.0f + __instance.offsetY,
                        64.0f,
                        64.0f,
                        128.0f,
                        128.0f,
                        (scale * 0.65f + 0.2f) * Settings.scale,
                        (scale * 0.65f + 0.2f) * Settings.scale,
                        0,
                        0,
                        0,
                        128,
                        128,
                        false,
                        false);

                sb.setBlendFunction(770, 771); // Reset the blend function to normal
                sb.setColor(originalColor); // Reset color to original
            }
        }
    }

    private static void processRooms() {
        List<MapRoomNode> eligibleNodes = new ArrayList<>();

        // Collect eligible nodes (elites and monsters)
        for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
            for (MapRoomNode node : row) {
                if (node.room instanceof MonsterRoom || node.room instanceof MonsterRoomElite) {
                    eligibleNodes.add(node);
                }
            }
        }

        if (eligibleNodes.size() < NUM_MARKED_NODES) {
            throw new RuntimeException("Not enough eligible nodes to mark.");
        }

        // Ensure the selected nodes are far enough apart
        List<MapRoomNode> markedNodes = new ArrayList<>();
        int currentMinDistance = MIN_DISTANCE;
        while (markedNodes.size() < NUM_MARKED_NODES) {
            markedNodes.clear();
            Collections.shuffle(eligibleNodes, AbstractDungeon.mapRng.random);
            for (MapRoomNode node : eligibleNodes) {
                if (markedNodes.size() >= NUM_MARKED_NODES) break;
                boolean tooClose = false;
                for (MapRoomNode markedNode : markedNodes) {
                    if (distance(node, markedNode) < currentMinDistance) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose && node.y != 0) {
                    markedNodes.add(node);
                }
            }
            currentMinDistance--; // Reduce the minimum distance if not enough nodes are marked
            if (currentMinDistance < 0) break; // Ensure we don't go into negative distance
        }

        // Mark the selected nodes
        for (MapRoomNode node : markedNodes) {
            isMarked.put(node, true);
        }

        System.out.println("Processed rooms for marking: " + markedNodes.size() + " nodes marked.");
        for (MapRoomNode node : markedNodes) {
            System.out.println("Node at (" + node.x + ", " + node.y + ") marked.");
        }
    }

    private static int distance(MapRoomNode node1, MapRoomNode node2) {
        int dx = node1.x - node2.x;
        int dy = node1.y - node2.y;
        return Math.abs(dx) + Math.abs(dy);
    }

    public static void resetRoomsProcessed() {
        roomsProcessed = false;
        isMarked.clear();
    }

    public static boolean isMarkedRoom(MapRoomNode node) {
        return isMarked.getOrDefault(node, false);
    }
}