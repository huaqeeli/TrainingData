package controllers;

import Validation.FormValidation;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFCell;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExporteExcelSheet {

    ResultSet rs;
    String[] feild;
    String[] titel;
    String[] personaltitel;
    String[] personalData;
    HSSFWorkbook workBook = new HSSFWorkbook();
    HSSFSheet sheet = workBook.createSheet();

    public ExporteExcelSheet(ResultSet rs, String[] feild, String[] titel) {
        this.rs = rs;
        this.feild = feild;
        this.titel = titel;
    }

    public ExporteExcelSheet() {
    }

    public ExporteExcelSheet(ResultSet rs, String[] feild, String[] titel, String[] personaltitel, String[] personalData) {
        this.rs = rs;
        this.feild = feild;
        this.titel = titel;
        this.personaltitel = personaltitel;
        this.personalData = personalData;
    }

    public ArrayList<Object[]> getTableData(ResultSet rs, String[] feild) throws IOException {
        ArrayList<Object[]> tableDataList = null;
        tableDataList = new ArrayList<>();
        try {
            while (rs.next()) {
                Object[] objArray = new Object[feild.length];
                for (int i = 0; i < feild.length; i++) {
                    if (rs.getString(feild[i]) == null) {
                        objArray[i] = "---";
                    } else {
                        objArray[i] = rs.getString(feild[i]);
                    }
                }
                tableDataList.add(objArray);
            }

        } catch (SQLException ex) {
           FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
        }

        return tableDataList;
    }

    public CellStyle setHederStyle() {
        CellStyle headerstyle = workBook.createCellStyle();
        headerstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerstyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerstyle.setAlignment(HorizontalAlignment.CENTER);
        headerstyle.setBorderBottom((short) 2);
        headerstyle.setBorderTop((short) 2);
        headerstyle.setBorderRight((short) 2);
        headerstyle.setBorderLeft((short) 2);
        return headerstyle;
    }

    public CellStyle setContentStyle() {
        CellStyle style = workBook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom((short) 2);
        style.setBorderTop((short) 2);
        style.setBorderRight((short) 2);
        style.setBorderLeft((short) 2);
        return style;
    }

    public void ceratHeader(String[] titel, int rownum, CellStyle style) {
        HSSFRow row = sheet.createRow(rownum);
        for (int i = 0; i < titel.length; i++) {
            HSSFCell cell = row.createCell((short) i);
            cell.setCellValue(titel[i]);
            cell.setCellStyle(style);
        }
    }

    public void ceratContent(ArrayList<Object[]> dataList, String[] feild, int rownum, CellStyle style) {
        short rowNo = (short) rownum;
        for (Object[] objects : dataList) {
            HSSFRow row = sheet.createRow(rowNo);
            for (int i = 0; i < feild.length; i++) {
                HSSFCell cell = row.createCell((short) i);
                cell.setCellValue(objects[i].toString());
                cell.setCellStyle(style);
            }
            rowNo++;
        }
    }

    public void writeFile(String fileName) {
        String file = fileName + ".xls";
        try {
            sheet.setRightToLeft(true);
            sheet.setDefaultColumnWidth(20);
            FileOutputStream fos = new FileOutputStream(file);
            workBook.write(fos);
            fos.flush();
        } catch (FileNotFoundException ex) {
            FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
        } catch (IOException ex) {
            FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
        }
    }

    public void doExport(ArrayList<Object[]> dataList, String fileName) {
        if (dataList != null && !dataList.isEmpty()) {
            sheet.setRightToLeft(true);
            sheet.setDefaultColumnWidth(20);

            ceratHeader(personaltitel, 0, setHederStyle());
            ceratHeader(personalData, 1, setContentStyle());
            ceratHeader(titel, 2, setHederStyle());
            ceratContent(dataList, feild, 3, setContentStyle());

            String file = fileName + ".xls";
            try {
                FileOutputStream fos = new FileOutputStream(file);
                workBook.write(fos);
                fos.flush();
            } catch (FileNotFoundException ex) {
               FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
            } catch (IOException ex) {
                FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
            }
        }
    }
}
