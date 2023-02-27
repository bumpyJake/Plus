package com.marcusslover.plus.lib.events;

import com.marcusslover.plus.lib.lifecycle.ILifeCycle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public class EventReference<T extends Event> implements ILifeCycle<EventReference<T>> {

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.NONE)
    private @Nullable EventListener listener = null;

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC)
    private @NotNull EventPriority priority = EventPriority.NORMAL;

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC)
    private @Nullable Consumer<T> handler = null;

    private @Nullable Plugin plugin;

    private EventReference(@NotNull Class<T> base) {
    }

    @Override
    public void unregister() {
        if (this.plugin == null || this.listener == null) {
            return;
        }

        var handle = EventHandler.get(this.plugin);

        handle.unsubscribe(this.listener);
    }

    /**
     * Register the event. This must be the final method called similarly to build() methods.
     *
     * @return the event reference
     */
    public EventReference<T> bind(@NotNull Plugin plugin) {
        if (this.plugin != null || this.listener != null) {
            throw new IllegalStateException("Event has already been bound.");
        }

        this.plugin = plugin;

        var handle = EventHandler.get(plugin);

        this.listener = handle.subscribe(this);

        return this;
    }

    public static <T extends Event> EventReference<T> of(@NotNull Class<T> base) {
        return new EventReference<>(base);
    }
}