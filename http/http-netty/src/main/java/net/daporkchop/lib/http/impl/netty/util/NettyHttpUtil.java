/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.impl.netty.util;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.request.query.QueryImpl;
import net.daporkchop.lib.http.util.URLEncoding;
import net.daporkchop.lib.http.util.StatusCodes;
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
    protected final long MATCHER_FIRST_OFFSET         = PUnsafe.pork_getOffset(Matcher.class, "first");
    protected final long MATCHER_GROUPS_OFFSET        = PUnsafe.pork_getOffset(Matcher.class, "groups");
    protected final long MATCHER_PARENTPATTERN_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "parentPattern");
    protected final long MATCHER_TEXT_OFFSET          = PUnsafe.pork_getOffset(Matcher.class, "text");
    protected final long PATTERN_GROUPINDICES_OFFSET  = PUnsafe.pork_getOffset(Pattern.class, "groupIndices");

    //protected final Pattern _BASE_URL_PATTERN       = Pattern.compile("^([\\/A-Za-z+*\\-._%]+)(?:\\?(?:[A-Za-z+*\\-._%]+(?:=[A-Za-z+*\\-._%]+)?&)*(?:[A-Za-z+*\\-._%]+(?:=[A-Za-z+*\\-._%]+)?))?(?:#[A-Za-z+*\\-._%]*)?$");
    //protected final Pattern _EXTRACT_PARAMS_PATTERN = Pattern.compile("\\?((?:(?>[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)(?:=(?:[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)?(?:&(?1))?)");

    //protected final Pattern _BASE_URL_PATTERN = Pattern.compile("^((?:[\\/A-Za-z+*\\-._]|%(?:[a-zA-Z0-9]{2})+)+)(\\?(?:(?1)(?:=(?1)+)?&)*(?:(?1)+(?:=(?1)+)?))?(?:#((?1)*))?$");

    //formatting for regex101:
    //^(?<path>((?:[\/A-Za-z0-9+*\-._]|%(?:[a-fA-F0-9]{2})+)+))(?>\?(?<params>(?>(?2)(?>=(?2))?)(?>&(?&params))?)?)?(?>#(?<fragment>(?2)*))?$
    protected final Pattern _URL_PATTERN = Pattern.compile("^(?<path>((?:[\\/A-Za-z0-9+*\\-._]|%(?:[a-fA-F0-9]{2})+)+))(?>\\?(?<params>(?>(?2)(?>=(?2))?)(?>&(?'params'))?)?)?(?>#(?<fragment>(?2)*))?$");

    protected final Pattern _PARAMS_PATTERN = Pattern.compile("(?>^|&)(?<key>((?>[\\/A-Za-z0-9+*\\-._]|%(?:[a-fA-F0-9]{2})+)+))(?>=(?<value>(?2)))?");

    public Query parseQuery(@NonNull HttpMethod method, @NonNull CharSequence query) throws HttpException {
        Matcher urlMatcher = _URL_PATTERN.matcher(query);
        if (!urlMatcher.find()) {
            throw StatusCodes.Bad_Request.exception();
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
                        throw new GenericHttpException(StatusCodes.Bad_Request, "Duplicate parameter: " + key);
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
            return PorkUtil.subSequence(PUnsafe.getObject(matcher, MATCHER_TEXT_OFFSET), startIndex, endIndex);
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
            return PorkUtil.subSequence(PUnsafe.getObject(matcher, MATCHER_TEXT_OFFSET), startIndex, endIndex);
        }
    }
}
