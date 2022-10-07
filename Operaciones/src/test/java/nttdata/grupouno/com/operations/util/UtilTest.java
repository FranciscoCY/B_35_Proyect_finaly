package nttdata.grupouno.com.operations.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class UtilTest {
    @Autowired
    Date dateRepresentation;

    @BeforeEach
    void init(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        dateRepresentation = cal.getTime();
    }

    @Test
    void dateToString() {
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        String resp = formatter.format(dateRepresentation);
        String h = Util.dateToString(dateRepresentation);
        assertEquals(resp, h);
    }

    @Test
    void getMonth() {
        String resp = Util.getMonth(dateRepresentation);
        assertEquals("01", resp);
    }

    @Test
    void getYear() {
        assertEquals("2022", Util.getYear(dateRepresentation));
    }

    @Test
    void stringToDate() {
        Date respo = Util.stringToDate("2022.01.01");
        assertEquals("2022", Util.getYear(respo));
    }

    @Test
    void generateCartNumber(){
        String data = Util.generateCartNumber();
        assertNotNull(data);
    }

    @Test
    void encriptAES(){
        String encript = Util.encriptAES("4214000000000001", "895b3346-5091-4465-b6ed-7b9f3b8411f8");
        assertEquals("os9FuU4Cs3M9+Ou7amrTOwe3ZfP/u7jEGbW1MQ4nokM=", encript);
    }

    @Test
    void desencriptAES(){
        String desencript = Util.desencriptAES("os9FuU4Cs3M9+Ou7amrTOwe3ZfP/u7jEGbW1MQ4nokM=", "895b3346-5091-4465-b6ed-7b9f3b8411f8");
        assertEquals("4214000000000001", desencript);
    }

    @Test
    void generateHash(){
        String hash = Util.generateHash("4214000000000001");
        assertEquals("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c", hash);
    }

    @Test
    void dateTimeToString(){
        String date = Util.dateTimeToString(dateRepresentation);
        assertEquals("2022.01.01 12.00.00", date);
    }

    @Test
    void stringToDateTime(){
        Date date = Util.stringToDateTime("2022.01.01 12.00.00");
        assertEquals(dateRepresentation, date);

        Date date2 = Util.stringToDateTime(null);
        assertEquals(null, date2);
    }
}