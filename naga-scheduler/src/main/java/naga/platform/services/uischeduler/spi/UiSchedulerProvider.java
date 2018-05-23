package naga.platform.services.uischeduler.spi;

import naga.platform.services.scheduler.Scheduled;
import naga.platform.services.scheduler.spi.SchedulerProvider;
import naga.util.function.Consumer;
import naga.util.tuples.Unit;

import static naga.platform.services.uischeduler.spi.AnimationFramePass.*;

/**
 * @author Bruno Salmon
 */
public interface UiSchedulerProvider extends SchedulerProvider {

    boolean isUiThread();

    default void runInUiThread(Runnable runnable) {
        if (isUiThread())
            runnable.run();
        else
            scheduleDeferred(runnable);
    }

    default void runOutUiThread(Runnable runnable) {
        if (!isUiThread())
            runnable.run();
        else
            runInBackground(runnable);
    }

    default Scheduled schedulePropertyChangeInNextAnimationFrame(Runnable propertyChangeTask) {
        return scheduleInNextAnimationFrame(propertyChangeTask, PROPERTY_CHANGE_PASS);
    }

    default Scheduled scheduleInNextAnimationFrame(Runnable animationTask, AnimationFramePass pass) {
        return scheduleDelayInAnimationFrame(0, animationTask, pass);
    }

    Scheduled scheduleDelayInAnimationFrame(long delayMs, Runnable animationTask, AnimationFramePass pass);

    Scheduled scheduleInFutureAnimationFrame(int frameCount, Runnable animationTask, AnimationFramePass pass);

    default Scheduled scheduleDelayInAnimationFrame(long delayMs, Consumer<Scheduled> animationTask, AnimationFramePass pass) {
        Unit<Scheduled> scheduledHolder = new Unit<>();
        Scheduled scheduled = scheduleDelayInAnimationFrame(delayMs, () -> animationTask.accept(scheduledHolder.get()), pass);
        scheduledHolder.set(scheduled);
        return scheduled;
    }

    default Scheduled schedulePeriodicInAnimationFrame(Runnable animationTask, AnimationFramePass pass) {
        return schedulePeriodicInAnimationFrame(0, animationTask, pass);
    }

    default Scheduled schedulePeriodicInAnimationFrame(Consumer<Scheduled> animationTask, AnimationFramePass pass) {
        Unit<Scheduled> scheduledHolder = new Unit<>();
        Scheduled scheduled = schedulePeriodicInAnimationFrame(() -> animationTask.accept(scheduledHolder.get()), pass);
        scheduledHolder.set(scheduled);
        return scheduled;
    }

    Scheduled schedulePeriodicInAnimationFrame(long delayMs, Runnable animationTask, AnimationFramePass pass);

    void requestNextScenePulse();

    boolean isAnimationFrameNow();

    // Run immediately but isAnimationFrame() returns true -> the layout pass is executed immediately instead of being
    // postponed to the next animation frame. This is can be useful if a node rendering is needed outside the animation
    // frame (for example when rendering a table cell during a repaint triggered by Swing).
    void runLikeInAnimationFrame(Runnable runnable);

}