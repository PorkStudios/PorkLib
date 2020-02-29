/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
