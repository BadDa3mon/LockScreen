package badda3mon.lockscreen.additional;

import android.util.Log;
import badda3mon.lockscreen.models.Problem;

import java.util.Random;

public class ProblemGenerator {

    public static Problem generate(int level){
        Random random = new Random();

        int firstNumber, secondNumber, operationValue, acceptAnswer;
        String operationMark;

        if (level == 1){
            firstNumber = random.nextInt(11);
            secondNumber = random.nextInt(10 - firstNumber);

            operationValue = random.nextInt(2); // 0 -> +, 1 -> -

            if (operationValue == 0) acceptAnswer = firstNumber + secondNumber;
            else {
                while (firstNumber < secondNumber) secondNumber = random.nextInt(10 - firstNumber);

                acceptAnswer = firstNumber - secondNumber;
            }
        } else if (level == 2){
            firstNumber = random.nextInt(51);
            secondNumber = random.nextInt(50 - firstNumber);

            operationValue = random.nextInt(2); // 0 -> +, 1 -> -

            if (operationValue == 0) acceptAnswer = firstNumber + secondNumber;
            else {
                while (firstNumber < secondNumber) secondNumber = random.nextInt(50 - firstNumber);

                acceptAnswer = firstNumber - secondNumber;
            }
        } else if (level == 3){
            operationValue = random.nextInt(4); // 0 -> +, 1 -> -,2 -> /, 3 -> *

            if (operationValue == 0 || operationValue == 1) {
                firstNumber = random.nextInt(21);
                secondNumber = random.nextInt(20 - firstNumber);

                if (operationValue == 0) acceptAnswer = firstNumber + secondNumber;
                else {
                    while (firstNumber < secondNumber) secondNumber = random.nextInt(20 - firstNumber);

                    acceptAnswer = firstNumber - secondNumber;
                }
            } else if (operationValue == 2){
                firstNumber = 0;
                secondNumber = 0;

                while (secondNumber == 0) {
                    secondNumber = random.nextInt(6);
                    firstNumber = random.nextInt(6) * secondNumber;
                }

                acceptAnswer = firstNumber / secondNumber;
            } else { //operationValue = 3
                firstNumber = random.nextInt(11);
                secondNumber = 20 / firstNumber;

                acceptAnswer = firstNumber * secondNumber;
            }
        } else if (level == 4){
            operationValue = random.nextInt(4); // 0 -> +, 1 -> -,2 -> /, 3 -> *

            if (operationValue == 0 || operationValue == 1) {
                firstNumber = random.nextInt(51);
                secondNumber = random.nextInt(50 - firstNumber);

                if (operationValue == 0) acceptAnswer = firstNumber + secondNumber;
                else {
                    while (firstNumber < secondNumber) secondNumber = random.nextInt(50 - firstNumber);

                    acceptAnswer = firstNumber - secondNumber;
                }
            } else if (operationValue == 2){
                secondNumber = 0;
                firstNumber = 0;

                while (secondNumber == 0) {
                    secondNumber = random.nextInt(8);
                    firstNumber = random.nextInt(8) * secondNumber;
                }

                acceptAnswer = firstNumber / secondNumber;
            } else { //operationValue = 3
                firstNumber = random.nextInt(26);
                secondNumber = 50 / firstNumber;

                acceptAnswer = firstNumber * secondNumber;
            }
        } else {
            operationValue = random.nextInt(4); // 0 -> +, 1 -> -,2 -> /, 3 -> *

            if (operationValue == 0 || operationValue == 1) {
                firstNumber = random.nextInt(101);
                secondNumber = random.nextInt(101 - firstNumber);

                if (operationValue == 0) acceptAnswer = firstNumber + secondNumber;
                else {
                    while (firstNumber < secondNumber) secondNumber = random.nextInt(101 - firstNumber);

                    acceptAnswer = firstNumber - secondNumber;
                }
            } else if (operationValue == 2){
                firstNumber = 0;
                secondNumber = 0;

                while (secondNumber == 0) {
                    secondNumber = random.nextInt(10);
                    firstNumber = random.nextInt(10) * secondNumber;
                }

                acceptAnswer = firstNumber / secondNumber;
            } else { //operationValue = 3
                firstNumber = random.nextInt(51);
                secondNumber = 100 / firstNumber;

                acceptAnswer = firstNumber * secondNumber;
            }
        }

        operationMark = getOperationMarkByType(operationValue);

        return new Problem(operationMark, getProblemString(firstNumber, secondNumber, operationValue), acceptAnswer);
    }

    private static String getProblemString(int firstNumber, int secondNumber, int operationValue){
        String operationMark = getOperationMarkByType(operationValue);
        String formatProblem = "%s %1s %2s";

        return String.format(formatProblem, firstNumber, operationMark, secondNumber);
    }

    private static String getOperationMarkByType(int type){
        String operationMark;

        switch (type){
            case 0: operationMark = "+"; break;
            case 1: operationMark = "-"; break;
            case 2: operationMark = "/"; break;
            default: operationMark = "*"; break;
        }

        return operationMark;
    }
}
