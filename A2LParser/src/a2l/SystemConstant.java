/*
 * Creation : 8 avr. 2019
 */
package a2l;

import java.util.List;

public final class SystemConstant implements Comparable<SystemConstant> {

    private final String name;
    private final String value;

    public SystemConstant(List<String> parameters) {
        this.name = parameters.get(0);
        this.value = parameters.get(1);
    }

    public final String getName() {
        return this.name;
    }

    public final String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(SystemConstant o) {
        return this.name.compareTo(o.name);
    }

}
