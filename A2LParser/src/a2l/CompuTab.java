/*
 * Creation : 5 janv. 2019
 */
package a2l;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import constante.ConversionType;

public final class CompuTab extends ConversionTable {

    private int numberValuePairs;
    private Map<Float, Float> valuePairs;
    private String defaultValue; // DEFAULT_VALUE
    private float defaultValueNumeric = Float.NaN; // DEFAULT_VALUE_NUMERIC

    public CompuTab(List<String> parameters, int beginLine, int endLine) {

        build(parameters, beginLine, endLine);

    }

    public final int getNumberValuePairs() {
        return numberValuePairs;
    }

    public final Map<Float, Float> getValuePairs() {
        return valuePairs;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 5) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.conversionType = ConversionType.getConversionType(parameters.get(4));
            this.numberValuePairs = Integer.parseInt(parameters.get(5));

            this.valuePairs = new LinkedHashMap<Float, Float>();

            int lastIdx = parameters.indexOf("DEFAULT_VALUE");
            if (lastIdx < 0) {
                lastIdx = parameters.indexOf("DEFAULT_VALUE_NUMERIC");
            }

            final List<String> listValuePairs;

            if (lastIdx > -1) {
                listValuePairs = parameters.subList(6, lastIdx);
            } else {
                listValuePairs = parameters.subList(6, parameters.size());
            }

            for (int i = 0; i < listValuePairs.size(); i++) {
                if (i % 2 == 0) {
                    valuePairs.put(Float.parseFloat(listValuePairs.get(i)), Float.parseFloat(listValuePairs.get(i + 1)));
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

	@Override
	public String getProperties() {
		
		StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + longIdentifier + "\n");
        sb.append("<li><b>Conversion type: </b>" + conversionType.name() + "\n");
        sb.append("<li><b>Number of value pairs: </b>" + numberValuePairs + "\n");
        sb.append("<li><b>Value pairs: </b>");
        sb.append("<ul>");
        for(Entry<Float, Float> entry : valuePairs.entrySet())
        {
        	sb.append("<li>" + entry.getKey() + " => " + entry.getValue() + "\n");
        }
        sb.append("</ul>");
        sb.append("<li><b>Default value: </b>" + defaultValue + "\n");
        sb.append("<li><b>Default value numeric: </b>" + defaultValueNumeric + "\n");
        sb.append("</u></html>");

        return sb.toString();
	}

}
