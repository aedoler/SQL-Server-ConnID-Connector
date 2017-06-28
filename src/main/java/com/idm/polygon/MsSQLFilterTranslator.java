package com.idm.polygon;

import com.idm.polygon.utilities.Logger;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;


import java.util.List;

/**
 * Created by adoler on 27/6/2017.
 */
public class MsSQLFilterTranslator extends AbstractFilterTranslator<String>  {

    private static final Logger LOG = new Logger();

    @Override
    protected String createEqualsExpression(EqualsFilter filter, boolean not) {
        LOG.write("Inside createEquals FilterTranslator.");
        if (not) {
            return null;
        }

        Attribute attr = filter.getAttribute();
        if (!attr.is(Uid.NAME)) {
            return null;
        }

        List<Object> values = attr.getValue();
        if (values.isEmpty()) {
            return null;
        }

        Object value = values.get(0);

        return value != null ? value.toString() : null;
    }
}
