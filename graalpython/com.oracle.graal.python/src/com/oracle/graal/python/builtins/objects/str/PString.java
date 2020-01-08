/*
 * Copyright (c) 2017, 2019, Oracle and/or its affiliates.
 * Copyright (c) 2013, Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.graal.python.builtins.objects.str;

import com.oracle.graal.python.builtins.objects.cext.PythonNativeWrapperLibrary;
import com.oracle.graal.python.builtins.objects.type.LazyPythonClass;
import com.oracle.graal.python.runtime.sequence.PImmutableSequence;
import com.oracle.graal.python.runtime.sequence.storage.SequenceStorage;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class PString extends PImmutableSequence {

    private CharSequence value;

    public PString(LazyPythonClass clazz, CharSequence value) {
        super(clazz);
        this.value = value;
    }

    public String getValue() {
        return PString.getValue(value);
    }

    public static String getValue(CharSequence charSequence) {
        if (charSequence instanceof PCharSequence) {
            PCharSequence s = (PCharSequence) charSequence;
            return s.materialize();
        } else {
            return (String) charSequence;
        }
    }

    public CharSequence getCharSequence() {
        return value;
    }

    void setCharSequence(String materialized) {
        this.value = materialized;
    }

    public int len() {
        return value.length();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public SequenceStorage getSequenceStorage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        if (value instanceof LazyString) {
            return value.toString().hashCode();
        }
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.equals(value);
    }

    public boolean isNative() {
        return getNativeWrapper() != null && PythonNativeWrapperLibrary.getUncached().isNative(getNativeWrapper());
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    public boolean isString() {
        return true;
    }

    @ExportMessage
    String asString() {
        return getValue();
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    public boolean isHashable() {
        return true;
    }

    @TruffleBoundary(allowInlining = true)
    public static int length(String s) {
        return s.length();
    }

    @TruffleBoundary(allowInlining = true)
    public static String valueOf(char c) {
        return String.valueOf(c);
    }

    @TruffleBoundary(allowInlining = true)
    public static char charAt(String s, int i) {
        return s.charAt(i);
    }

    @TruffleBoundary(allowInlining = true)
    public static int indexOf(String s, String sub, int fromIndex) {
        return s.indexOf(sub, fromIndex);
    }

    @TruffleBoundary(allowInlining = true)
    public static String substring(String str, int start, int end) {
        return str.substring(start, end);
    }

    @TruffleBoundary(allowInlining = true)
    public static String substring(String str, int start) {
        return str.substring(start);
    }

    @TruffleBoundary
    public static boolean isWhitespace(char c) {
        return Character.isWhitespace(c);
    }

    @TruffleBoundary
    public static boolean isWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint);
    }

}
