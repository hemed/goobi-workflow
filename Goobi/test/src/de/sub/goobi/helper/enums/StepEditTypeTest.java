package de.sub.goobi.helper.enums;

import static org.junit.Assert.*;

import org.junit.Test;

import de.sub.goobi.helper.Helper;

public class StepEditTypeTest {

    @Test
    public void testStepEditTypeGetValue() {
        assertTrue(StepEditType.UNNOWKN.getValue() == 0);
    }
    
    @Test
    public void testStepEditTypeGetTitle() {
        assertEquals(Helper.getTranslation("unbekannt"), StepEditType.UNNOWKN.getTitle());
    }
    
    
    @Test
    public void testStepEditTypeGetTypeFromValue() {
        assertEquals(StepEditType.MANUAL_SINGLE, StepEditType.getTypeFromValue(1));
        assertEquals(StepEditType.UNNOWKN, StepEditType.getTypeFromValue(666));        
    }
}
