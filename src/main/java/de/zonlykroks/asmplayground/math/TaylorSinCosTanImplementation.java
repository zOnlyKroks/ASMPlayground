package de.zonlykroks.asmplayground.math;

@SuppressWarnings("unused")
public class TaylorSinCosTanImplementation {

    /**
     * Computes sin(x) via Taylor series expansion around 0 up to x^17 term.
     */
    public static double taylorSin(double rad) {
        double x2 = rad * rad;
        double x3 = x2 * rad;
        double x5 = x2 * x3;
        double x7 = x2 * x5;
        double x9 = x2 * x7;
        double x11 = x2 * x9;
        double x13 = x2 * x11;
        double x15 = x2 * x13;
        double x17 = x2 * x15;

        double val = rad;
        val -= x3 * 0.16666666666666666666666666666667;      // 1/3!
        val += x5 * 0.00833333333333333333333333333333;      // 1/5!
        val -= x7 * 1.984126984126984126984126984127e-4;      // 1/7!
        val += x9 * 2.7557319223985890652557319223986e-6;      // 1/9!
        val -= x11 * 2.5052108385441718775052108385442e-8;    // 1/11!
        val += x13 * 1.6059043836821614599392377170155e-10;   // 1/13!
        val -= x15 * 7.6471637318198164759011319857881e-13;   // 1/15!
        val += x17 * 2.8114572543455207631989455830103e-15;   // 1/17!
        return val;
    }

    /**
     * Computes cos(x) via Taylor series expansion around 0 up to x^16 term.
     */
    public static double taylorCos(double rad) {
        double x2 = rad * rad;
        double x4 = x2 * x2;
        double x6 = x4 * x2;
        double x8 = x4 * x4;
        double x10 = x8 * x2;
        double x12 = x6 * x6;
        double x14 = x12 * x2;
        double x16 = x8 * x8;

        double val = 1.0;
        val -= x2 * 0.5;                                      // 1/2!
        val += x4 * 0.04166666666666666666666666666667;      // 1/4!
        val -= x6 * 0.00138888888888888888888888888889;      // 1/6!
        val += x8 * 2.4801587301587301587301587301587e-5;     // 1/8!
        val -= x10 * 2.7557319223985890652557319223986e-7;    // 1/10!
        val += x12 * 2.0876756987868098979210090321201e-9;    // 1/12!
        val -= x14 * 1.1470745597729724713851697978681e-11;   // 1/14!
        val += x16 * 4.779477332387385297438207491117e-14;    // 1/16!
        return val;
    }

    /**
     * Computes tan(x) as sin(x)/cos(x) using the Taylor implementations.
     */
    public static double taylorTan(double rad) {
        return taylorSin(rad) / taylorCos(rad);
    }
}
