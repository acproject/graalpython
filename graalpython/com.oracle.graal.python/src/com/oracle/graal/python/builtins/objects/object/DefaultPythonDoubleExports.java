/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.graal.python.builtins.objects.object;

import com.oracle.graal.python.builtins.PythonBuiltinClassType;
import com.oracle.graal.python.builtins.objects.floats.PFloat;
import com.oracle.graal.python.builtins.objects.ints.PInt;
import com.oracle.graal.python.builtins.objects.object.PythonObjectLibrary.CallContext;
import com.oracle.graal.python.builtins.objects.type.LazyPythonClass;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(value = PythonObjectLibrary.class, receiverType = Double.class)
final class DefaultPythonDoubleExports {
    @ExportMessage
    static boolean isHashable(@SuppressWarnings("unused") Double value) {
        return true;
    }

    @ExportMessage
    static LazyPythonClass getLazyPythonClass(@SuppressWarnings("unused") Double value) {
        return PythonBuiltinClassType.PFloat;
    }

    @ExportMessage
    static class LeftEq {
        private static byte fromBool(boolean x) {
            return (byte) (x ? 1 : 0);
        }

        @Specialization
        static byte eq(Double self, boolean other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(other ? self == 1 : self == 0);
        }

        @Specialization
        static byte eq(Double self, int other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(self == other);
        }

        @Specialization
        static byte eq(Double self, long other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(self == other);
        }

        @Specialization
        static byte eq(Double self, double other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(self == other);
        }

        @Specialization
        @TruffleBoundary
        static byte eq(Double self, PInt other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(self == other.doubleValue());
        }

        @Specialization
        static byte eq(Double self, PFloat other, @SuppressWarnings("unused") CallContext context) {
            return fromBool(self == other.getValue());
        }

        @Fallback
        @SuppressWarnings("unused")
        static byte eq(Double self, Object other, @SuppressWarnings("unused") CallContext context) {
            return -1;
        }
    }

    @ExportMessage
    static class Hash {
        static boolean noDecimals(Double self, @SuppressWarnings("unused") CallContext context) {
            return self % 1 == 0;
        }

        @Specialization(guards = {"noDecimals(self)"})
        static long hashDoubleNoDecimals(Double self, @SuppressWarnings("unused") CallContext context) {
            return self.longValue();
        }

        @Specialization(guards = {"!noDecimals(self)"})
        static long hashDoubleWithDecimals(Double self, @SuppressWarnings("unused") CallContext context) {
            return Double.valueOf(self).hashCode();
        }
    }
}
