package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.window.WindowContainerToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(TaskInfo.class)
public class TaskInfoHidden {

    /**
     * The id of the user the task was running as if this is a leaf task. The id of the current
     * running user of the system otherwise.
     */
    public int userId;

    /**
     * The identifier for this task.
     */
    public int taskId;

    /**
     * Whether or not this task has any running activities.
     */
    public boolean isRunning;

    /**
     * The base intent of the task (generally the intent that launched the task). This intent can
     * be used to relaunch the task (if it is no longer running) or brought to the front if it is.
     */
    @NonNull
    public Intent baseIntent;

    /**
     * The component of the first activity in the task, can be considered the "application" of this
     * task.
     */
    @Nullable
    public ComponentName baseActivity;

    /**
     * The component of the top activity in the task, currently showing to the user.
     */
    @Nullable
    public ComponentName topActivity;

    /**
     * The component of the target activity if this task was started from an activity alias.
     * Otherwise, this is null.
     */
    @Nullable
    public ComponentName origActivity;

    /**
     * The component of the activity that started this task (may be the component of the activity
     * alias).
     */
    @Nullable
    public ComponentName realActivity;

    /**
     * The number of activities in this task (including running).
     */
    public int numActivities;

    /**
     * The last time this task was active since boot (including time spent in sleep).
     */
    public long lastActiveTime;

    /**
     * The id of the display this task is associated with.
     */
    public int displayId;

    /**
     * The recent activity values for the highest activity in the stack to have set the values.
     * {@link Activity#setTaskDescription(android.app.ActivityManager.TaskDescription)}.
     */
    @Nullable
    public ActivityManager.TaskDescription taskDescription;

    /**
     * Whether this task supports multi windowing modes based on the device settings and the
     * root activity resizability and configuration.
     */
    public boolean supportsMultiWindow;

    /**
     * Used as an opaque identifier for this task.
     */
    @NonNull
    public WindowContainerToken token;

    /**
     * The PictureInPictureParams for the Task, if set.
     */
    @Nullable
    public PictureInPictureParams pictureInPictureParams;

    /**
     */
    public boolean shouldDockBigOverlays;

    /**
     * The task id of the host Task of the launch-into-pip Activity, i.e., it points to the Task
     * the launch-into-pip Activity is originated from.
     */
    public int launchIntoPipHostTaskId;

    /**
     * The {@link Rect} copied from {@link DisplayCutout#getSafeInsets()} if the cutout is not of
     * (LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS),
     * {@code null} otherwise.
     */
    @Nullable
    public Rect displayCutoutInsets;

    /**
     * The {@link ActivityInfo} of the top activity in this task.
     */
    @Nullable
    public ActivityInfo topActivityInfo;

    /**
     * Whether the direct top activity is in size compat mode on foreground.
     */
    public boolean topActivityInSizeCompat;

    /**
     * Whether the direct top activity is eligible for letterbox education.
     */
    public boolean topActivityEligibleForLetterboxEducation;

    /**
     * Whether this task is resizable. Unlike {@link #resizeMode} (which is what the top activity
     * supports), this is what the system actually uses for resizability based on other policy and
     * developer options.
     */
    public boolean isResizeable;

    /**
     * Minimal width of the task when it's resizeable.
     */
    public int minWidth;

    /**
     * Minimal height of the task when it's resizeable.
     */
    public int minHeight;

    /**
     * The default minimal size of the task used when a minWidth or minHeight is not specified.
     */
    public int defaultMinSize;

    /**
     * Relative position of the task's top left corner in the parent container.
     */
    public Point positionInParent;

    /**
     * The launch cookies associated with activities in this task if any.
     * @see ActivityOptions#setLaunchCookie(IBinder)
     */
    public ArrayList<IBinder> launchCookies = new ArrayList<>();

    /**
     * The identifier of the parent task that is created by organizer, otherwise
     * {@link ActivityTaskManager#INVALID_TASK_ID}.
     */
    public int parentTaskId;

    /**
     * Whether this task is focused.
     */
    public boolean isFocused;

    /**
     * Whether this task is visible.
     */
    public boolean isVisible;

    /**
     * Whether this task is sleeping due to sleeping display.
     */
    public boolean isSleeping;

}
