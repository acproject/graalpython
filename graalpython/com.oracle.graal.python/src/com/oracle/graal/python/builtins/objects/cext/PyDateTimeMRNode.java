package com.oracle.graal.python.builtins.objects.cext;

import com.oracle.graal.python.builtins.objects.cext.CArrayWrappers.CByteArrayWrapper;
import com.oracle.graal.python.builtins.objects.object.PythonObject;
import com.oracle.graal.python.nodes.attributes.ReadAttributeFromObjectNode;
import com.oracle.graal.python.nodes.util.CastToIndexNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;

@ImportStatic(NativeMemberNames.class)
public abstract class PyDateTimeMRNode extends Node {

    public abstract Object execute(PythonObject object, String key);

    public static final String YEAR = "_year";
    public static final String MONTH = "_month";
    public static final String DAY = "_day";
    public static final String HOUR = "_hour";
    public static final String MIN = "_minute";
    public static final String SEC = "_second";
    public static final String USEC = "_microsecond";
    public static final String DATE = "date";

    /**
     * Fields are packed into successive bytes, each viewed as unsigned and big-endian, unless
     * otherwise noted:
     *
     * <code>
     * byte offset
     *  0           year     2 bytes, 1-9999
     *  2           month    1 byte, 1-12
     *  3           day      1 byte, 1-31
     *  4           hour     1 byte, 0-23
     *  5           minute   1 byte, 0-59
     *  6           second   1 byte, 0-59
     *  7           usecond  3 bytes, 0-999999
     * 10
     * </code>
     */
    @Specialization(guards = "eq(DATETIME_DATA,key)")
    Object doData(PythonObject object, @SuppressWarnings("unused") String key,
                    @Cached("create()") ReadAttributeFromObjectNode getYearNode,
                    @Cached("create()") ReadAttributeFromObjectNode getMonthNode,
                    @Cached("create()") ReadAttributeFromObjectNode getDayNode,
                    @Cached("create()") ReadAttributeFromObjectNode getHourNode,
                    @Cached("create()") ReadAttributeFromObjectNode getMinNode,
                    @Cached("create()") ReadAttributeFromObjectNode getSecNode,
                    @Cached("create()") ReadAttributeFromObjectNode getUSecNode,
                    @Cached("create()") CastToIndexNode castToIntNode) {

        int year = castToIntNode.execute(getYearNode.execute(object, YEAR));
        int month = castToIntNode.execute(getMonthNode.execute(object, MONTH));
        int day = castToIntNode.execute(getDayNode.execute(object, DAY));
        int hour = castToIntNode.execute(getHourNode.execute(object, HOUR));
        int min = castToIntNode.execute(getMinNode.execute(object, MIN));
        int sec = castToIntNode.execute(getSecNode.execute(object, SEC));
        int usec = castToIntNode.execute(getUSecNode.execute(object, USEC));
        assert year >= 0 && year < 0x10000;
        assert month >= 0 && month < 0x100;
        assert day >= 0 && day < 0x100;
        assert hour >= 0 && hour < 0x100;
        assert min >= 0 && min < 0x100;
        assert sec >= 0 && sec < 0x100;
        assert usec >= 0 && sec < 0x1000000;
        byte[] data = new byte[]{(byte) (year >> 8), (byte) year, (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec, (byte) (usec >> 16), (byte) (usec >> 8), (byte) usec};

        return new CByteArrayWrapper(data);
    }

    protected boolean eq(String expected, String actual) {
        return expected.equals(actual);
    }

    public static PyDateTimeMRNode create() {
        return PyDateTimeMRNodeGen.create();
    }
}
