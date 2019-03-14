/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.setting;

import lombok.Builder;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PConstants;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@Builder
@SuppressWarnings("unchecked")
public class Settings {
    @NonNull
    protected final Map<Option, Object> backingMap;

    protected boolean validated = false;

    public <T> T getAndSet(@NonNull Option<T> option, @NonNull T value)   {
        if (this.validated) {
            throw new IllegalStateException("Settings have already been validated!");
        } else {
            value = (T) this.backingMap.replace(option, value);
            if (value == null) {
                throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
            } else {
                return value;
            }
        }
    }

    public <T> Settings set(@NonNull Option<T> option, @NonNull T value)   {
        if (this.validated) {
            throw new IllegalStateException("Settings have already been validated!");
        } else {
            value = (T) this.backingMap.replace(option, value);
            if (value == null) {
                throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
            } else {
                return this;
            }
        }
    }

    public Settings copy(@NonNull Settings other)    {
        if (this.validated) {
            throw new IllegalStateException("Settings have already been validated!");
        } else {
            other.backingMap.forEach(this::set);
            return this;
        }
    }

    public <T> T get(@NonNull Option<T> option) {
        T value = (T) this.backingMap.get(option);
        if (value == null)  {
            throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
        } else {
            return value == Option.UNSET_VALUE ? null : value;
        }
    }

    public <T> T getOrDefault(@NonNull Option<T> option, @NonNull Option<T> fallback) {
        T value = (T) this.backingMap.get(option);
        if (value == null)  {
            throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
        } else {
            return value == Option.UNSET_VALUE ? this.get(fallback) : value;
        }
    }

    public <T> T getOrCompute(@NonNull Option<T> option, @NonNull Supplier<T> fallback) {
        T value = (T) this.backingMap.get(option);
        if (value == null)  {
            throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
        } else {
            return value == Option.UNSET_VALUE ? fallback.get() : value;
        }
    }

    public <T> T getOrDefault(@NonNull Option<T> option, @NonNull T fallback) {
        T value = (T) this.backingMap.get(option);
        if (value == null)  {
            throw new IllegalStateException(String.format("Unknown option: \"%s\"", option));
        } else {
            return value == Option.UNSET_VALUE ? fallback : value;
        }
    }

    public Settings validate()  {
        this.backingMap.forEach((option, value) -> {
            if (value == Option.UNSET_VALUE && option.isRequired())   {
                throw new IllegalStateException(String.format("Value \"%s\" is not set, but required!", option));
            }
        });
        this.validated = true;
        return this;
    }

    public Settings validateMatches(@NonNull OptionGroup options)  {
        options.options.forEach(option -> {
            if (!this.backingMap.containsKey(option))   {
                throw new IllegalStateException(String.format("Value \"%s\" is not contained, but required!", option));
            }
        });
        return this.validate();
    }

    public boolean contains(@NonNull Option option) {
        return this.backingMap.containsKey(option);
    }

    public static class SettingsBuilder {
        private Map<Option, Object> backingMap;

        public SettingsBuilder backingMap(@NonNull Map<Option, Object> backingMap) {
            if (!backingMap.isEmpty()) {
                throw new IllegalArgumentException("Backing map must be empty!");
            } else if (this.backingMap != null && !this.backingMap.isEmpty()) {
                backingMap.putAll(this.backingMap);
            }
            this.backingMap = backingMap;
            return this;
        }

        public SettingsBuilder option(@NonNull Option option) {
            if (this.backingMap == null) {
                this.backingMap = this.createMap();
            } else if (this.backingMap.containsKey(option)) {
                throw new IllegalArgumentException(String.format("Option \"%s\" already set!", option));
            }
            this.backingMap.put(option, option.getDefaultValue());
            return this;
        }

        public SettingsBuilder options(Option... options) {
            return this.options(OptionGroup.of(options));
        }

        public SettingsBuilder options(@NonNull OptionGroup options) {
            if (this.backingMap == null) {
                this.backingMap = this.createMap();
            } else if (!options.isCompatible(this.backingMap.keySet())) {
                throw new IllegalArgumentException("Options already set!");
            }
            options.addTo(this.backingMap);
            return this;
        }

        protected Map<Option, Object> createMap()   {
            return new IdentityHashMap<>(); //TODO: different map implementation? this doesn't need to be mutable after the builder is done
        }
    }
}
