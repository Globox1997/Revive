package net.revive.accessor;

public interface PlayerEntityAccessor {

    public void setDeathReason(boolean outOfWorld);

    public boolean getDeathReason();

    public void setCanRevive(boolean canRevive);

    public boolean canRevive();
}
