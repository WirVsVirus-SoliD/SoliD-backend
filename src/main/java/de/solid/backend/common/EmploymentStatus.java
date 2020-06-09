package de.solid.backend.common;

/*
 * the list of available employment status
 * 
 */
public enum EmploymentStatus {
  Student("Student*in"), Schüler("Schüler*in"), Arbeitslos("Arbeitslos"), Angestellt("Angestellt");

  private String label;

  private EmploymentStatus(String label) {
    this.label = label;
  }

  public static EmploymentStatus fromString(String value) {
    switch (value.toLowerCase()) {
      case "student":
        return Student;
      case "schüler*in":
        return Schüler;
      case "arbeitslos":
        return Arbeitslos;
      default:
        return Angestellt;
    }
  }

  public String getLabel() {
    return label;
  }
}
