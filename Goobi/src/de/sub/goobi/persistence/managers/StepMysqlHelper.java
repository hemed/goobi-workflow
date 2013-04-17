package de.sub.goobi.persistence.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.goobi.beans.ErrorProperty;
import org.goobi.beans.Step;
import org.goobi.beans.User;
import org.goobi.beans.Usergroup;

import de.sub.goobi.helper.enums.PropertyType;
import de.sub.goobi.helper.enums.StepEditType;
import de.sub.goobi.helper.enums.StepStatus;

class StepMysqlHelper {
    private static final Logger logger = Logger.getLogger(StepMysqlHelper.class);

    public static List<Step> getStepsForProcess(int processId) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        String sql = "SELECT * FROM schritte WHERE schritte.ProzesseID = ? order by Reihenfolge";
        Object[] param = { processId };
        try {
            List<Step> list = new QueryRunner().query(connection, sql, resultSetToStepListHandler, param);
            return list;
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static int getStepCount(String order, String filter) throws SQLException {

        Connection connection = MySQLHelper.getInstance().getConnection();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(SchritteID) FROM schritte, prozesse, projekte WHERE schritte.prozesseId = prozesse.ProzesseID and prozesse.ProjekteID = projekte.ProjekteID ");
        if (filter != null && !filter.isEmpty()) {
            sql.append(" AND " + filter);
        }
        try {
            logger.debug(sql.toString());
            if (filter != null && !filter.isEmpty()) {
                return new QueryRunner().query(connection, sql.toString(), MySQLHelper.resultSetToIntegerHandler);
            } else {
                return new QueryRunner().query(connection, sql.toString(), MySQLHelper.resultSetToIntegerHandler);
            }
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static List<Step> getSteps(String order, String filter, Integer start, Integer count) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM schritte, prozesse, projekte WHERE schritte.prozesseId = prozesse.ProzesseID and prozesse.ProjekteID = projekte.ProjekteID ");
        if (filter != null && !filter.isEmpty()) {
            sql.append(" AND " + filter);
        }

        if (order != null && !order.isEmpty()) {
            sql.append(" ORDER BY " + order);
        }
        if (start != null && count != null) {
            sql.append(" LIMIT " + start + ", " + count);
        }

        try {
            logger.debug(sql.toString());
            List<Step> ret = null;
            if (filter != null && !filter.isEmpty()) {
                ret = new QueryRunner().query(connection, sql.toString(), resultSetToStepListHandler);
            } else {
                ret = new QueryRunner().query(connection, sql.toString(), resultSetToStepListHandler);
            }
            return ret;
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static ResultSetHandler<List<Step>> resultSetToStepListHandler = new ResultSetHandler<List<Step>>() {

        @Override
        public List<Step> handle(ResultSet rs) throws SQLException {
            List<Step> answer = new ArrayList<Step>();
            while (rs.next()) {
                answer.add(convert(rs));
            }
            return answer;
        }

    };

    public static ResultSetHandler<Step> resultSetToStepHandler = new ResultSetHandler<Step>() {
        public Step handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                try {
                    Step o = convert(rs);
                    return o;
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            return null;
        }
    };

    private static Step convert(ResultSet rs) throws SQLException {
        Step s = new Step();
        s.setId(rs.getInt("SchritteID"));
        s.setTitel(rs.getString("Titel"));
        s.setPrioritaet(rs.getInt("Prioritaet"));
        s.setReihenfolge(rs.getInt("Reihenfolge"));
        s.setBearbeitungsstatusEnum(StepStatus.getStatusFromValue(rs.getInt("Bearbeitungsstatus")));
        s.setBearbeitungszeitpunkt(rs.getDate("BearbeitungsZeitpunkt"));
        s.setBearbeitungsbeginn(rs.getDate("BearbeitungsBeginn"));
        s.setBearbeitungsende(rs.getDate("BearbeitungsEnde"));
        s.setHomeverzeichnisNutzen(rs.getShort("homeverzeichnisNutzen"));
        s.setTypMetadaten(rs.getBoolean("typMetadaten"));
        s.setTypAutomatisch(rs.getBoolean("typAutomatisch"));
        s.setTypImportFileUpload(rs.getBoolean("typImportFileUpload"));
        s.setTypExportRus(rs.getBoolean("typExportRus"));
        s.setTypImagesLesen(rs.getBoolean("typImagesLesen"));
        s.setTypImagesSchreiben(rs.getBoolean("typImagesSchreiben"));
        s.setTypExportDMS(rs.getBoolean("typExportDMS"));
        s.setTypBeimAnnehmenModul(rs.getBoolean("typBeimAnnehmenModul"));
        s.setTypBeimAnnehmenAbschliessen(rs.getBoolean("typBeimAnnehmenAbschliessen"));
        s.setTypBeimAnnehmenModulUndAbschliessen(rs.getBoolean("typBeimAnnehmenModulUndAbschliessen"));
        s.setTypAutomatischScriptpfad(rs.getString("typAutomatischScriptpfad"));
        s.setTypBeimAbschliessenVerifizieren(rs.getBoolean("typBeimAbschliessenVerifizieren"));
        s.setTypModulName(rs.getString("typModulName"));
        s.setTypBeimAnnehmenModul(rs.getBoolean("typBeimAnnehmenModul"));
        s.setTypBeimAnnehmenModul(rs.getBoolean("typBeimAnnehmenModul"));
        s.setTypBeimAnnehmenModul(rs.getBoolean("typBeimAnnehmenModul"));
        s.setUserId(rs.getInt("BearbeitungsBenutzerID"));
        s.setProcessId(rs.getInt("ProzesseID"));
        s.setEditTypeEnum(StepEditType.getTypeFromValue(rs.getInt("edittype")));
        s.setTypScriptStep(rs.getBoolean("typScriptStep"));
        s.setScriptname1(rs.getString("scriptName1"));
        s.setScriptname2(rs.getString("scriptName2"));
        s.setTypAutomatischScriptpfad2(rs.getString("typAutomatischScriptpfad2"));
        s.setScriptname3(rs.getString("scriptName3"));
        s.setTypAutomatischScriptpfad3(rs.getString("typAutomatischScriptpfad3"));
        s.setScriptname4(rs.getString("scriptName4"));
        s.setTypAutomatischScriptpfad4(rs.getString("typAutomatischScriptpfad4"));
        s.setScriptname5(rs.getString("scriptName5"));
        s.setTypAutomatischScriptpfad5(rs.getString("typAutomatischScriptpfad5"));
        s.setBatchStep(rs.getBoolean("batchStep"));
        s.setStepPlugin(rs.getString("stepPlugin"));
        s.setValidationPlugin(rs.getString("validationPlugin"));
        // load error properties
        List<ErrorProperty> stepList = getErrorPropertiesForStep(s.getId());
        if (!stepList.isEmpty()) {
            for (ErrorProperty property : stepList) {
                property.setSchritt(s);
            }
            s.setEigenschaften(stepList);
        }
        return s;
    }

    public static ResultSetHandler<List<ErrorProperty>> resultSetToErrorPropertyListHandler = new ResultSetHandler<List<ErrorProperty>>() {

        @Override
        public List<ErrorProperty> handle(ResultSet rs) throws SQLException {
            List<ErrorProperty> properties = new ArrayList<ErrorProperty>();
            while (rs.next()) {
                int id = rs.getInt("schritteeigenschaftenID");
                String title = rs.getString("Titel");
                String value = rs.getString("Wert");
                Boolean mandatory = rs.getBoolean("IstObligatorisch");
                int type = rs.getInt("DatentypenID");
                String choice = rs.getString("Auswahl");
                Date creationDate = rs.getDate("creationDate");
                int container = rs.getInt("container");
                ErrorProperty ve = new ErrorProperty();
                ve.setId(id);
                ve.setTitel(title);
                ve.setWert(value);
                ve.setIstObligatorisch(mandatory);
                ve.setType(PropertyType.getById(type));
                ve.setAuswahl(choice);
                ve.setCreationDate(creationDate);
                ve.setContainer(container);
                properties.add(ve);
            }
            return properties;
        }
    };

    public static ResultSetHandler<Boolean> checkForResultHandler = new ResultSetHandler<Boolean>() {

        @Override
        public Boolean handle(ResultSet rs) throws SQLException {

            if (rs.next()) {
                return true;
            }
            return false;
        }
    };

    public static Step getStepById(int id) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        String sql = "SELECT * FROM schritte WHERE SchritteID = ?";
        Object[] param = { id };
        try {
            Step s = new QueryRunner().query(connection, sql, resultSetToStepHandler, param);
            return s;
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static void deleteStep(Step o) throws SQLException {
        if (o.getId() != null) {
            for (ErrorProperty property : o.getEigenschaften()) {
                deleteErrorProperty(property);
            }

            String sql = "DELETE FROM schritte WHERE SchritteID = ?";
            Object[] param = { o.getId() };
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                QueryRunner run = new QueryRunner();
                run.update(connection, sql, param);
            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }

    }

    public static List<Step> getAllSteps() throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM schritte");
        try {
            logger.debug(sql.toString());
            List<Step> ret = new QueryRunner().query(connection, sql.toString(), resultSetToStepListHandler);
            return ret;
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static void saveStep(Step o) throws SQLException {

        if (o.getId() == null) {
            // new process
            insertStep(o);
        } else {
            // process exists already in database
            updateStep(o);
        }

        if (o.getEigenschaftenSize() > 0) {
            for (ErrorProperty property : o.getEigenschaften()) {
                saveErrorProperty(property);
            }
        }

        if (o.getBenutzerSize() > 0) {
            saveUserAssignment(o);
        }
        if (o.getBenutzergruppenSize() > 0) {
            saveUserGroupAssignment(o);
        }

    }

    private static void saveErrorProperty(ErrorProperty property) throws SQLException {
        if (property.getId() == null) {
            String sql =
                    "INSERT INTO schritteeigenschaften (Titel, WERT, IstObligatorisch, DatentypenID, Auswahl, schritteID, creationDate, container) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] param =
                    { property.getTitel(), property.getWert(), property.isIstObligatorisch(), property.getType().getId(), property.getAuswahl(),
                            property.getSchritt().getId(),
                            property.getCreationDate() == null ? null : new Timestamp(property.getCreationDate().getTime()), property.getContainer() };
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                QueryRunner run = new QueryRunner();
                logger.debug(sql.toString());
                Integer id = run.insert(connection, sql.toString(), MySQLHelper.resultSetToIntegerHandler, param);
                if (id != null) {
                    property.setId(id);
                }
            } finally {
                MySQLHelper.closeConnection(connection);
            }

        } else {
            String sql =
                    "UPDATE schritteeigenschaften set Titel = ?,  WERT = ?, IstObligatorisch = ?, DatentypenID = ?, Auswahl = ?, schritteID = ?, creationDate = ?, container = ? WHERE schritteeigenschaftenID = "
                            + property.getId();
            Object[] param =
                    { property.getTitel(), property.getWert(), property.isIstObligatorisch(), property.getType().getId(), property.getAuswahl(),
                            property.getSchritt().getId(),
                            property.getCreationDate() == null ? null : new Timestamp(property.getCreationDate().getTime()), property.getContainer() };
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                QueryRunner run = new QueryRunner();
                run.update(connection, sql, param);
            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    private static List<ErrorProperty> getErrorPropertiesForStep(int stepId) throws SQLException {
        String sql = "SELECT * FROM schritteeigenschaften WHERE schritteID = " + stepId;
        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            return run.query(connection, sql, resultSetToErrorPropertyListHandler);
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    private static void deleteErrorProperty(ErrorProperty property) throws SQLException {
        String sql = "DELETE FROM schritteeigenschaften WHERE schritteeigenschaftenID = " + property.getId();
        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            run.update(connection, sql, resultSetToErrorPropertyListHandler);
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    private static void insertStep(Step o) throws SQLException {
        String sql = "INSERT INTO schritte " + generateInsertQuery(false) + generateValueQuery(false);
        Object[] param = generateParameter(o, false);
        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            logger.debug(sql.toString());
            Integer id = run.insert(connection, sql.toString(), MySQLHelper.resultSetToIntegerHandler, param);
            if (id != null) {
                o.setId(id);
            }
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    private static Object[] generateParameter(Step o, boolean includeID) {
        if (includeID) {
            Object[] param =
                    {
                            o.getId(), //SchritteID
                            o.getTitel(), //Titel
                            o.getPrioritaet(), //Prioritaet
                            o.getReihenfolge(), //Reihenfolge
                            o.getBearbeitungsstatusAsString(), //Bearbeitungsstatus
                            o.getBearbeitungszeitpunkt() == null ? null : new Timestamp(o.getBearbeitungszeitpunkt().getTime()), // BearbeitungsZeitpunkt
                            o.getBearbeitungsbeginn() == null ? null : new Timestamp(o.getBearbeitungsbeginn().getTime()), // BearbeitungsBeginn
                            o.getBearbeitungsende() == null ? null : new Timestamp(o.getBearbeitungsende().getTime()), // BearbeitungsEnde
                            o.getHomeverzeichnisNutzen(), // homeverzeichnisNutzen
                            o.isTypMetadaten(), // typMetadaten
                            o.isTypAutomatisch(), // typAutomatisch
                            o.isTypImportFileUpload(), // typImportFileUpload
                            o.isTypExportRus(), //typExportRus
                            o.isTypImagesLesen(),//typImagesLesen
                            o.isTypImagesSchreiben(), // typImagesSchreiben
                            o.isTypExportDMS(), // typExportDMS
                            o.isTypBeimAnnehmenModul(), // typBeimAnnehmenModul
                            o.isTypBeimAnnehmenAbschliessen(), // typBeimAnnehmenAbschliessen
                            o.isTypBeimAnnehmenModulUndAbschliessen(), // typBeimAnnehmenModulUndAbschliessen
                            (o.getTypAutomatischScriptpfad() == null || o.getTypAutomatischScriptpfad().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad(), // typAutomatischScriptpfad
                            o.isTypBeimAbschliessenVerifizieren(), // typBeimAbschliessenVerifizieren
                            (o.getTypModulName() == null || o.getTypModulName().equals("")) ? null : o.getTypModulName(), // typModulName
                            o.getUserId() == null ? null : o.getUserId(), //BearbeitungsBenutzerID
                            o.getProcessId() == null ? null : o.getProcessId(), //ProzesseID
                            o.getEditTypeEnum().getValue(), //edittype
                            o.getTypScriptStep(), //typScriptStep
                            (o.getScriptname1() == null || o.getScriptname1().equals("")) ? null : o.getScriptname1(), //scriptName1
                            (o.getScriptname2() == null || o.getScriptname2().equals("")) ? null : o.getScriptname2(), //scriptName2
                            (o.getTypAutomatischScriptpfad2() == null || o.getTypAutomatischScriptpfad2().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad2(), //typAutomatischScriptpfad2
                            (o.getScriptname3() == null || o.getScriptname3().equals("")) ? null : o.getScriptname3(), //scriptName3
                            (o.getTypAutomatischScriptpfad3() == null || o.getTypAutomatischScriptpfad3().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad3(), //typAutomatischScriptpfad3
                            (o.getScriptname4() == null || o.getScriptname4().equals("")) ? null : o.getScriptname4(), //scriptName4
                            (o.getTypAutomatischScriptpfad4() == null || o.getTypAutomatischScriptpfad4().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad4(), //typAutomatischScriptpfad4
                            (o.getScriptname5() == null || o.getScriptname5().equals("")) ? null : o.getScriptname5(), //scriptName5
                            (o.getTypAutomatischScriptpfad5() == null || o.getTypAutomatischScriptpfad5().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad5(), //typAutomatischScriptpfad5
                            o.getBatchStep(), //batchStep
                            (o.getStepPlugin() == null || o.getStepPlugin().equals("")) ? null : o.getStepPlugin(),// stepPlugin
                            (o.getValidationPlugin() == null || o.getValidationPlugin().equals("")) ? null : o.getValidationPlugin() //validationPlugin
                    };
            return param;
        } else {
            Object[] param =
                    {
                            o.getTitel(), //Titel
                            o.getPrioritaet(), //Prioritaet
                            o.getReihenfolge(), //Reihenfolge
                            o.getBearbeitungsstatusAsString(), //Bearbeitungsstatus
                            o.getBearbeitungszeitpunkt() == null ? null : new Timestamp(o.getBearbeitungszeitpunkt().getTime()), // BearbeitungsZeitpunkt
                            o.getBearbeitungsbeginn() == null ? null : new Timestamp(o.getBearbeitungsbeginn().getTime()), // BearbeitungsBeginn
                            o.getBearbeitungsende() == null ? null : new Timestamp(o.getBearbeitungsende().getTime()), // BearbeitungsEnde
                            o.getHomeverzeichnisNutzen(), // homeverzeichnisNutzen
                            o.isTypMetadaten(), // typMetadaten
                            o.isTypAutomatisch(), // typAutomatisch
                            o.isTypImportFileUpload(), // typImportFileUpload
                            o.isTypExportRus(), //typExportRus
                            o.isTypImagesLesen(),//typImagesLesen
                            o.isTypImagesSchreiben(), // typImagesSchreiben
                            o.isTypExportDMS(), // typExportDMS
                            o.isTypBeimAnnehmenModul(), // typBeimAnnehmenModul
                            o.isTypBeimAnnehmenAbschliessen(), // typBeimAnnehmenAbschliessen
                            o.isTypBeimAnnehmenModulUndAbschliessen(), // typBeimAnnehmenModulUndAbschliessen
                            (o.getTypAutomatischScriptpfad() == null || o.getTypAutomatischScriptpfad().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad(), // typAutomatischScriptpfad
                            o.isTypBeimAbschliessenVerifizieren(), // typBeimAbschliessenVerifizieren
                            (o.getTypModulName() == null || o.getTypModulName().equals("")) ? null : o.getTypModulName(), // typModulName
                            o.getUserId() == null ? null : o.getUserId(), //BearbeitungsBenutzerID
                            o.getProcessId() == null ? null : o.getProcessId(), //ProzesseID
                            o.getEditTypeEnum().getValue(), //edittype
                            o.getTypScriptStep(), //typScriptStep
                            (o.getScriptname1() == null || o.getScriptname1().equals("")) ? null : o.getScriptname1(), //scriptName1
                            (o.getScriptname2() == null || o.getScriptname2().equals("")) ? null : o.getScriptname2(), //scriptName2
                            (o.getTypAutomatischScriptpfad2() == null || o.getTypAutomatischScriptpfad2().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad2(), //typAutomatischScriptpfad2
                            (o.getScriptname3() == null || o.getScriptname3().equals("")) ? null : o.getScriptname3(), //scriptName3
                            (o.getTypAutomatischScriptpfad3() == null || o.getTypAutomatischScriptpfad3().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad3(), //typAutomatischScriptpfad3
                            (o.getScriptname4() == null || o.getScriptname4().equals("")) ? null : o.getScriptname4(), //scriptName4
                            (o.getTypAutomatischScriptpfad4() == null || o.getTypAutomatischScriptpfad4().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad4(), //typAutomatischScriptpfad4
                            (o.getScriptname5() == null || o.getScriptname5().equals("")) ? null : o.getScriptname5(), //scriptName5
                            (o.getTypAutomatischScriptpfad5() == null || o.getTypAutomatischScriptpfad5().equals("")) ? null : o
                                    .getTypAutomatischScriptpfad5(), //typAutomatischScriptpfad5
                            o.getBatchStep(), //batchStep
                            (o.getStepPlugin() == null || o.getStepPlugin().equals("")) ? null : o.getStepPlugin(),// stepPlugin
                            (o.getValidationPlugin() == null || o.getValidationPlugin().equals("")) ? null : o.getValidationPlugin() //validationPlugin
                    };
            return param;
        }
    }

    private static String generateValueQuery(boolean includeID) {
        if (!includeID) {
            return "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            return "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

    }

    private static String generateInsertQuery(boolean includeID) {
        String answer = "(";
        if (includeID) {
            answer += " SchritteID, ";
        }
        answer +=
                "Titel, Prioritaet, Reihenfolge, Bearbeitungsstatus, BearbeitungsZeitpunkt, BearbeitungsBeginn, BearbeitungsEnde, "
                        + "homeverzeichnisNutzen, typMetadaten, typAutomatisch, typImportFileUpload, typExportRus, typImagesLesen, typImagesSchreiben, "
                        + "typExportDMS, typBeimAnnehmenModul, typBeimAnnehmenAbschliessen, typBeimAnnehmenModulUndAbschliessen, typAutomatischScriptpfad, "
                        + "typBeimAbschliessenVerifizieren, typModulName, BearbeitungsBenutzerID, ProzesseID, edittype, typScriptStep, scriptName1, "
                        + "scriptName2, typAutomatischScriptpfad2, scriptName3, typAutomatischScriptpfad3, scriptName4, typAutomatischScriptpfad4, "
                        + "scriptName5, typAutomatischScriptpfad5, batchStep, stepPlugin, validationPlugin)" + " VALUES ";
        return answer;
    }

    private static void updateStep(Step o) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE schritte SET ");
        sql.append(" Titel = ?,");
        sql.append(" Prioritaet = ?,");
        sql.append(" Reihenfolge = ?,");
        sql.append(" Bearbeitungsstatus = ?,");
        sql.append(" BearbeitungsZeitpunkt = ?,");
        sql.append(" BearbeitungsBeginn = ?,");
        sql.append(" BearbeitungsEnde = ?,");
        sql.append(" homeverzeichnisNutzen = ?,");
        sql.append(" typMetadaten = ?,");
        sql.append(" typAutomatisch = ?,");
        sql.append(" typImportFileUpload = ?,");
        sql.append(" typExportRus = ?,");
        sql.append(" typImagesLesen = ?,");
        sql.append(" typImagesSchreiben = ?,");
        sql.append(" typExportDMS = ?,");
        sql.append(" typBeimAnnehmenModul = ?,");
        sql.append(" typBeimAnnehmenAbschliessen = ?,");
        sql.append(" typBeimAnnehmenModulUndAbschliessen = ?,");
        sql.append(" typAutomatischScriptpfad = ?,");
        sql.append(" typBeimAbschliessenVerifizieren = ?,");
        sql.append(" typModulName = ?,");
        sql.append(" BearbeitungsBenutzerID = ?,");
        sql.append(" ProzesseID = ?,");
        sql.append(" edittype = ?,");
        sql.append(" typScriptStep = ?,");
        sql.append(" scriptName1 = ?,");
        sql.append(" scriptName2 = ?,");
        sql.append(" typAutomatischScriptpfad2 = ?,");
        sql.append(" scriptName3 = ?,");
        sql.append(" typAutomatischScriptpfad3 = ?,");
        sql.append(" scriptName4 = ?,");
        sql.append(" typAutomatischScriptpfad4 = ?,");
        sql.append(" scriptName5 = ?,");
        sql.append(" typAutomatischScriptpfad5 = ?,");
        sql.append(" batchStep = ?,");
        sql.append(" stepPlugin = ?,");
        sql.append(" validationPlugin = ?");
        sql.append(" WHERE SchritteID = " + o.getId());

        Object[] param = generateParameter(o, false);

        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            run.update(connection, sql.toString(), param);
        } finally {
            MySQLHelper.closeConnection(connection);
        }

    }

    public static void updateBatchList(List<Step> stepList) throws SQLException {
        String tablename = "a" + String.valueOf(new Date().getTime());
        String tempTable = "CREATE TEMPORARY TABLE IF NOT EXISTS " + tablename + " LIKE schritte;";

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + tablename + " " + generateInsertQuery(true));
        List<Object[]> paramList = new ArrayList<Object[]>();
        for (Step o : stepList) {
            sql.append(" " + generateValueQuery(true) + ",");
            Object[] param = generateParameter(o, true);
            paramList.add(param);
        }
        Object[][] paramArray = new Object[paramList.size()][];
        paramList.toArray(paramArray);

        String insertQuery = sql.toString();
        insertQuery = insertQuery.substring(0, insertQuery.length() - 1);

        StringBuilder joinQuery = new StringBuilder();

        joinQuery.append("UPDATE schritte SET schritte.Titel = " + tablename + ".Titel, ");
        joinQuery.append(" Prioritaet = " + tablename + ".Prioritaet,");
        joinQuery.append(" Reihenfolge = " + tablename + ".Reihenfolge,");
        joinQuery.append(" Bearbeitungsstatus = " + tablename + ".Bearbeitungsstatus,");
        joinQuery.append(" BearbeitungsZeitpunkt = " + tablename + ".BearbeitungsZeitpunkt,");
        joinQuery.append(" BearbeitungsBeginn = " + tablename + ".BearbeitungsBeginn,");
        joinQuery.append(" BearbeitungsEnde = " + tablename + ".BearbeitungsEnde,");
        joinQuery.append(" homeverzeichnisNutzen = " + tablename + ".homeverzeichnisNutzen,");
        joinQuery.append(" typMetadaten = " + tablename + ".typMetadaten,");
        joinQuery.append(" typAutomatisch = " + tablename + ".typAutomatisch,");
        joinQuery.append(" typImportFileUpload = " + tablename + ".typImportFileUpload,");
        joinQuery.append(" typExportRus = " + tablename + ".typExportRus,");
        joinQuery.append(" typImagesLesen = " + tablename + ".typImagesLesen,");
        joinQuery.append(" typImagesSchreiben = " + tablename + ".typImagesSchreiben,");
        joinQuery.append(" typExportDMS = " + tablename + ".typExportDMS,");
        joinQuery.append(" typBeimAnnehmenModul = " + tablename + ".typBeimAnnehmenModul,");
        joinQuery.append(" typBeimAnnehmenAbschliessen = " + tablename + ".typBeimAnnehmenAbschliessen,");
        joinQuery.append(" typBeimAnnehmenModulUndAbschliessen = " + tablename + ".typBeimAnnehmenModulUndAbschliessen,");
        joinQuery.append(" typAutomatischScriptpfad = " + tablename + ".typAutomatischScriptpfad,");
        joinQuery.append(" typBeimAbschliessenVerifizieren = " + tablename + ".typBeimAbschliessenVerifizieren,");
        joinQuery.append(" typModulName = " + tablename + ".typModulName,");
        joinQuery.append(" BearbeitungsBenutzerID = " + tablename + ".BearbeitungsBenutzerID,");
        joinQuery.append(" ProzesseID = " + tablename + ".ProzesseID,");
        joinQuery.append(" edittype = " + tablename + ".edittype,");
        joinQuery.append(" typScriptStep = " + tablename + ".typScriptStep,");
        joinQuery.append(" scriptName1 = " + tablename + ".scriptName1,");
        joinQuery.append(" scriptName2 = " + tablename + ".scriptName2,");
        joinQuery.append(" typAutomatischScriptpfad2 = " + tablename + ".typAutomatischScriptpfad2,");
        joinQuery.append(" scriptName3 = " + tablename + ".scriptName3,");
        joinQuery.append(" typAutomatischScriptpfad3 = " + tablename + ".typAutomatischScriptpfad3,");
        joinQuery.append(" scriptName4 = " + tablename + ".scriptName4,");
        joinQuery.append(" typAutomatischScriptpfad4 = " + tablename + ".typAutomatischScriptpfad4,");
        joinQuery.append(" scriptName5 = " + tablename + ".scriptName5,");
        joinQuery.append(" typAutomatischScriptpfad5 = " + tablename + ".typAutomatischScriptpfad5,");
        joinQuery.append(" batchStep = " + tablename + ".batchStep,");
        joinQuery.append(" stepPlugin = " + tablename + ".stepPlugin,");
        joinQuery.append(" validationPlugin = " + tablename + ".validationPlugin");
        joinQuery.append(" WHERE schritte.SchritteID= " + tablename + ".SchritteID;");

        String deleteTempTable = "DROP TEMPORARY TABLE " + tablename + ";";

        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            // create temporary table
            run.update(connection, tempTable);
            // insert bulk into a temp table
            run.batch(connection, insertQuery, paramArray);
            // update process table using join
            run.update(connection, joinQuery.toString());
            // delete temporary table
            run.update(connection, deleteTempTable);
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static void insertBatchStepList(List<Step> stepList) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO schritte " + generateInsertQuery(false));
        List<Object[]> paramArray = new ArrayList<Object[]>();
        for (Step o : stepList) {
            sql.append(" " + generateValueQuery(false) + ",");
            Object[] param = generateParameter(o, false);
            paramArray.add(param);
        }
        String values = sql.toString();

        values = values.substring(0, values.length() - 1);

        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            QueryRunner run = new QueryRunner();
            run.update(connection, values, paramArray);
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static void main(String[] args) throws SQLException {

        Step s76 = StepMysqlHelper.getStepById(76);
        Step s77 = StepMysqlHelper.getStepById(77);
        Step s78 = StepMysqlHelper.getStepById(78);
        Step s79 = StepMysqlHelper.getStepById(79);
        Step s42338 = StepMysqlHelper.getStepById(42338);
        Step s83 = StepMysqlHelper.getStepById(83);
        Step s85 = StepMysqlHelper.getStepById(85);
        Step s61351 = StepMysqlHelper.getStepById(61351);
        Step s335310 = StepMysqlHelper.getStepById(335310);
        Step s84 = StepMysqlHelper.getStepById(84);
        Step s216 = StepMysqlHelper.getStepById(216);
        Step s217 = StepMysqlHelper.getStepById(217);
        Step s316611 = StepMysqlHelper.getStepById(316611);
        Step s345846 = StepMysqlHelper.getStepById(345846);

        List<Step> stepList = new ArrayList<Step>();
        stepList.add(s76);
        stepList.add(s77);
        stepList.add(s78);
        stepList.add(s79);
        stepList.add(s42338);
        stepList.add(s83);
        stepList.add(s85);
        stepList.add(s61351);
        stepList.add(s335310);
        stepList.add(s84);
        stepList.add(s216);
        stepList.add(s217);
        stepList.add(s316611);
        stepList.add(s345846);

        StepMysqlHelper.updateBatchList(stepList);

    }

    public static List<Integer> getIDList(String filter) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SchritteId FROM schritte");
        if (filter != null && !filter.isEmpty()) {
            sql.append(" WHERE " + filter);
        }

        try {
            logger.debug(sql.toString());
            List<Integer> ret = null;
            ret = new QueryRunner().query(connection, sql.toString(), MySQLHelper.resultSetToIntegerListHandler);

            return ret;
        } finally {
            MySQLHelper.closeConnection(connection);
        }

    }

    public static List<String> getDistinctStepTitles() throws SQLException {
        String sql = "select distinct titel from schritte";
        Connection connection = MySQLHelper.getInstance().getConnection();
        try {
            logger.debug(sql.toString());
            return new QueryRunner().query(connection, sql.toString(), MySQLHelper.resultSetToStringListHandler);

        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static void saveUserAssignment(Step step) throws SQLException {
        if (step.getId() != null) {
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                for (User user : step.getBenutzer()) {
                    // check if assignment exists
                    String sql = " SELECT * from schritteberechtigtebenutzer WHERE BenutzerID =" + user.getId() + " AND schritteID = " + step.getId();
                    boolean exists = new QueryRunner().query(connection, sql.toString(), checkForResultHandler);
                    if (!exists) {
                        String insert =
                                " INSERT INTO schritteberechtigtebenutzer (BenutzerID , schritteID) VALUES (" + user.getId() + "," + step.getId()
                                        + ")";
                        new QueryRunner().update(connection, insert);
                    }
                }
            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    private static void saveUserGroupAssignment(Step step) throws SQLException {
        if (step.getId() != null) {
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                for (Usergroup userGroup : step.getBenutzergruppen()) {
                    // check if assignment exists
                    String sql =
                            " SELECT * from schritteberechtigtegruppen WHERE BenutzerGruppenID =" + userGroup.getId() + " AND schritteID = "
                                    + step.getId();
                    boolean exists = new QueryRunner().query(connection, sql.toString(), checkForResultHandler);
                    if (!exists) {
                        String insert =
                                " INSERT INTO schritteberechtigtegruppen (BenutzerGruppenID , schritteID) VALUES (" + userGroup.getId() + ","
                                        + step.getId() + ")";
                        new QueryRunner().update(connection, insert);
                    }
                }
            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }

    }

    public static void removeUsergroupFromStep(Step step, Usergroup usergroup) throws SQLException {
        if (step.getId() != null) {
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                String sql =
                        "DELETE FROM schritteberechtigtegruppen WHERE BenutzerGruppenID =" + usergroup.getId() + " AND schritteID = " + step.getId();

                new QueryRunner().update(connection, sql);

            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    public static void removeUserFromStep(Step step, User user) throws SQLException {
        if (step.getId() != null) {
            Connection connection = MySQLHelper.getInstance().getConnection();
            try {
                String sql = "DELETE FROM schritteberechtigtebenutzer WHERE BenutzerID =" + user.getId() + " AND schritteID = " + step.getId();

                new QueryRunner().update(connection, sql);

            } finally {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    public static void addHistory(Date date, double order, String value, int type, int processId) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        Timestamp datetime = new Timestamp(date.getTime());

        try {
            QueryRunner run = new QueryRunner();
            // String propNames = "numericValue, stringvalue, type, date, processId";
            Object[] param = { order, value, type, datetime, processId };
            String sql = "INSERT INTO " + "history" + " (numericValue, stringvalue, type, date, processId) VALUES ( ?, ?, ?, ? ,?)";
            logger.trace("added history event " + sql + ", " + param);
            run.update(connection, sql, param);
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }

    public static List<String> getScriptsForStep(int stepId) throws SQLException {
        Connection connection = MySQLHelper.getInstance().getConnection();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM schritte WHERE SchritteID = ? ");
        try {
            Object[] params = { stepId };
            logger.debug(sql.toString() + ", " + stepId);
            List<String> ret = new QueryRunner().query(connection, sql.toString(), resultSetToScriptsHandler, params);
            return ret;
        } finally {
            MySQLHelper.closeConnection(connection);
        }
    }
    
    public static ResultSetHandler<List<String>> resultSetToScriptsHandler = new ResultSetHandler<List<String>>() {
        @Override
        public List<String> handle(ResultSet rs) throws SQLException {
            List<String> answer = new ArrayList<String>();
            if (rs.next()) {
                if (rs.getString("typAutomatischScriptpfad") != null && rs.getString("typAutomatischScriptpfad").length() > 0) {
                    answer.add(rs.getString("typAutomatischScriptpfad"));
                }
                if (rs.getString("typAutomatischScriptpfad2") != null && rs.getString("typAutomatischScriptpfad2").length() > 0) {
                    answer.add(rs.getString("typAutomatischScriptpfad2"));
                }
                if (rs.getString("typAutomatischScriptpfad3") != null && rs.getString("typAutomatischScriptpfad3").length() > 0) {
                    answer.add(rs.getString("typAutomatischScriptpfad3"));
                }
                if (rs.getString("typAutomatischScriptpfad4") != null && rs.getString("typAutomatischScriptpfad4").length() > 0) {
                    answer.add(rs.getString("typAutomatischScriptpfad4"));
                }
                if (rs.getString("typAutomatischScriptpfad5") != null && rs.getString("typAutomatischScriptpfad5").length() > 0) {
                    answer.add(rs.getString("typAutomatischScriptpfad5"));
                }
            }
            return answer;
        }
    };
}
