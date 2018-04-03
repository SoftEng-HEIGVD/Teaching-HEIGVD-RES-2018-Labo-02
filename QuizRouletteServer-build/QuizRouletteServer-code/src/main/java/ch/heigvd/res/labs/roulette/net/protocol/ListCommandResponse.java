/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;
import java.util.List;

/**
 *
 * @author Jimmy Verdasca
 */
public class ListCommandResponse {
   Student[] students;
   
   public ListCommandResponse(Student[] students) {
      this.students = students;
   }

   public Student[] getStudents() {
      return students;
   }

   public void setStudents(Student[] students) {
      this.students = students;
   }
   
   
}
