package org.goobi.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringUtils;
import org.goobi.vocabulary.Definition;
import org.goobi.vocabulary.Field;
import org.goobi.vocabulary.VocabRecord;
import org.goobi.vocabulary.Vocabulary;

import de.sub.goobi.helper.Helper;
import de.sub.goobi.persistence.managers.VocabularyManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * 
 * This bean can be used to display the current state of the goobi_fast and goobi_slow queues. The bean provides methods to show all active tickets
 * and remove a ticket or clear the queue.
 *
 */

@javax.faces.bean.ManagedBean
@SessionScoped
@Log4j
public class VocabularyBean extends BasicBean implements Serializable {

    private static final long serialVersionUID = -4591427229251805665L;

    @Getter
    @Setter
    private Vocabulary currentVocabulary;

    @Getter
    @Setter
    private Definition currentDefinition;

    @Getter
    @Setter
    private VocabRecord currentVocabRecord;

    //details up or down
    @Getter
    @Setter
    private String uiStatus;

    @Getter
    private String[] possibleDefinitionTypes = { "input", "textarea", "select", "select1", "html" };

    private List<VocabRecord> recordsToDelete;

    public VocabularyBean() {
        uiStatus = "down";
        sortierung = "title";
    }

    public String FilterKein() {
        VocabularyManager vm = new VocabularyManager();
        paginator = new DatabasePaginator(sortierung, filter, vm, "vocabulary_all");
        return "vocabulary_all";
    }

    public String editVocabulary() {
        return "vocabulary_edit";
    }

    public String editRecords() {
        recordsToDelete = new ArrayList<>();
        // load records of selected vocabulary
        VocabularyManager.loadRecordsForVocabulary(currentVocabulary);
        if (!currentVocabulary.getRecords().isEmpty()) {
            currentVocabRecord = currentVocabulary.getRecords().get(0);
        } else {
            addRecord();
        }
        return "vocabulary_records";
    }

    public String newVocabulary() {
        currentVocabulary = new Vocabulary();
        return editVocabulary();
    }

    public String saveVocabulary() {
        int numberOfMainEntries = 0;
        for (Definition def : currentVocabulary.getStruct()) {
            if (def.isMainEntry()) {
                numberOfMainEntries++;
            }
        }

        // check if one field is marked as main entry
        if (numberOfMainEntries == 0) {
            Helper.setFehlerMeldung(Helper.getTranslation("vocabularyManager_noMainEntry"));
            return "";
        } else if (numberOfMainEntries > 1) {
            Helper.setFehlerMeldung(Helper.getTranslation("vocabularyManager_wrongNumberOfMainEntries"));
            return "";
        }
        // check if title is unique
        if (VocabularyManager.isTitleUnique(currentVocabulary)) {
            VocabularyManager.saveVocabulary(currentVocabulary);
        } else {
            Helper.setFehlerMeldung(Helper.getTranslation("vocabularyManager_titleNotUnique"));
            return "";
        }

        return cancelEdition();
    }

    public String deleteVocabulary() {
        if (currentVocabulary.getId() != null) {
            // TODO delete records as well?
            VocabularyManager.deleteVocabulary(currentVocabulary);
        }
        return cancelEdition();
    }

    public String cancelEdition() {
        return FilterKein();
    }

    public void deleteDefinition() {
        if (currentDefinition != null && currentVocabulary != null) {
            currentVocabulary.getStruct().remove(currentDefinition);
        }
    }

    public void addDefinition() {
        currentVocabulary.getStruct().add(new Definition());
    }

    public void addRecord() {
        VocabRecord rec = new VocabRecord();
        List<Field> fieldList = new ArrayList<>();
        for (Definition definition : currentVocabulary.getStruct()) {
            Field field = new Field(definition.getLabel(), definition.getLanguage(), "", definition);
            fieldList.add(field);
        }
        rec.setFields(fieldList);
        currentVocabulary.getRecords().add(rec);
        currentVocabRecord = rec;
    }

    public void deleteRecord() {
        recordsToDelete.add(currentVocabRecord);
        currentVocabulary.getRecords().remove(currentVocabRecord);
    }

    public String cancelRecordEdition() {
        recordsToDelete.clear();

        return cancelEdition();
    }

    public String saveRecordEdition() {
        boolean valid = true;
        for (VocabRecord vr : currentVocabulary.getRecords()) {
            vr.setValid(true);
            for (Field field : vr.getFields()) {
                field.setValidationMessage(null);
                if (field.getDefinition().isRequired()) {
                    if (StringUtils.isBlank(field.getValue())) {
                        valid = false;
                        vr.setValid(false);
                        field.setValidationMessage("vocabularyManager_validation_fieldIsRequired");
                    }
                }
                if (field.getDefinition().isUnique() && StringUtils.isNotBlank(field.getValue())) {
                    requiredCheck: for (VocabRecord other : currentVocabulary.getRecords()) {
                        if (!vr.equals(other)) {
                            for (Field f : other.getFields()) {
                                if (field.getDefinition().equals(f.getDefinition())) {
                                    if (field.getValue().equals(f.getValue())) {
                                        valid = false;
                                        vr.setValid(false);
                                        field.setValidationMessage("vocabularyManager_validation_fieldIsNotUnique");
                                        break requiredCheck;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        if (!valid) {
            return "";
        }
        for (VocabRecord vr : recordsToDelete) {
            VocabularyManager.deleteRecord(vr);
        }

        VocabularyManager.saveRecords(currentVocabulary);
        return cancelEdition();
    }

    public void Reload() {

    }
}
