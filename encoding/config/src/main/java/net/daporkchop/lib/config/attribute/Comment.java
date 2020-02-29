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

package net.daporkchop.lib.config.attribute;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.config.Config;

import java.util.Arrays;
import java.util.Objects;

/**
 * Contains the text in a comment.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment {
    /**
     * Parses a comment from it's String[] form (as set in {@link Config.Comment#value()}
     *
     * @param lines the comment's content
     * @return a new instance of {@link Comment}
     */
    public static Comment from(@NonNull String[] lines) {
        boolean empty = false;
        if (lines.length == 0) {
            empty = true;
        } else {
            for (String s : lines) {
                if (s != null && !s.isEmpty()) {
                    //this won't check for comments whose only content is a newline, but
                    //if people really want a blank line then ok
                    break;
                }
            }
        }
        if (empty) {
            return new Comment(new String[0]);
        }
        return new Comment(Arrays.stream(lines)
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.split("\n")))
                .toArray(String[]::new));
    }

    @NonNull
    protected final String[] commentLines;

    /**
     * Checks if the comment is present (i.e. whether or not the comment has any text in it, and therefore
     * whether or not the comment will be displayed)
     */
    public boolean isPresent() {
        return this.commentLines.length > 0;
    }
}
