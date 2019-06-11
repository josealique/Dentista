import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DentistOffice {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        WaitingRoom waitingRoom = new WaitingRoom(10);
        Dentist dentist = new Dentist(waitingRoom);
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Patient patient = new Patient(i, waitingRoom);
            waitingRoom.addPatient(patient);
        }
        patients.forEach(es::execute);
        es.execute(dentist);
    }
}


class Patient implements Runnable {
    private int id;
    private WaitingRoom waitingRoom;

    Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;

        Patient patient = (Patient) o;

        if (getId() != patient.getId()) return false;
        return waitingRoom != null ? waitingRoom.equals(patient.waitingRoom) : patient.waitingRoom == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (waitingRoom != null ? waitingRoom.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}


class WaitingRoom {
    final List<Patient> patientList = new ArrayList<>();
    int capacity = 10;

    WaitingRoom(int capacity) {
        this.capacity = capacity;
    }

    void addPatient(Patient patient){
        synchronized (patientList) {
            if (patientList.size() <= capacity){
                patientList.add(patient);
            }
        }
    }

    public void removePatient(Patient p) {
        synchronized (patientList) {
            patientList.remove(p);
        }
    }

    public Patient getPatient() {
        return patientList.get(0);
    }
}

class Dentist implements Runnable {
    WaitingRoom waitingRoom;
    boolean isOperating = true;

    Dentist(WaitingRoom wr) {
        this.waitingRoom = wr;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    if (waitingRoom.patientList.size() == 0) {
                        isOperating = false;
                        System.out.println("Dentist Sleep");
                        return;
                    } else {
                        Patient p = waitingRoom.getPatient();
                        System.out.println("The Dentist operating the patient: "+ p.getId());
                        wait(5000);
                        System.out.println("Dentist finished operating " + p.getId());
                        System.out.println("-------------------------------------");
                        waitingRoom.removePatient(p);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       }
    }
}