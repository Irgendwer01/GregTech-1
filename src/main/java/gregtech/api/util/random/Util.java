/*
 * DSI utilities
 *
 * Copyright (C) 2002-2023 Sebastiano Vigna
 *
 * This program and the accompanying materials are made available under the
 * terms of the GNU Lesser General Public License v2.1 or later,
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html,
 * or the Apache Software License 2.0, which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later OR Apache-2.0
 */
package gregtech.api.util.random;

/**
 * All-purpose static-method container class.
 *
 * @author Sebastiano Vigna
 * @since 0.1
 */

final class Util {

    private Util() {}

    private static final XoRoShiRo128PlusRandom seedUniquifier = new XoRoShiRo128PlusRandom(System.nanoTime());

    /**
     * Returns a random seed generated by taking the output of a {@link XoRoShiRo128PlusRandom}
     * (seeded at startup with {@link System#nanoTime()}) and xoring it with {@link System#nanoTime()}.
     *
     * @return a reasonably good random seed.
     */
    public static long randomSeed() {
        final long x;
        synchronized (seedUniquifier) {
            x = seedUniquifier.nextLong();
        }
        return x ^ System.nanoTime();
    }
}
