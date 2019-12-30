import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(
        author="RonMan",
        description="A Cheeky Script to Avoid 72 hours of mindless repetition",
        category = Category.AGILITY,
        version = 1.24,
        name = "Cheeky Agility"
)

public class Main extends AbstractScript {

    static class Obstacle {

        Area area;
        int id;
        boolean isClicked;
        boolean done;
        String name;
        String action;

        Obstacle(int x1, int y1, int x2, int y2, int z, String rsName, String rsAction, int objectId) {
            area = new Area(x1, y1, x2, y2, z);
            id = objectId;
            isClicked = false;
            done = false;
            name = rsName;
            action = rsAction;
        }

        public void setData(int x1, int y1, int x2, int y2, int z, String rsName, String rsAction, int objectId) {
            area = new Area(x1, y1, x2, y2, z);
            id = objectId;
            isClicked = false;
            done = false;
            name = rsName;
            action = rsAction;
        }
    }

    // Varrock
    //Obstacle[] agilityCourse2 = new Obstacle[]{
    //    new Obstacle(3128,3430, 3245, 3388, 0, "Rough wall", "Climb", 14412),
    //    new Obstacle(3214,3419, 3219, 3412, 3, "Clothes line", "Cross", 14413),
    //    new Obstacle(3201,3417, 3208, 3413, 3, "Gap", "Leap", 14414),
    //    new Obstacle(3193,3416, 3197, 3416, 1, "Wall", "Balance",14832),
    //    new Obstacle(3192,3406, 3198, 3402, 3, "Gap", "Leap",14833),
    //    new Obstacle(3182,3403, 3208, 3382, 3, "Gap", "Leap",14834),
    //    new Obstacle(3218,3403, 3232, 3392, 3, "Gap", "Leap",14835),
    //    new Obstacle(3236,3408, 3240, 3403, 3, "Ledge", "Hurdle",14836),
    //    new Obstacle(3236,3415, 3240, 3410, 3, "Edge", "Jump-off", 14841),
    //};

    // Falador
    Obstacle[] agilityCourse = new Obstacle[]{
        new Obstacle(3007, 3368, 3053, 3332, 0, "Rough wall", "Climb", 14898),
        new Obstacle(3036, 3343, 3040, 3342, 3, "Tightrope", "Cross", 14899),
        new Obstacle(3044, 3349, 3051, 3341, 3, "Hand holds", "Cross", 14901),
        new Obstacle(3048, 3358, 3050, 3357, 3, "Gap", "Jump", 14903),
        new Obstacle(3045, 3367, 3048, 3361, 3, "Gap", "Jump", 14904),
        new Obstacle(3034, 3364, 3041, 3361, 3, "Tightrope", "Cross", 14905),
        new Obstacle(3026, 3354, 3029, 3352, 3, "Tightrope", "Cross", 14911),
        new Obstacle(3009, 3358, 3021, 3353, 3, "Gap", "Jump", 14919),
        new Obstacle(3016, 3349, 3022, 3343, 3, "Ledge", "Jump", 14920),
        new Obstacle(3011, 3346, 3014, 3344, 3, "Ledge", "Jump", 14921),
        new Obstacle(3009, 3342, 3013, 3335, 3, "Ledge", "Jump", 14923),
        new Obstacle(3012, 3334, 3017, 3331, 3, "Ledge", "Jump", 14924),
        new Obstacle(3019, 3335, 3024, 3332, 3, "Ledge", "Jump", 14925),
    };

    int currentIndex = -1;
    int cyclesStationary = 0;
    //int currentExp = getSkills().getExperience(Skill.AGILITY);

    public int getCurrentIndex() {
        int returnIndex = -1;
        for (int i = 0; i < agilityCourse.length; i++) {
            if (agilityCourse[i].area.contains(getLocalPlayer())) {
                returnIndex = i;
                log("course index " + returnIndex + " next obstacle is " + agilityCourse[returnIndex].name);
                break;
            }
        }

        return returnIndex;
    }

    public int randBetween(int lower, int upper) {
        return (int) (Math.random() * (upper - lower)) + lower;
    }

    public int getNthIndex(int i) {
        return (currentIndex + i) % agilityCourse.length;
    }

    public GameObject getNextObstacle(int n) {
        return getGameObjects().closest(gameObject -> gameObject != null && gameObject.getID() == agilityCourse[getNthIndex(n)].id  && gameObject.hasAction(agilityCourse[getNthIndex(n)].action));
    }

    public void doNextObstacle() {

        if (agilityCourse[currentIndex].area.contains(getLocalPlayer())) {

            GameObject nextObstacle = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getID() == agilityCourse[currentIndex].id  && gameObject.hasAction(agilityCourse[currentIndex].action));
            getCamera().keyboardRotateToEntity(nextObstacle);

            if (nextObstacle.isOnScreen()) {
                log("obstacle " + agilityCourse[currentIndex].name + " on screen");
                if (getLocalPlayer().getAnimation() == -1) {
                    if (agilityCourse[currentIndex].isClicked) {
                        if (!getLocalPlayer().isMoving()) {
                            log("resetting isClicked and getting closer");
                            agilityCourse[currentIndex].isClicked = false;
                            getWalking().walk(nextObstacle.getTile());
                        }
                    } else {
                        log("doing a thing!");
                        if (nextObstacle.interact(agilityCourse[currentIndex].action)) {
                            agilityCourse[currentIndex].isClicked = true;
                            sleepUntil(() -> agilityCourse[getNthIndex(1)].area.contains(getLocalPlayer()), randBetween(6000, 9000));
                            agilityCourse[currentIndex].isClicked = false;
                        } else {
                            getWalking().walk(nextObstacle.getTile());
                        }
                    }
                }
            } else {
                if (!getLocalPlayer().isMoving()) {
                    log("going to next obstacle");
                    getWalking().walk(nextObstacle.getTile());
                } else {
                    log("player is still moving");
                }
            }
        }
    }

    @Override
    public void onStart() {
        log("let's get ready to rumble");
        log("current index is " + getCurrentIndex());
    }

    @Override
    public int onLoop(){

        if (getLocalPlayer().isMoving()) {
            cyclesStationary = 0;
        } else {
            cyclesStationary++;
            log("stationary for " + cyclesStationary + " cycles");
        }

        currentIndex = getCurrentIndex();
        if (currentIndex != -1) {
            GroundItem mog = getGroundItems().closest(groundItem -> groundItem != null && groundItem.getID() == 11849);
            if (mog != null) {
                if (agilityCourse[currentIndex].area.contains(mog.getTile())) {
                    log("taking mark of grace");
                    mog.interact("Take");
                    sleepUntil(() -> getLocalPlayer().getTile() == mog.getTile(), randBetween(1500, 3000));
                } else {
                    doNextObstacle();
                }
            } else {
                doNextObstacle();
            }
        }

        return randBetween(300, 500);
    }

    @Override
    public void onExit() {
        log("All Done");
    }

}

