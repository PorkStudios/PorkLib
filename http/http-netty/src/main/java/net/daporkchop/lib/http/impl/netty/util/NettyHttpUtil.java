/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.http.impl.netty.util;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.request.query.QueryImpl;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.URLEncoding;
import net.daporkchop.lib.http.util.exception.GenericHttpException;
import net.daporkchop.lib.http.util.exception.HttpException;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class NettyHttpUtil {
    protected final long MATCHER_FIRST_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "first");
    protected final long MATCHER_GROUPS_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "groups");
    protected final long MATCHER_PARENTPATTERN_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "parentPattern");
    protected final long MATCHER_TEXT_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "text");
    protected final long PATTERN_GROUPINDICES_OFFSET = PUnsafe.pork_getOffset(Pattern.class, "groupIndices");

    //protected final Pattern _BASE_URL_PATTERN       = Pattern.compile("^([\\/A-Za-z+*\\-._%]+)(?:\\?(?:[A-Za-z+*\\-._%]+(?:=[A-Za-z+*\\-._%]+)?&)*(?:[A-Za-z+*\\-._%]+(?:=[A-Za-z+*\\-._%]+)?))?(?:#[A-Za-z+*\\-._%]*)?$");
    //protected final Pattern _EXTRACT_PARAMS_PATTERN = Pattern.compile("\\?((?:(?>[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)(?:=(?:[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)?(?:&(?1))?)");

    //protected final Pattern _BASE_URL_PATTERN = Pattern.compile("^((?:[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)(\\?(?:(?1)(?:=(?1)+)?&)*(?:(?1)+(?:=(?1)+)?))?(?:#((?1)*))?$");

    //protected final String _URL_CHARS = "[A-Za-z0-9-._~:\\/?#[\\]@!\\$&'\\(\\)\\*\\+,;=]";
    protected final String _URL_CHARS = "[A-Za-z0-9-._~:\\/\\[\\]@!\\$&'\\(\\)\\*\\+,;=]";
    protected final String _PARAMS_CHARS = "[A-Za-z0-9-._~:\\/\\[\\]@!\\$&'\\(\\)\\*\\+,;]";

    //formatting for regex101:
    //^(?<path>((?:[\/A-Za-z0-9+*\-._]|%(?:[a-fA-F0-9]{2})+)+))(?>\?(?<params>(?>(?2)(?>=(?2))?)(?>&(?&params))?)?)?(?>#(?<fragment>(?2)*))?$
    protected final Pattern _URL_PATTERN = Pattern.compile("^(?<path>((?:" + _URL_CHARS + "|%(?:[a-fA-F0-9]{2})+)+))(?>\\?(?<params>(?>(?2)(?>=(?2))?)(?>&(?'params'))?)?)?(?>#(?<fragment>(?2)*))?$");

    protected final Pattern _PARAMS_PATTERN = Pattern.compile("(?>^|&)(?<key>((?>" + _PARAMS_CHARS + "|%(?:[a-fA-F0-9]{2})+)+))(?>=(?<value>(?2)))?");

    //TODO: this still doesn't work correctly...
    public Query parseQuery(@NonNull HttpMethod method, @NonNull CharSequence query) throws HttpException {
        Matcher urlMatcher = _URL_PATTERN.matcher(query);
        if (!urlMatcher.find()) {
            throw StatusCodes.BAD_REQUEST.exception();
        }

        if (false) {
            System.out.print("path=");
            System.out.print(urlMatcher.group("path"));
            if (urlMatcher.group("params") != null) {
                System.out.print(", params=");
                System.out.print(urlMatcher.group("params"));
            }
            if (urlMatcher.group("fragment") != null) {
                System.out.print(", fragment=");
                System.out.print(urlMatcher.group("fragment"));
            }
            System.out.println();
            return null;
        }

        Map<String, String> params = Collections.emptyMap();
        CharSequence rawParams = fastGroup(urlMatcher, "params");
        if (rawParams != null) {
            Matcher paramsMatcher = _PARAMS_PATTERN.matcher(rawParams);
            if (paramsMatcher.find()) {
                params = new HashMap<>();
                do {
                    String key = URLEncoding.decode(fastGroup(paramsMatcher, "key"));
                    CharSequence rawValue = fastGroup(paramsMatcher, "value");
                    if (params.putIfAbsent(key, rawValue == null ? "" : URLEncoding.decode(rawValue)) != null) {
                        throw new GenericHttpException(StatusCodes.BAD_REQUEST, "Duplicate parameter: " + key);
                    }
                } while (paramsMatcher.find());
            }
        }

        CharSequence fragment = fastGroup(urlMatcher, "fragment");

        return new QueryImpl(
                method,
                URLEncoding.decode(fastGroup(urlMatcher, "path")),
                fragment == null ? null : URLEncoding.decode(fragment),
                params
        );
    }

    public CharSequence fastGroup(@NonNull Matcher matcher, int group) {
        if (PUnsafe.getInt(matcher, MATCHER_FIRST_OFFSET) < 0) {
            throw new IllegalStateException("No match found");
        } else if (group < 0 || group > matcher.groupCount()) {
            throw new IndexOutOfBoundsException("No group " + group);
        }
        int[] groups = PUnsafe.getObject(matcher, MATCHER_GROUPS_OFFSET);
        int startIndex = groups[group * 2];
        int endIndex = groups[group * 2 + 1];
        if (startIndex < 0 || endIndex < 0) {
            return null;
        } else {
            return PStrings.subSequence(PUnsafe.getObject(matcher, MATCHER_TEXT_OFFSET), startIndex, endIndex);
        }
    }

    public CharSequence fastGroup(@NonNull Matcher matcher, @NonNull String name) {
        if (PUnsafe.getInt(matcher, MATCHER_FIRST_OFFSET) < 0) {
            throw new IllegalStateException("No match found");
        }
        Map<String, Integer> groupIndices = PUnsafe.getObject(PUnsafe.getObject(matcher, MATCHER_PARENTPATTERN_OFFSET), PATTERN_GROUPINDICES_OFFSET);
        Integer indexObj;
        if (groupIndices == null || (indexObj = groupIndices.get(name)) == null) {
            throw new IllegalArgumentException("No group with name <" + name + '>');
        }
        int group = indexObj;
        int[] groups = PUnsafe.getObject(matcher, MATCHER_GROUPS_OFFSET);
        int startIndex = groups[group * 2];
        int endIndex = groups[group * 2 + 1];
        if (startIndex < 0 || endIndex < 0) {
            return null;
        } else {
            return PStrings.subSequence(PUnsafe.getObject(matcher, MATCHER_TEXT_OFFSET), startIndex, endIndex);
        }
    }
}
