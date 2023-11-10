package android.app;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(TaskInfo.class)
public class TaskInfoHidden {

    /**
     * The identifier for this task.
     */
    public int taskId;

    /**
     * The id of the display this task is associated with.
     */
    public int displayId;

}
