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

package net.daporkchop.lib.gui.component.capability;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.type.container.Panel;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.component.type.functional.Label;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface ComponentAdder<Impl> {
    Button button(@NonNull String name);

    default Impl button(@NonNull String name, @NonNull Consumer<Button> initializer)    {
        Button button = this.button(name);
        initializer.accept(button);
        return (Impl) this;
    }
    
    Label label(@NonNull String name);

    default Impl label(@NonNull String name, @NonNull Consumer<Label> initializer)    {
        Label label = this.label(name);
        initializer.accept(label);
        return (Impl) this;
    }

    default Label label(@NonNull String name, @NonNull String text)    {
        return this.label(name).setText(text);
    }

    default Impl label(@NonNull String name, @NonNull String text, @NonNull Consumer<Label> initializer)    {
        Label label = this.label(name).setText(text);
        initializer.accept(label);
        return (Impl) this;
    }

    Panel panel(@NonNull String name);

    default Impl panel(@NonNull String name, @NonNull Consumer<Panel> initializer)    {
        Panel panel = this.panel(name);
        initializer.accept(panel);
        return (Impl) this;
    }

    ScrollPane scrollPane(@NonNull String name);

    default Impl scrollPane(@NonNull String name, @NonNull Consumer<ScrollPane> initializer)    {
        ScrollPane scrollPane = this.scrollPane(name);
        initializer.accept(scrollPane);
        return (Impl) this;
    }
}
