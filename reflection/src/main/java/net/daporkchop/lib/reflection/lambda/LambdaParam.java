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

package net.daporkchop.lib.reflection.lambda;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@Getter
public class LambdaParam {
    @NonNull
    protected Class<?> type;

    protected boolean targetGeneric;
    protected boolean interfaceGeneric;

    public boolean isGeneric()  {
        return this.targetGeneric || this.interfaceGeneric;
    }

    @RequiredArgsConstructor
    @Accessors(chain = true)
    public static class Builder<T> {
        @NonNull
        protected final LambdaBuilder<T> lambdaBuilder;
        @NonNull
        protected final Consumer<LambdaParam> completionHandler;
        
        protected final LambdaParam param = new LambdaParam();

        public Builder<T> setType(@NonNull Class<?> type)  {
            this.param.setType(type);
            return this;
        }

        public Builder<T> setTargetGeneric(boolean targetGeneric)  {
            this.param.setTargetGeneric(targetGeneric);
            return this;
        }

        public Builder<T> setInterfaceGeneric(boolean interfaceGeneric)  {
            this.param.setInterfaceGeneric(interfaceGeneric);
            return this;
        }

        public LambdaBuilder<T> build() {
            if (this.param.getType() == null)   {
                throw new IllegalStateException("type not set!");
            } else {
                this.completionHandler.accept(this.param);
                return this.lambdaBuilder;
            }
        }
    }
}
