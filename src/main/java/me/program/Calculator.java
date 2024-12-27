package me.program;

public class Calculator {
    public static double calculatePositionSize(double accountBalance, double riskPercentage, double stopLoss) {
        double riskAmount = accountBalance * (riskPercentage / 100);
        return riskAmount / stopLoss;
    }
}