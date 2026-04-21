public class Employee {
    String name;
    double salary;
    int workHours;
    int hireYear;

    public Employee(String name, double salary, int workHours, int hireYear) {
        this.name = name;
        this.salary = salary;
        this.workHours = workHours;
        this.hireYear = hireYear;
    }

    public double tax() {
        if (salary < 1000) {
            return 0;
        }
        return salary * 0.03;
    }

    public double bonus() {
        if (workHours > 40) {
            return (workHours - 40) * 30;
        }
        return 0;
    }

    public double raiseSalary() {
        int currentYear = 2021;
        int yearsWorked = currentYear - hireYear;

        if (yearsWorked < 10) {
            return salary * 0.05;
        } else if (yearsWorked < 20) {
            return salary * 0.10;
        } else {
            return salary * 0.15;
        }
    }

    @Override
    public String toString() {
        double tax = tax();
        double bonus = bonus();
        double raise = raiseSalary();
        double salaryWithTaxAndBonus = salary - tax + bonus;
        double totalSalary = salaryWithTaxAndBonus + raise;

        return "Name: " + name + "\n"
                + "Salary: " + salary + "\n"
                + "Work Hours: " + workHours + "\n"
                + "Hire Year: " + hireYear + "\n"
                + "Tax: " + tax + "\n"
                + "Bonus: " + bonus + "\n"
                + "Salary Raise: " + raise + "\n"
                + "Salary with Tax and Bonus: " + salaryWithTaxAndBonus + "\n"
                + "Total Salary: " + totalSalary;
    }
}
