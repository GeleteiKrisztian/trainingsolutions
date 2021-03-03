package covid;


import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTest {

    @BeforeEach
    public void setUp() {
        Flyway flyway = Flyway.configure().dataSource(new TbdDAO().getDs()).load();
        flyway.clean();
        flyway.migrate();
        new Generation().importCities();
    }

    @Test
    void testRegisterOneValidCitizen() {
        byte age = 25;
        Citizen citizen = new Citizen("John Doe", "2011", age, "johndoe@gmail.com", "123456782");
        new Registration().regCitizen(citizen);
        assertEquals(1, new TbdDAO().readCitizensFromDB().size());
    }

    @Test
    void testRegisterOneNotValidCitizen() {
        byte age = 25;
        Registration reg = new Registration();
        Assertions.assertThrows(IllegalArgumentException.class, () -> { reg.regCitizen(new Citizen("JohnDoe", "2011", age, "johndoe@gmail.com", "123456782"));});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { reg.regCitizen(new Citizen("John Doe", "1995", (byte) 3, "johndoe@gmail.com", "123456782"));});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { reg.regCitizen(new Citizen("JohnDoe", "2011", age, "johndoe@gmail.com", "123456782"));});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { reg.regCitizen(new Citizen("JohnDoe", "2011", age, "johndoegmail.com", "123456782"));});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { reg.regCitizen(new Citizen("JohnDoe", "2011", age, "johndoe@gmail.com", "123456780"));});
        assertEquals(0, new TbdDAO().readCitizensFromDB().size());
    }

    @Test
    void wrongNameFormatThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.nameValidator("JohnDoe");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.nameValidator("john Doe");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.nameValidator("John doe");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.nameValidator("");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.nameValidator(null);});
    }

    @Test
    void wrongZipThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.zipValidator("1995");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.zipValidator("");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.zipValidator(null);});

    }

    @Test
    void wrongAgeThrows() {
        byte age = 3;
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.ageValidator(age);});
    }

    @Test
    void wrongEmailsTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator("gk@gmail.com");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator("gkgmail.com");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator("gk@gmailcom");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator("gkgmailcom");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator("");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailValidator(null);});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailsEqual(null, "gkc@gmail.com");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailsEqual("gkc@gmail.com", null);});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailsEqual(null, null);});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.emailsEqual("gkc@gmail.co", "gkc@gmail.com");});
        Assertions.assertEquals("gkc@gmail.com", Validator.emailsEqual("gkc@gmail.com", "gkc@gmail.com"));
    }

    @Test
    void wrongTajThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456780");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456781");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456783");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456784");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456785");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456786");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456787");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456788");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("123456789");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator("");});
        Assertions.assertThrows(IllegalArgumentException.class, () -> { Validator.tajValidator(null);});
    }

}