package buildcraft.api.transport;

import buildcraft.api.transport.neptune.IPipeHolder;

public abstract class PipeEvent {
    public final boolean canBeCancelled;
    public final IPipeHolder holder;
    private boolean canceled = false;

    public PipeEvent(IPipeHolder holder) {
        this(false, holder);
    }

    protected PipeEvent(boolean canBeCancelled, IPipeHolder holder) {
        this.canBeCancelled = canBeCancelled;
        this.holder = holder;
    }

    public void cancel() {
        if (canBeCancelled) {
            canceled = true;
        }
    }

    public boolean isCanceled() {
        return canceled;
    }
}
