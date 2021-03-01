package covid;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class Vaccination {

    public void VaccinationCitizen() {
        try(PreparedStatement preparedStatement =
                    new TbdDAO().getDs().getConnection().prepareStatement("SELECT * FROM citizens LEFT JOIN vaccinations ON citizen_id = vaccinations.citizen_id_f WHERE number_of_vaccination = 0 OR (number_of_vaccination > 0 AND number_of_vaccination < 2 AND last_vaccination < ?) ORDER BY zip, age DESC, citizen_name, number_of_vaccination ASC LIMIT 1")) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusDays(15)));
            ResultSet res = preparedStatement.executeQuery();

            if (res.next()) {
                int id = res.getInt(1);
                String name = res.getString("citizen_name");
                int zipCode = res.getInt("zip");
                byte age = res.getByte("age");
                String email = res.getString("email");
                String taj = res.getString("taj");
                byte numOfVaccs = res.getByte("number_of_vaccination");
                Timestamp lastVaccination = Optional.ofNullable(res.getTimestamp("last_vaccination")).orElse(Timestamp.valueOf(LocalDateTime.of(2037, 1, 1, 0, 0)));
                LocalDateTime lastVaccDateTime = lastVaccination.toLocalDateTime();
                String vaccTypeStr = res.getString("vaccination_type");
                VaccineType vaccineType = null;
                if (vaccTypeStr != null) {
                    vaccineType = VaccineType.valueOf(vaccTypeStr);
                }
                Citizen citizen = new Citizen(id, name, zipCode, age, email, taj, numOfVaccs, lastVaccDateTime, vaccineType);
                System.out.println("Kiválasztva: " + citizen.getFullName());
                if (citizen.getNumberOfVaccination() == 0) {
                    getFirstVaccinatonForCitizen(citizen);
                } else {
                    getFirstVaccinatonForCitizen(citizen);

                }
            } else {
                System.out.println("Gratulálunk! Sikeresen be lett oltva mindenki,vagy nincs olyan,akit be lehetne oltani...MÉG! (Eddig)\n");
            }
    } catch (SQLException sqlException) {
            throw new IllegalStateException("", sqlException);
        }
}

    private void updateCitizen(Citizen citizen) {
        try(PreparedStatement preparedStatement =
                    new TbdDAO().getDs().getConnection().prepareStatement("UPDATE `citizens` SET number_of_vaccination = number_of_vaccination + 1, last_vaccination = NOW() WHERE citizen_id = ?")) {
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
        throw new IllegalStateException("", sqlException);
    }
    }


    private void doVaccination(Citizen citizen) {
        try (Connection connection = new TbdDAO().getDs().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO vaccinations (citizen_id_f, vaccination_date, `status`, note, vaccination_type) VALUES (?, ?, ?, ?, ?)")) {
            LocalDateTime dateTime = LocalDateTime.now();
            VaccineType vaccineType;
            if (citizen.getNumberOfVaccination() == 0) {
                vaccineType = chooseVaccine();
            } else {
                vaccineType = citizen.getVaccineType();
                System.out.println(citizen.getVaccineType().toString());
            }
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTime));
            preparedStatement.setString(3, Status.values()[citizen.getNumberOfVaccination() + 1].toString());
            System.out.print("Megjegyzés: ");
            preparedStatement.setString(4, new Scanner(System.in).nextLine() + "\n");
            preparedStatement.setString(5, vaccineType.toString());
            //preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();
            System.out.println("Sikeres beoltás.\n");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Can't vaccination ciziten. DB error.", sqlException);
        }
    }

    public void getVaccineType(Citizen citizen) {

    }

    // A taj szám érvényes,ha benne van az adatbázisban,nem kell külön ellenőrizni
public void getFirstVaccinatonForCitizen(Citizen citizen) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("TAJ szám: ");
    String taj = scanner.nextLine();
    try(PreparedStatement preparedStatement =
                new TbdDAO().getDs().getConnection().prepareStatement("SELECT taj FROM citizens WHERE taj = ?")) {
        preparedStatement.setString(1, taj);
        ResultSet res = preparedStatement.executeQuery();
        if (res.next()) {
            if (!res.getString("taj").equals(citizen.getTajId())) {
                throw new IllegalStateException("TAJ IDs doesn't match");
            }
            doVaccination(citizen);
            updateCitizen(citizen);
        }
    } catch (SQLException sqlException) {
        throw new IllegalStateException("", sqlException);
    }

}

    /*public void dosecondVaccinationForCitizen(Citizen citizen) {
        doVaccination(citizen.getId());
        try(PreparedStatement preparedStatement =
                    new TbdDAO().getDs().getConnection().prepareStatement("UPDATE vaccinations SET status = YES WHERE citizen_id_f = ?")) {
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.executeUpdate();
            updateCitizen(citizen);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("", sqlException);
        }
    }
     */

    private VaccineType chooseVaccine() {
    int item = -1;
    VaccineType[] vaccineTypes = VaccineType.values();
    try {
        while (item != 0) {
            for (int i = 0; i < vaccineTypes.length; i++) {
                System.out.println(i + 1 + ". " + vaccineTypes[i].toString());
            }
            System.out.println(0 + ". Kilépés");
            System.out.print("\nVálassz menüpontot: ");
            Scanner scanner = new Scanner(System.in);
            item = Integer.parseInt(scanner.nextLine());
            if (item < 0 || item > vaccineTypes.length) {
                throw new IllegalMenuItemException("Hibás szám.");
            }
            if (item == 0) {
                System.out.println("Köszönjük!");
                break;
            }
            System.out.println("\n" + vaccineTypes[item - 1].toString());
            return vaccineTypes[item - 1];
        }
    } catch (NumberFormatException | IllegalMenuItemException e) {
        System.out.println("Hibás adatbevitel. Kérlek válassz újra: \n");
        chooseVaccine();
    }
        return null;
    }


}

