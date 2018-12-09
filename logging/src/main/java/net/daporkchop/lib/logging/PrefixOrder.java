/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.logging;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Used for sorting the prefixes on log messages.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum PrefixOrder {
    DATE_LEVEL((dateFormat, level, levelFormat, message) -> {
        if (dateFormat == null && levelFormat == null)  {
            return String.format("%s\n", message);
        } else if (dateFormat == null)  {
            return String.format("%s%s\n", String.format(levelFormat, level.name()), message);
        } else if (levelFormat == null) {
            return String.format("%s%s\n", dateFormat.format(Date.from(Instant.now())), message);
        } else {
            return String.format("%s%s%s\n", dateFormat.format(Date.from(Instant.now())), String.format(levelFormat, level.name()), message);
        }
    }),
    LEVEL_DATE((dateFormat, level, levelFormat, message) -> {
        if (dateFormat == null && levelFormat == null)  {
            return String.format("%s\n", message);
        } else if (dateFormat == null)  {
            return String.format("%s%s\n", String.format(levelFormat, level.name()), message);
        } else if (levelFormat == null) {
            return String.format("%s%s\n", dateFormat.format(Date.from(Instant.now())), message);
        } else {
            return String.format("%s%s%s\n", String.format(levelFormat, level.name()), dateFormat.format(Date.from(Instant.now())), message);
        }
    }),
    LEVEL((dateFormat, level, levelFormat, message) -> levelFormat == null ? String.format("%s\n", message) : String.format("%s%s\n", String.format(levelFormat, level.name()), message)),
    DATE((dateFormat, level, levelFormat, message) -> dateFormat == null ? String.format("%s\n", message) : String.format("%s%s\n", dateFormat.format(Date.from(Instant.now())), message)),
    NO_PREFIX((dateFormat, level, levelFormat, message) -> String.format("%s\n", message));
    ;

    @NonNull
    private final MessagePrefixer prefixer;

    interface MessagePrefixer   {
        String prefix(DateFormat dateFormat, @NonNull LogLevel level, String levelFormat, @NonNull String message);
    }
}
