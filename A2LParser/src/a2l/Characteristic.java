/*
 * Creation : 2 mars 2018
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.AXIS_DESCR;
import static constante.SecondaryKeywords.BIT_MASK;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CALIBRATION_ACCESS;
import static constante.SecondaryKeywords.COMPARISON_QUANTITY;
import static constante.SecondaryKeywords.DEPENDENT_CHARACTERISTIC;
import static constante.SecondaryKeywords.DISCRETE;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.EXTENDED_LIMITS;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MATRIX_DIM;
import static constante.SecondaryKeywords.MAX_REFRESH;
import static constante.SecondaryKeywords.NUMBER;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;
import static constante.SecondaryKeywords.REF_MEMORY_SEGMENT;
import static constante.SecondaryKeywords.STEP_SIZE;
import static constante.SecondaryKeywords.VIRTUAL_CHARACTERISTIC;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import a2l.AxisDescr.Attribute;
import constante.SecondaryKeywords;

public final class Characteristic extends AdjustableObject {

    private CharacteristicType type;

    private List<AxisDescr> axisDescrs;

    public Characteristic(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION, null);
        optionalsParameters.put(AXIS_DESCR, null);
        optionalsParameters.put(BIT_MASK, null);
        optionalsParameters.put(BYTE_ORDER, null);
        optionalsParameters.put(CALIBRATION_ACCESS, null); // ToDo
        optionalsParameters.put(COMPARISON_QUANTITY, null); // ToDo
        optionalsParameters.put(DEPENDENT_CHARACTERISTIC, null); // ToDo
        optionalsParameters.put(DISCRETE, null); // ToDo
        optionalsParameters.put(DISPLAY_IDENTIFIER, null);
        optionalsParameters.put(ECU_ADDRESS_EXTENSION, null); // ToDo
        optionalsParameters.put(EXTENDED_LIMITS, null); // ToDo
        optionalsParameters.put(FORMAT, null);
        optionalsParameters.put(MATRIX_DIM, null);
        optionalsParameters.put(MAX_REFRESH, null);
        optionalsParameters.put(NUMBER, null);
        optionalsParameters.put(PHYS_UNIT, null);
        optionalsParameters.put(READ_ONLY, null); // Par defaut
        optionalsParameters.put(REF_MEMORY_SEGMENT, null);
        optionalsParameters.put(STEP_SIZE, null);
        optionalsParameters.put(VIRTUAL_CHARACTERISTIC, null);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final List<AxisDescr> getAxisDescrs() {
        return axisDescrs;
    }

    public final CharacteristicType getType() {
        return type;
    }

    public final int getDim() {
        Object oByte = optionalsParameters.get(NUMBER);

        if (oByte == null) {
            oByte = optionalsParameters.get(MATRIX_DIM);
            return (int) ((Object[]) oByte)[0];
        }
        return (int) oByte;
    }

    public final int[] getDimArray() {
        Object numberParam = optionalsParameters.get(NUMBER);
        Object matrixDimParam = optionalsParameters.get(MATRIX_DIM);

        if (matrixDimParam != null) {
            Object[] arrMatrixDim = (Object[]) matrixDimParam;

            switch (arrMatrixDim.length) {
            case 1:
                return new int[] { (int) arrMatrixDim[0] };
            case 2:
                return new int[] { (int) arrMatrixDim[0], (int) arrMatrixDim[1] };
            case 3:
                return new int[] { (int) arrMatrixDim[0], (int) arrMatrixDim[1], (int) arrMatrixDim[2] };
            default:
                return new int[] { 0 };
            }
        }
        return new int[] { (int) numberParam };
    }

    public final boolean hasBitMask() {
        return optionalsParameters.get(BIT_MASK) != null;
    }

    public final double applyBitMask(long value) {
        long bitMask = (long) optionalsParameters.get(BIT_MASK);

        long maskedValue = value & bitMask;

        String bits = Long.toBinaryString(maskedValue);

        int shift = 0;

        for (int i = 0; i < bits.length(); i++) {
            int j = bits.length() - 1 - i;
            if (bits.charAt(j) == '1') {
                shift = i;
                break;
            }
        }

        return maskedValue >> shift;
    }

    public final void assignAxisPts(HashMap<String, AdjustableObject> adjustableObjects) {
        if (axisDescrs != null) {
            for (AxisDescr axisDescr : axisDescrs) {
                Attribute axisType = axisDescr.getAttribute();
                if (axisType.compareTo(Attribute.COM_AXIS) == 0 || axisType.compareTo(Attribute.RES_AXIS) == 0) {
                    axisDescr.setAxisPts(adjustableObjects.get(axisDescr.getAxisRef(axisType)));
                }
                if (axisType.compareTo(Attribute.CURVE_AXIS) == 0) {
                    axisDescr.setCurveAxis(adjustableObjects.get(axisDescr.getAxisRef(axisType)));
                }
            }
        }
    }

    public enum CharacteristicType {

        ASCII, CURVE, MAP, CUBOID, CUBE_4, CUBE_5, VAL_BLK, VALUE, UNKNOWN;

        private static CharacteristicType getCharacteristicType(String name) {
            switch (name) {
            case "ASCII":
                return ASCII;
            case "CURVE":
                return CURVE;
            case "MAP":
                return MAP;
            case "CUBOID":
                return CUBOID;
            case "CUBE_4":
                return CUBE_4;
            case "CUBE_5":
                return CUBE_5;
            case "VAL_BLK":
                return VAL_BLK;
            case "VALUE":
                return VALUE;
            default:
                return UNKNOWN;
            }
        }

    }

    @Override
    public final void assignComputMethod(HashMap<String, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversion);
        if (axisDescrs != null) {
            for (AxisDescr axisDescr : axisDescrs) {
                axisDescr.setCompuMethod(compuMethods.get(axisDescr.getConversion()));
            }
        }
    }

    @Override
    public String[] getUnit() {
        String[] unit;

        switch (this.type) {
        case VALUE:
            unit = new String[] { this.compuMethod.getUnit() };
            break;
        case CURVE:
            unit = new String[2];
            unit[0] = this.axisDescrs.get(0).getPhysUnit().length() > 0 ? this.axisDescrs.get(0).getPhysUnit()
                    : this.axisDescrs.get(0).getCompuMethod().getUnit();
            unit[1] = this.compuMethod.getUnit();
            break;
        case MAP:
            unit = new String[3];
            unit[0] = this.axisDescrs.get(0).getPhysUnit().length() > 0 ? this.axisDescrs.get(0).getPhysUnit()
                    : this.axisDescrs.get(0).getCompuMethod().getUnit();
            unit[1] = this.axisDescrs.get(1).getPhysUnit().length() > 0 ? this.axisDescrs.get(1).getPhysUnit()
                    : this.axisDescrs.get(1).getCompuMethod().getUnit();
            unit[2] = this.compuMethod.getUnit();
            break;
        case VAL_BLK:
            unit = new String[] { this.compuMethod.getUnit() };
            break;
        default:
            unit = new String[] { "" };
            break;
        }
        return unit;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException {

        final int nbParams = parameters.size();

        try {
            if (nbParams >= 9) {

                this.name = parameters.get(2);
                this.longIdentifier = parameters.get(3);
                this.type = CharacteristicType.getCharacteristicType(parameters.get(4));
                this.adress = parameters.get(5);
                this.deposit = parameters.get(6);
                this.maxDiff = Float.parseFloat(parameters.get(7));
                this.conversion = parameters.get(8);
                this.lowerLimit = Float.parseFloat(parameters.get(9));
                this.upperLimit = Float.parseFloat(parameters.get(10));

                int n = 11;

                Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                for (int nPar = n; nPar < nbParams; nPar++) {
                    if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                        switch (parameters.get(nPar)) {
                        case "ANNOTATION":
                            n = nPar + 1;
                            do {
                            } while (!parameters.get(++nPar).equals("ANNOTATION"));
                            optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3), 0, 0));
                            n = nPar + 1;
                            break;
                        case "AXIS_DESCR":
                            if (axisDescrs == null) {
                                axisDescrs = new ArrayList<AxisDescr>();
                                optionalsParameters.put(AXIS_DESCR, axisDescrs);
                            }
                            n = nPar + 1;
                            do {
                            } while (!parameters.get(++nPar).equals("AXIS_DESCR"));
                            axisDescrs.add(new AxisDescr(parameters.subList(n, nPar - 1), 0, 0));
                            n = nPar + 1;
                            break;
                        case "BIT_MASK":
                            String bitMask = parameters.get(nPar + 1);
                            if (bitMask.startsWith("0x")) {
                                optionalsParameters.put(BIT_MASK, Long.parseLong(bitMask.substring(2), 16));
                            } else {
                                optionalsParameters.put(BIT_MASK, Long.parseLong(bitMask));
                            }
                            nPar += 1;
                            break;
                        case "BYTE_ORDER":
                            optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                            nPar += 1;
                            break;
                        case "DISPLAY_IDENTIFIER":
                            optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1));
                            nPar += 1;
                            break;
                        case "FORMAT":
                            optionalsParameters.put(FORMAT, parameters.get(nPar + 1));
                            nPar += 1;
                            break;
                        case "MATRIX_DIM":
                            List<Integer> dim = new ArrayList<Integer>();

                            try {
                                nPar += 1;
                                do {
                                    dim.add(Integer.parseInt(parameters.get(nPar)));
                                    nPar += 1;
                                } while (nPar < parameters.size());
                            } catch (NumberFormatException nfe) {
                                nPar += 1;
                            }
                            optionalsParameters.put(MATRIX_DIM, dim.toArray());
                            dim.clear();
                            break;
                        case "NUMBER":
                            optionalsParameters.put(NUMBER, Integer.parseInt(parameters.get(nPar + 1)));
                            nPar += 1;
                            break;
                        case "PHYS_UNIT":
                            optionalsParameters.put(PHYS_UNIT, parameters.get(nPar + 1));
                            nPar += 1;
                            break;
                        case "READ_ONLY":
                            optionalsParameters.put(READ_ONLY, true);
                            break;
                        default:
                            break;
                        }
                    }
                }

            } else {
                throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
            }
        } catch (IllegalArgumentException e) {
            throw new A2lObjectParsingException("Parsing error on " + this.name, beginLine, endLine);
        }

    }

    @Override
    protected void formatValues() {

        if (values != null) {
            final DecimalFormat df = new DecimalFormat();

            for (short y = 0; y < values.getDimY(); y++) {
                for (short x = 0; x < values.getDimX(); x++) {
                    try {
                        double doubleValue = Double.parseDouble(values.getValue(y, x));

                        if (y == 0 && (type.compareTo(CharacteristicType.CURVE) == 0 || type.compareTo(CharacteristicType.MAP) == 0)) {
                            df.setMaximumFractionDigits(axisDescrs.get(0).getNbDecimal());
                        } else if (x == 0 && type.compareTo(CharacteristicType.MAP) == 0) {
                            df.setMaximumFractionDigits(axisDescrs.get(1).getNbDecimal());
                        } else {
                            df.setMaximumFractionDigits(getNbDecimal());
                        }

                        values.setValue(y, x, df.format(doubleValue));
                    } catch (Exception e) {
                        // Nothing
                    }
                }
            }
        }

    }

}